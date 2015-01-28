package poset;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PosetParser {
	private final Logger logger = LoggerFactory.getLogger(PosetParser.class);

	/**
	 * Construct a poset from the given file.
	 *
	 * @param fileName the file contains the target poset.
	 *
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void parsePosetFromFile(String fileName, Builder builder)
			throws NumberFormatException, IOException {
		
		logger.info("Parsing poset from file: \"{}\"", fileName);

		// TODO check file format

		String s;
		FileReader fr = new FileReader(new File(fileName));
		LineNumberReader r = new LineNumberReader(fr);
		boolean isBuilderInitialized = false;
		while ((s = r.readLine()) != null) {
			int pindex = s.indexOf("(");
			if (pindex == -1)
				continue; // no vector on this line
			pindex = s.indexOf(":");
			if (pindex != -1) {
				int pid = Integer.parseInt(s.substring(0, pindex));
				VectorClock vc = VectorClock.fromString(s);
				if (!isBuilderInitialized) {
					isBuilderInitialized = true;
					builder.beginBuild(vc.size());
				}
				builder.addVectorClock(pid, vc);
			}
		}
		fr.close();
		
		builder.endBuild();
		logger.info("Successfully parsed the poset: \"{}\"", fileName);
	}
}
