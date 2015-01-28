package util;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import traversal.Traverser;
import traversal.concurrent.ConcurrentTraverser;
import traversal.enumerator.Enumerator;
import traversal.enumerator.NOPEnumerator;

public class SimpleOptions {
	private Logger logger = LoggerFactory.getLogger(SimpleOptions.class);
	private Traverser traverser;
	private String testName;
	private Enumerator enumerator;
	
	public SimpleOptions() { }
	
	public void parse(String[] argc) throws Exception {
		for (int i = 0; i < argc.length; ++i) {
			switch (i) {
			case 0:
				traverser = decideTraverser(argc[i]);
				break;
			case 1:
				testName = argc[i];
				break;
			case 2:
				if (traverser instanceof ConcurrentTraverser) {
					((ConcurrentTraverser) traverser).setPoolSize(Integer.parseInt(argc[i]));
				}
				break;
			case 3:
				enumerator = decideEnumerator(argc[i]);
				break;
			default:
			}
		}
	}

	public Traverser getTraverser() {
		return traverser;
	}
	
	public String getTestName() {
		return testName;
	}
	
	public Enumerator getEnumerator() {
		return (enumerator != null) ? enumerator : new NOPEnumerator();
	}
	
	private Traverser decideTraverser(String traverserName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		final String defaultTraverser = "traversal.concurrent.ConcurrentUnorderedTraverser";
		StringBuilder fullTraverserName = new StringBuilder("traversal.");
		if (traverserName.startsWith("Concurrent")) {
			fullTraverserName.append("concurrent.");
		} else {
			fullTraverserName.append("sequential.");
		}
		fullTraverserName.append(traverserName);
		if (!traverserName.endsWith("Traverser")) {
			fullTraverserName.append("Traverser");
		}
		
		return (Traverser) getClassFromName(fullTraverserName.toString(), defaultTraverser)
				.newInstance();
	}
	
	private Enumerator decideEnumerator(String string) {
		Enumerator enumerator = null;
		
		StringTokenizer st = new StringTokenizer(string, ":");
		while (st.hasMoreTokens()) {
			String enumName = st.nextToken().trim();
			StringBuilder fullEnumName = new StringBuilder("traversal.enumerator.");
			fullEnumName.append(enumName);
			if (!enumName.endsWith("Enumerator")) {
				fullEnumName.append("Enumerator");
			}
			try {
				Class<?> enumClazz = getClassFromName(fullEnumName.toString(), null);
				if (enumClazz != null) {
					Enumerator childEnumerator = (Enumerator) enumClazz
							.newInstance();
					if (enumerator != null) {
						enumerator.setNext(childEnumerator);
					} else {
						enumerator = childEnumerator;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return enumerator;
	}
	
	private Class<?> getClassFromName(String clazzName, String defaultClazzName) throws ClassNotFoundException {
		try {
			return Class.forName(clazzName);
		} catch (ClassNotFoundException e) {
			logger.warn(
					"Initiating the class:\"{}\" failed; using the default class \"{}\".",
					clazzName, defaultClazzName);
		}
		// in non-case-sensitive file system (e.g., Mac HFS+), a class may not
		// be found because case sensitivity problem. For example, to initial
		// a LexTraverser but is misspelled as LEXTraverser.
		catch(NoClassDefFoundError e) {
			logger.warn(
					"Initiating the class:\"{}\" failed; using the default class \"{}\".",
					clazzName, defaultClazzName);
		}
		
		try {
			if (defaultClazzName != null)
				return Class.forName(defaultClazzName);
			else return null;
		} catch (ClassNotFoundException e) {
			logger.error("Failed to initiate the default class: {}.",
					defaultClazzName);
			throw e;
		} catch(NoClassDefFoundError e) {
			logger.error("Failed to initiate the default class: {}.",
					defaultClazzName);
			throw new ClassNotFoundException();
		}
	}
}
