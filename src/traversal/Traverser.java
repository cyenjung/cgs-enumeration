package traversal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poset.DefaultPoset;
import poset.Poset;
import traversal.concurrent.ConcurrentTraverser;
import traversal.enumerator.Enumerator;
import traversal.enumerator.NOPEnumerator;
import util.PerformanceMon;

public abstract class Traverser {
	private PerformanceMon monitor = new PerformanceMon();
	protected Logger logger;
	protected Enumerator enumerator;
	
	/**
	 * Add the new enumerator to the list of enumerators.
	 * @param newEnumerator the new enumerator.
	 */
	public void addEnumerator(Enumerator newEnumerator) {
		if (enumerator != null)	newEnumerator.setNext(enumerator);
		enumerator = newEnumerator;
	}
	
	/**
	 * Override the enumerator to the specified enumerator.
	 * @param newEnumerator the new enumerator.
	 */
	public void setEnumerator(Enumerator newEnumerator) {
		enumerator = newEnumerator;
	}

	/**
	 * Start the traversal of the given poset.
	 * @param poset the poset to traverse.
	 */
	public void traverse(Poset poset) {
		if (enumerator == null) enumerator = new NOPEnumerator();
		logger = LoggerFactory.getLogger(this.getClass());
		String classSimpleName = this.getClass().getSimpleName();
		if (this instanceof ConcurrentTraverser) {
			logger.info("{} started with {} threads.", classSimpleName, ((ConcurrentTraverser) this).getPoolSize());
		} else {
			logger.info("{} started.", classSimpleName);
		}
		monitor.start();
		doTraverse(poset);
		monitor.end();
		logger.info("{} ended.", this.getClass().getSimpleName());
		logger.info("{}", monitor.getStat());
		enumerator.close();
	}
	
	/**
	 * The implementation of traversal algorithm.
	 * @param poset the poset to traverse.
	 */
	protected abstract void doTraverse(Poset poset);
	
	/**
	 * Get the specific type of poset that is used by the traverser.
	 * @return a poset that is designed for the traverser.
	 */
	public Poset getPoset() {
		return new DefaultPoset();
	}

}