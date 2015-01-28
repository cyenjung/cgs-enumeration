package traversal.concurrent;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poset.DefaultPoset;
import poset.Poset;
import poset.VectorClock;
import traversal.enumerator.Enumerator;

public class ConcurrentBFSTraverser extends ConcurrentTraverser {
	private Logger logger = LoggerFactory.getLogger(ConcurrentBFSTraverser.class);

	@Override
	public void doTraverse(Poset gPoset) {
		DefaultPoset poset = (DefaultPoset) gPoset;
		@SuppressWarnings("unchecked")
		final Queue<VectorClock> ques[] = new Queue[2];
		for (int i = 0; i < 2; ++i) ques[i] = new ConcurrentLinkedQueue<VectorClock>();
		ques[0].add(new VectorClock(poset.width(), 0));
		final AtomicInteger curQue = new AtomicInteger(0);
		final CyclicBarrier barrier = new CyclicBarrier(poolSize, new Tripper(ques, curQue));
		final ExecutorService pool = Executors.newFixedThreadPool(poolSize);
		for (int i = 0; i < poolSize; ++i) {
			pool.submit(new Worker(poset, ques, curQue, barrier, enumerator.fork()));
		}
		pool.shutdown();
		try {
			if (!pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
				pool.shutdownNow();
				if (!pool.awaitTermination(1, TimeUnit.SECONDS))
					logger.error("Thread pool did not terminate.");
			}
		} catch (InterruptedException e) {
			pool.shutdownNow();
			logger.warn("Thread pool is forced to shutdown.");
		}
	}

	private class Worker implements Runnable {
		private final boolean isTraceEnabled = logger.isTraceEnabled();
		private final DefaultPoset poset;
		private final Queue<VectorClock>[] ques;
		private final AtomicInteger curQue;
		private final int n;
		private final CyclicBarrier barrier;
		private final Enumerator enumerator;
		
		public Worker(DefaultPoset poset, Queue<VectorClock>[] ques,
				AtomicInteger curQue, CyclicBarrier barrier,
				Enumerator enumerator) {
			this.poset = poset;
			this.ques = ques;
			this.curQue = curQue;
			this.barrier = barrier;
			this.n = poset.width();
			this.enumerator = enumerator;
		}

		@Override
		public void run() {
			
			while(true) {
				if (curQue.get() < 0) {
					if (isTraceEnabled) {
						logger.trace("Thread {} is leaving traversing job.", Thread.currentThread().getName());
					}
					break;
				}
				int curQueTemp = curQue.get();
				VectorClock G = ques[curQueTemp].poll();
				try {
					if (G == null) {
						barrier.await();
						if (isTraceEnabled) {
							logger.trace(
									"Thread {} is leaving the barrier and curQue = {}.",
									Thread.currentThread().getName(), curQueTemp);
						}
						continue;
					}
				} catch (InterruptedException e) {
					logger.warn("Thread in the barrier is interrupted: {}", Arrays.toString(e.getStackTrace()));
					break;
				} catch (BrokenBarrierException e) {
					logger.warn("The barrier in {} is broken: {}", ConcurrentBFSTraverser.class, Arrays.toString(e.getStackTrace()));
					break;
				}
				
				enumerator.enumerate(poset, G); // evaluate predicate
				
				for (int i = 0; i < n; ++i) {
					if (!poset.isNextEnabled(G, i)) continue;
					boolean hasDuplicate = false;
					for (int j = 0; j < n; ++j) {
						if (i == j) continue;
						if (poset.getVectorClock(i, G.get(i)+1).isConcurrentWith(poset.getVectorClock(j, G.get(j)))) {
							if (poset.getId(i, G.get(i)+1) < poset.getId(j, G.get(j)))
								hasDuplicate = true;
						}
					}
					if (!hasDuplicate) {
						VectorClock H = VectorClock.copyOf(G);
						H.set(i, G.get(i)+1);
						if (isTraceEnabled) logger.trace("Adding a frontier to next level of que.");
						ques[1-curQueTemp].add(H);
					}
				}
			}
			enumerator.close();
		}
	}
	
	private class Tripper implements Runnable {
		private final boolean isTraceEnabled = logger.isTraceEnabled();
		private Queue<VectorClock>[] ques;
		private AtomicInteger curQue;
		
		public Tripper (Queue<VectorClock>[] ques, AtomicInteger curQue) {
			this.ques = ques;
			this.curQue = curQue;
		}
		
		@Override
		public void run() {
			if (isTraceEnabled) logger.trace("Tripper triggered.");
			if (ques[1-curQue.get()].isEmpty()) {
				curQue.set(-1);
			} else curQue.set(1 - curQue.get());
		}
		
	}
}
