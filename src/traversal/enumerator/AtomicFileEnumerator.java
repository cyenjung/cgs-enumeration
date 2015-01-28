package traversal.enumerator;

import poset.Poset;
import poset.VectorClock;

public class AtomicFileEnumerator extends FileEnumerator {
	public AtomicFileEnumerator() {
		super();
	}
	
	public AtomicFileEnumerator(String fileName) {
		super(fileName);
	}
	
	@Override
	protected synchronized void doEnumeration(Poset poset, VectorClock G) {
		logger.debug("{} is writting a frontier to a file.", this.getClass());
		if (fout != null) fout.println(G.toString());
	}

}
