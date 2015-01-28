package traversal.sequential;

import poset.DefaultPoset;
import poset.VectorClock;

public class LexTraverser extends BoundedTraverser {
	@Override
	public void boundedTraverse(final DefaultPoset poset, final VectorClock l, final VectorClock m) {
		final int n = poset.width(); // number of shared object
		final VectorClock G = VectorClock.copyOf(l);// current CGS
		
		int preK = n;
		while (G.leq(m)) {
			enumerator.enumerate(poset, G); // evaluate predicate 
			
			if (G.equals(m)) break;
			
			// decide k
			int k = n - 1;
			for ( ; k >= 0; --k) {
				if (G.get(k) != m.get(k)) { // if next event on Pk exists
					boolean enabled = true;
					final VectorClock vc = poset.getVectorClock(k, G.get(k)+1);
					for (int j = 0; j < n; ++j) {
						if (j != k && vc.get(j) > G.get(j)) {
							enabled = false;
							break;
						}
					}
					if (enabled) break;
				}
			}
			
			G.set(k, G.get(k)+1);
			if (preK <= k) {
				for (int i = k+1; i < n; ++i)
					if (G.get(i) < poset.getClock(k, G.get(k), i))
						G.set(i, poset.getClock(k, G.get(k), i));
			} else {
				for (int i = k+1; i < n; ++i) G.set(i, l.get(i));
				for (int i = k+1; i < n; ++i) {
					for (int j = 0; j <= k; ++j) {
						if (G.get(i) < poset.getClock(j, G.get(j), i))
							G.set(i, poset.getClock(j, G.get(j), i));
					}
				}
			}
			preK = k;
		}
	}
}
