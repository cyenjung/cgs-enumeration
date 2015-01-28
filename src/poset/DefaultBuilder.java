package poset;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poset.DefaultPoset.Node;

public class DefaultBuilder implements Builder {
	private final Logger logger = LoggerFactory.getLogger(DefaultBuilder.class);
	private DefaultPoset poset;
	private int n; // width of the poset
	private List<Node> nodes;
	private List<List<Node>> chainsForBuilder;
	private Node[][] chains;    // used after the build of the poset
	private VectorClock[][] vClocks;
	
	@Override
	public void setPoset(Poset poset) {
		this.poset = (DefaultPoset) poset;
	}
	
	@Override
	public void beginBuild(int width) {
		logger.info("Initializing a poset with width: {}", width);
		n = width;
		// Initial data structures.
		nodes = new ArrayList<Node>();
		chainsForBuilder = new ArrayList<List<Node>>();
		for (int i = 0; i < n; ++i)
			chainsForBuilder.add(new ArrayList<Node>());
		VectorClock initVc = new VectorClock(n, 0);
		for (int i = 0; i < n; ++i) {
			VectorClock boundary = new VectorClock(n, -1);
			for (int j = 0; j <= i; ++j) boundary.set(j, 0);
			addNode(i, new Node(initVc, boundary, nodes.size()));
		}
	}

	@Override
	public void addVectorClock(int pid, VectorClock vc) {
		VectorClock boundary = new VectorClock(n);
		for (int i = 0; i < n; ++i) {
			if (i != pid)
				boundary.set(i, sizeOfChain(i) - 1);
			else
				boundary.set(i, sizeOfChain(i));
		}
		Node node = new Node(vc, boundary, nodes.size());
		addNode(pid, node);
		logger.trace("Inserted Node {} with vc: {}, boundary: {}", node.id, vc, boundary);
	}
	
	@Override
	public void endBuild() {
		// allocate arrays for storing the poset
		chains = new Node[n][];
		for (int i = 0; i < n; ++i)
			chains[i] = new Node[chainsForBuilder.get(i).size()];
		for (int i = 0; i < n; ++i)
			for (int j = 0; j < chainsForBuilder.get(i).size(); ++j)
				chains[i][j] = chainsForBuilder.get(i).get(j);
		// allocate arrays for storing vector clocks
		vClocks = new VectorClock[n][];
		for (int i = 0; i < n; ++i)
			vClocks[i] = new VectorClock[chainsForBuilder.get(i).size()];
		for (int i = 0; i < n; ++i)
			for (int j = 0; j < chainsForBuilder.get(i).size(); ++j)
				vClocks[i][j] = chains[i][j].vc;
		// store data into the poset
		poset.setChains(chains);
		poset.setNodes(nodes);
		poset.setVectorClocks(vClocks);
		// force release memory for garbage collector
		nodes = null;
		chainsForBuilder = null;
		chains = null;
		vClocks = null;
		poset = null;
	}

	/**
	 * Add a node to the specified process.
	 * @param pid The id of the process to which the node is inserted.
	 * @param node the node to insert.
	 */
	private void addNode(int pid, Node node) {
		nodes.add(node);
		chainsForBuilder.get(pid).add(node);
	}
	
	private int sizeOfChain(int pid) {
		return chainsForBuilder.get(pid).size();
	}
	
}
