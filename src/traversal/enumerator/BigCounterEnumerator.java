package traversal.enumerator;

import java.math.BigInteger;

import poset.Poset;
import poset.VectorClock;

public class BigCounterEnumerator extends AbstractEnumerator {
	private BigInteger bigVal = BigInteger.ZERO;
	private long count = 0;
	
	protected void doEnumeration(Poset poset, VectorClock G) {
		if (count == Long.MAX_VALUE) {
			bigVal = bigVal.add(BigInteger.valueOf(count));
			count = 0;
		}
		++count;
	}
	
	@Override
	protected void doClose() {
		bigVal = bigVal.add(BigInteger.valueOf(count));
		logger.info("# cuts: {}", bigVal);
	}
	
	public Enumerator fork() {
		Enumerator fork = new BigForkedCounter(this);
		if (next != null) fork.setNext(next.fork());
		return fork;
	}
	
	private class BigForkedCounter extends AbstractEnumerator {
		BigCounterEnumerator parent;
		BigInteger bigVal = BigInteger.ZERO;
		long count = 0;
		
		BigForkedCounter(BigCounterEnumerator parent) {
			this.parent = parent;
		}
		
		@Override
		protected void doEnumeration(Poset poset, VectorClock G) {
			if (count == Long.MAX_VALUE) {
				bigVal = bigVal.add(BigInteger.valueOf(count));
				count = 0;
			}
			++count;
		}

		@Override
		protected void doClose() {
			bigVal = bigVal.add(BigInteger.valueOf(count));
			count = 0;
			synchronized (parent) {
				parent.bigVal = parent.bigVal.add(bigVal);
			}
			bigVal = BigInteger.ZERO;
		}
	}
}
