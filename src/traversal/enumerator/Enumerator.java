package traversal.enumerator;

import poset.Poset;
import poset.VectorClock;

public interface Enumerator {

	/**
	 * Set the next enumerator after current enumerator.
	 * @param next the next enumerator.
	 */
	public void setNext(Enumerator next);

	/**
	 * Start the enumeration. At the end of the enumeration, the next enumerator is called.
	 * @param poset the poset to enumerate.
	 * @param G the frontier to enumerate.
	 */
	public void enumerate(Poset poset, VectorClock G);

	/**
	 * Clean up for the enumerator.
	 */
	public void close();
	
	/**
	 * Get a forked enumerator for concurrent enumeration. A forked enumerator
	 * does not necessary to be thread-safe; it is enumerated independently by
	 * each thread, and then merge the result back to the parent enumerator
	 * by the method close(). The merge must be a thread-safe operation and 
	 * the forked enumerator should can be reused after it is closed.
	 * @return a forked enumerator for concurrent traverser.
	 */
	public Enumerator fork();

}