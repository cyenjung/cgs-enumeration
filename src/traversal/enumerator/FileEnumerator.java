package traversal.enumerator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import poset.Poset;
import poset.VectorClock;

public class FileEnumerator extends AbstractEnumerator {
	protected PrintWriter fout;
	
	public FileEnumerator() {
		this("out.txt");
	}
	
	public FileEnumerator(String fileName) {
		try {
			fout = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			fout = null;
			logger.warn("Unable to open output file. {}", e.toString());
		}
	}

	@Override
	protected void doEnumeration(Poset poset, VectorClock G) {
		logger.debug("{} is writting a frontier to a file.", this.getClass());
		if (fout != null) fout.println(G.toString());
	}
	
	public void doClose() {
		if (fout != null) fout.close();
	}
}
