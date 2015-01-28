package poset;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import traversal.Traverser;

import util.SimpleOptions;

public class TraverseLattice {

	public static void main(String argc[]) {
		Logger logger = LoggerFactory.getLogger(TraverseLattice.class);
		logger.info("{} started.", TraverseLattice.class.getSimpleName());
		
		SimpleOptions options = new SimpleOptions();
		try {
			options.parse(argc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Stopping the program because failed to initialize.");
			logger.error(Arrays.toString(e.getStackTrace()));
			return;
		}
		
		Traverser traverser = options.getTraverser();
		Poset poset = traverser.getPoset();
		try {
			String fileName = options.getTestName();
			Builder builder = poset.getBuilder();
			PosetParser parser = new PosetParser();
			parser.parsePosetFromFile(fileName, builder);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error(Arrays.toString(e.getStackTrace()));
			return;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(Arrays.toString(e.getStackTrace()));
			return;
		}
		
		traverser.addEnumerator(options.getEnumerator());
		
		traverser.traverse(poset);
		
		logger.info("{} ended.\n", TraverseLattice.class.getSimpleName());
	}
}
