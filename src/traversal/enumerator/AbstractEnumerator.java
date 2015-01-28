package traversal.enumerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poset.Poset;
import poset.VectorClock;

public abstract class AbstractEnumerator implements Enumerator {
	protected Logger logger;
	Enumerator next = null;
	
	public AbstractEnumerator() {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	/* (non-Javadoc)
	 * @see traversal.enumerator.Enumerator#setNext(traversal.enumerator.Enumerator)
	 */
	@Override
	public void setNext(Enumerator next) {
		if (next != this) this.next = next;
	}
	
	/* (non-Javadoc)
	 * @see traversal.enumerator.Enumerator#enumerate(lattice.DefaultPoset, lattice.VectorClock)
	 */
	@Override
	public void enumerate(Poset poset, VectorClock G) {
		doEnumeration(poset, G);
		if (next != null) next.enumerate(poset, G);
	}
	
	protected abstract void doEnumeration(Poset poset, VectorClock G);
	
	/* (non-Javadoc)
	 * @see traversal.enumerator.Enumerator#close()
	 */
	@Override
	public void close() {
		doClose();
		if (next != null) next.close();
	}
	
	protected abstract void doClose();
	
	/* (non-Javadoc)
	 * @see traversal.enumerator.Enumerator#fork()
	 */
	public Enumerator fork() {
		Enumerator fork = new DummyForkedEnumerator(this);
		if (next != null) fork.setNext(next.fork());
		return fork;
	}
	
	private class DummyForkedEnumerator extends AbstractEnumerator {
		AbstractEnumerator parent;
		
		public DummyForkedEnumerator(AbstractEnumerator parent) {
			this.parent = parent;
		}

		@Override
		public Enumerator fork() {
			return new DummyForkedEnumerator(parent);
		}

		@Override
		protected void doEnumeration(Poset poset, VectorClock G) {
			this.parent.doEnumeration(poset, G);
		}

		@Override
		protected void doClose() { }
	}
	
}
