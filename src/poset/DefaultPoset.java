package poset;

import java.util.List;

/**
 * Poset is a 2D array of nodes, in which each node holds a vector clock.
 * 
 * @author Yen-Jung Chang
 * 
 */
public class DefaultPoset implements Poset {
	private Node[][] chains;    // 2D array that stores the poset.
	private List<Node> nodes;   // a linear expansion of the events in the poset.
	private VectorClock[][] vClocks;

	/**
	 * Returns the number of processes.
	 * 
	 * @return number of total processes.
	 */
	public int width() {
		return chains.length;
	}

	/**
	 * Returns the number of the nodes for the specified process
	 * 
	 * @param pid
	 *            id of the process
	 * @return number of nodes
	 */
	public int sizeOfChain(int pid) {
		return chains[pid].length;
	}

	/**
	 * Returns the vector clock of the specific node
	 * 
	 * @param pid
	 *            process id
	 * @param nidx
	 *            index to the node.
	 * @return the vector clock of the specific node
	 */
	public VectorClock getVectorClock(int pid, int nidx) {
		return vClocks[pid][nidx];
	}


	/**
	 * Returns the clock value of a target process P, which is stored on a
	 * specified node whose process is P.
	 * 
	 * @param pid
	 *            the process that owns the node.
	 * @param nidx
	 *            index to the node.
	 * @param ppid
	 *            id of the target process.
	 * @return
	 */
	public int getClock(int pid, int nidx, int ppid) {
		return vClocks[pid][nidx].get(ppid);
	}
	
	@Override
	public Builder getBuilder() {
		Builder builder = new DefaultBuilder();
		builder.setPoset(this);
		return builder;
	}

	/***************** BFS related methods ******************/
	/**
	 * Returns if the next node is enabled according to vector clock.
	 * 
	 * @param G
	 *            current cut.
	 * @param pid
	 *            the process to advance.
	 * @return False if the next node is not enabled or the next node exceed the
	 *         boundary.
	 */
	public boolean isNextEnabled(VectorClock G, int pid) {
		if (G.get(pid) + 1 >= sizeOfChain(pid))
			return false;

		VectorClock e = getVectorClock(pid, G.get(pid) + 1);
		return (isConsistentToFrontier(G, pid, e));
	}

	/**
	 * Check if vector clock E is pairwise consistent with all the vector clocks
	 * on G, excepts the one with id - pid.
	 * 
	 * @param G
	 *            the specified frontier.
	 * @param pid
	 *            the process id that the vector clock belongs to.
	 * @param E
	 *            the vector clock.
	 * @return True if the vector clock is pairwise consistent to the frontier.
	 */
	private boolean isConsistentToFrontier(VectorClock G, int pid, VectorClock E) {
		int n = width();
		for (int i = 0; i < n; ++i) {
			if (i != pid && getClock(i, G.get(i), i) < E.get(i))
				return false;
		}
		return true;
	}

	/**
	 * Returns if the frontier G is consistent upon this poset.
	 * 
	 * @param G
	 *            the specified frontier.
	 * @return True if the frontier is consistent upon this poset.
	 */
	public boolean isConsistentCut(VectorClock G) {
		int n = width();
		if (G.size() != n)
			return false;
		for (int i = 0; i < n; ++i)
			if (G.get(i) >= sizeOfChain(i))
				return false;
		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j < n; ++j) {
				if (getClock(i, G.get(i), i) < getClock(j, G.get(j), i)
						|| getClock(j, G.get(j), j) < getClock(i, G.get(i), j))
					return false;
			}
		}
		return true;
	}
	
	/************ Builder related methods ************/
	void setChains(Node[][] chains) {
		this.chains = chains;
	}
	
	void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	void setVectorClocks(VectorClock[][] vClocks) {
		this.vClocks = vClocks;
	}

	/************ Node related methods ***************/
	/**
	 * Gets the number of nodes in the poset.
	 * 
	 * @return the number of nodes.
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * Returns the id of the specified node.
	 * 
	 * @param pid
	 *            process id.
	 * @param idx
	 *            index to the node.
	 * @return node id.
	 */
	public int getId(int pid, int idx) {
		return chains[pid][idx].id;
	}

	/**
	 * Return the least cut of the node.
	 * @param id the node id.
	 * @return A Frontier that represents the least cut of the node.
	 */
	public VectorClock getNodeLeastCut(int id) {
		return nodes.get(id).vc;
	}

	/**
	 * Return the boundary cut of the node.
	 * @param id the node id.
	 * @return A Frontier that represents the boundary cut of the node.
	 */
	public VectorClock getNodeBoundary(int id) {
		return nodes.get(id).boundary;
	}

	/**
	 * Node is a container that stores vector clock and the total order of the
	 * vector clock.
	 * 
	 * @author Yen-Jung Chang
	 * 
	 */
	static class Node {
		// a linear extension id. 
		VectorClock vc;
		// the boundary to traversal this node in a concurrent fashion.
		VectorClock boundary;
		int id;

		public Node(VectorClock vc, VectorClock boundary, int id) {
			this.vc = vc;
			this.boundary = boundary;
			this.id = id;
		}
	}
}
