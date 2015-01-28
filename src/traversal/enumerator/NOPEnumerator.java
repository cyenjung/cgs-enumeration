package traversal.enumerator;

import poset.Poset;
import poset.VectorClock;

public class NOPEnumerator extends AbstractEnumerator {

	@Override
	protected void doEnumeration(Poset poset, VectorClock G) { }

	@Override
	protected void doClose() { }

}
