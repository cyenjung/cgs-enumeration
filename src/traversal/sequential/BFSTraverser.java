package traversal.sequential;

import java.util.ArrayDeque;
import java.util.Deque;

import poset.DefaultPoset;
import poset.VectorClock;

public class BFSTraverser extends BoundedTraverser {
	@Override
	public void boundedTraverse(DefaultPoset poset, VectorClock l, VectorClock m) {
		final int n = poset.width();
		
		Deque<VectorClock> que = new ArrayDeque<VectorClock>();
		que.addLast(l);
		
		while(!que.isEmpty()) {
			VectorClock G = que.pollFirst();
			enumerator.enumerate(poset, G); // evaluate predicate
			for (int i = 0; i < n; ++i) {
				if (G.get(i) >= m.get(i)) continue; // next event exceeds the boundary 
				if (!poset.isNextEnabled(G, i)) continue; // next event is inconsistent to current frontier
				boolean hasDuplicate = false;
				for (int j = 0; j < n; ++j) {
					if (i != j && G.get(j) > l.get(j) && poset.getId(i, G.get(i)+1) < poset.getId(j, G.get(j)) &&
					    poset.getVectorClock(i, G.get(i)+1).isConcurrentWith(poset.getVectorClock(j, G.get(j))))
					{
						hasDuplicate = true;
						break;
					}
				}
				if (!hasDuplicate) {
					VectorClock H = VectorClock.copyOf(G);
					H.set(i, G.get(i)+1);
					que.addLast(H);
				}
			}
		}
	}
}
