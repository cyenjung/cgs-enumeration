package traversal.concurrent;

import traversal.Traverser;

public abstract class ConcurrentTraverser extends Traverser {
	protected int poolSize = 4;
	
	public ConcurrentTraverser() { }
	
	public ConcurrentTraverser(int poolSize) {
		this.poolSize = poolSize;
	}
	
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	public int getPoolSize() {
		return poolSize;
	}
}
