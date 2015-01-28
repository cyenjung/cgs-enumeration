package poset;


public interface Builder {
	
	/**
	 * Specify the poset on which the builder should work on. 
	 * @param poset Builder will add events into the poset.
	 */
	void setPoset(Poset poset);

	/**
	 * The method is invoked before starting to build the poset. 
	 * @param width The number of processes in the poset.
	 */
	void beginBuild(int width);

	/**
	 * Add a new event into the poset.
	 * @param pid The process to which the event belongs.
	 * @param vc The vector clock of the new event.
	 */
	void addVectorClock(int pid, VectorClock vc);

	/**
	 * The method is invoked at the end of building.
	 */
	void endBuild();

}
