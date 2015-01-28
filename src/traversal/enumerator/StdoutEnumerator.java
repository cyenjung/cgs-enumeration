package traversal.enumerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poset.Poset;
import poset.VectorClock;

public class StdoutEnumerator extends AbstractEnumerator {
	private Logger logger = LoggerFactory.getLogger(StdoutEnumerator.class);
	
	@Override
	protected void doEnumeration(Poset poset, VectorClock G) {
		logger.debug("{} is writting a frontier to STDOUT.", this.getClass());
		System.out.println(G);
	}

	@Override
	protected void doClose() { }
}
