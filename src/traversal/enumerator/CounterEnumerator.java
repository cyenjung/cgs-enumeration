package traversal.enumerator;

import poset.Poset;
import poset.VectorClock;

public class CounterEnumerator extends AbstractEnumerator {
	private long count = 0;
	
	public long getCount() { return count; }
	
	protected void doEnumeration(Poset poset, VectorClock G) {
		++count;
	}
	
	@Override
	protected void doClose() {
		logger.info("# cuts: {}", count);
	}
	
	public Enumerator fork() {
		Enumerator fork = new ForkedCounter(this);
		if (next != null) fork.setNext(next.fork());
		return fork;
	}
	
	private class ForkedCounter extends AbstractEnumerator {
		CounterEnumerator parent;
		long counter = 0;
		
		ForkedCounter(CounterEnumerator parent) {
			this.parent = parent;
		}
		
		@Override
		protected void doEnumeration(Poset poset, VectorClock G) {
			++counter;
		}

		@Override
		protected void doClose() {
			synchronized (parent) {
				parent.count += counter;
				counter = 0;
			}
		}
	}
}
