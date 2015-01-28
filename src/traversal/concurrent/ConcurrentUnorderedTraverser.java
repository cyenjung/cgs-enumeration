package traversal.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import poset.DefaultPoset;
import poset.Poset;
import poset.VectorClock;
import traversal.enumerator.Enumerator;
import traversal.sequential.BoundedTraverser;
import traversal.sequential.*;

public class ConcurrentUnorderedTraverser extends ConcurrentTraverser {
	
	@Override
	protected void doTraverse(Poset gPoset) {
		DefaultPoset poset = (DefaultPoset) gPoset;
		logger.debug("{} nodes to process.", poset.getNodeCount());
		
		AtomicInteger ticket = new AtomicInteger(poset.getNodeCount()-1);
		final ExecutorService pool = Executors.newFixedThreadPool(poolSize);
		for (int i = 0; i < poolSize; ++i) {
			pool.submit(new Worker(poset, ticket, enumerator.fork()));
		}
		pool.shutdown();
		try {
			if (!pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
				logger.warn("Terminating thread pool.");
				pool.shutdownNow();
				if (!pool.awaitTermination(1, TimeUnit.SECONDS))
					logger.error("Thread pool did not terminate.");
			}
		} catch (InterruptedException e) {
			logger.warn("Terminating thread pool.");
			pool.shutdownNow();
		}
	}

	private class Worker implements Runnable {
		final private DefaultPoset poset;
		final private AtomicInteger ticket;
		final private BoundedTraverser boundedTraverser;
		final private Enumerator enumerator;
		
		public Worker(DefaultPoset poset, AtomicInteger ticket, Enumerator enumerator) {
			this.poset = poset;
			this.ticket = ticket;
			this.boundedTraverser = new LexTraverser();
			this.enumerator = enumerator;
			this.boundedTraverser.setEnumerator(enumerator);
		}

		@Override
		public void run() {
			while (true) {
				// get the node to process in the reversed order
				int idx = ticket.getAndDecrement();
				if (idx < 0) break;
				
				// get the least consistent cut "l" of the node
				final VectorClock l = poset.getNodeLeastCut(idx);
				// get the boundary cut of the node
				final VectorClock m = poset.getNodeBoundary(idx);
				
				if (l.equals(m)) { enumerator.enumerate(poset, l); }
				else if (l.leq(m)) boundedTraverser.boundedTraverse(poset, l, m);
			}
			enumerator.close();
		}
		
	}
}
