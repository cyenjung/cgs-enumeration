package traversal.sequential;

import poset.DefaultPoset;
import poset.Poset;
import poset.VectorClock;
import traversal.Traverser;

public abstract class BoundedTraverser extends Traverser {

	@Override
	protected void doTraverse(Poset gPoset) {
		DefaultPoset poset = (DefaultPoset) gPoset;
		final int n = poset.width();
		VectorClock l = new VectorClock(n, 0); // least frontier
		VectorClock m = new VectorClock(n); // max frontier
		for (int i = 0; i < n; ++i) m.set(i, poset.sizeOfChain(i)-1 );
		if (l.equals(m)) { enumerator.enumerate(poset, l); }
		else if (l.leq(m)) boundedTraverse(poset, l, m);
	}

	public abstract void boundedTraverse(DefaultPoset poset, VectorClock l, VectorClock m);
}
