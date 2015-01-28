package poset;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Vector clock can be used to reconstruct the "Happen-before" relationship
 * between any two events in a partial ordered computation.
 * 
 * @author Yen-Jung Chang
 *
 */
public class VectorClock {
	// although using primitive type of array is a bad idea, the performance
	// of array is significantly better than ArrayList in lattice
	// traversing, in where the number of the invocation of get() is around
	// billions.
	private int[] clocks;
	
	/**
	 * Initial a vector clock of size n.
	 * @param n the size of the vector clock.
	 */
	public VectorClock(int n) {
		clocks = new int[n];
	}
	
	/**
	 * Construct a vector clock with size of n and filled with initVal. 
	 * @param n the size of the vector clock.
	 * @param initVal the initial value to fill the vector clock.
	 */
	public VectorClock(int n, int initVal) {
		clocks = new int[n];
		for (int i = 0; i < n; ++i) clocks[i] = initVal;
	}
	
	/**
	 * Deep copy of the target vector clock.
	 * @param that the target vector clock.
	 * @return A copy of vector clock. 
	 */
	public static VectorClock copyOf(VectorClock that) {
		VectorClock vc = new VectorClock(that.size());
		for (int i = that.size() - 1; i >= 0; --i) 
			vc.clocks[i] = that.clocks[i];
		return vc;
	}
	
	/**
	 * Deep copy of the target vector clock.
	 * @param that the target vector clock. 
	 */
	public void copy(VectorClock that) {
		for (int i = clocks.length - 1; i >= 0; --i) 
			clocks[i] = that.clocks[i];
	}
	
	/**
	 * Returns the clock value of specified share variable
	 * @param pid id of specified share variable
	 * @return clock of specified share variable
	 */
	public int get(int pid) {
		return clocks[pid];
	}
	
	/**
	 * Set the clock value of the specified process. 
	 * @param pid the id of the process.
	 * @param val the value to set.
	 */
	public void set(int pid, int val) {
		clocks[pid] = val;
	}
	
	/**
	 * Increase the specified clock by 1.
	 * @param pid the id to the clock.
	 */
	public void increament(int pid) {
		++clocks[pid];
	}
	
	/**
	 * Decrease the specified clock by 1.
	 * @param pid the id to the clock.
	 */
	public void decreament(int pid) {
		--clocks[pid];
	}
	
	/**
	 * Returns the size of the vector clock
	 * @return the size of the vector clock
	 */
	public int size() {
		return clocks.length;
	}
	
	/**
	 * Join this vector clock with the other clock. 
	 * The values of this clock is set to the greatest one of the two clocks. 
	 * @param that The vector clock on a sender event.
	 */
	public void join(VectorClock that) {
		for (int i = this.size()-1; i >= 0; --i) {
			if (this.get(i) < that.get(i)) {
				this.set(i, that.get(i));
			}
		}
	}
	
	/**
	 * Returns if this vector clock's clocks are smaller or equal to those of the specified vector clock.
	 * @param that the specified vector clock.
	 * @return False if one of the clocks is larger than that of the specified vector clock.
	 */
	public boolean leq(VectorClock that) {
		for (int i = 0; i < size(); ++i)
			if (get(i) > that.get(i)) return false;
		return true;
	}
	
	/**
	 * Returns if this vector clock is concurrent with the specified vector clock.
	 * @param that the specified vector clock.
	 * @return False if this vector clock has a happens-before relation with the specified vector clock.
	 */
	public boolean isConcurrentWith(VectorClock that) {
		if (!leq(that) && !that.leq(this)) return true;
		else return false;
	}
	
	/**
	 * Check if the clocks of this vector clock is less than or equals to those on G.
	 * @param G the frontier to compare with.
	 * @param k the range (0 to k-1) to compare.
	 * @return false if any value of vc is larger that that in G.
	 */
	public boolean isLexLeq(final VectorClock G, final int k) {
		for (int j = 0; j < k; ++j) {
			if (get(j) > G.get(j)) return false;
		}
		return true;
	}
	
	/* For experience */
	public boolean isLexLeqPDCS(final VectorClock G, final int k, final int n) {
		for (int j = 0; j < n; ++j) {
			if (j != k && get(j) > G.get(j)) return false;
		}
		return true;
	}
	
	/**
	 * Converts a string of the format, (0, 1, ..., n), to a frontier.  
	 * @param s the string
	 */
	public static VectorClock fromString(String s) {
		int findex = s.indexOf("(");
		int lindex = s.indexOf(")");
		s = s.substring(findex + 1, lindex);
		VectorClock vc = new VectorClock(countSize(s));
		StringTokenizer str = new StringTokenizer(s);
		int count = 0;
		while (str.hasMoreTokens()) {
			s = str.nextToken(",");
			int clock = Integer.parseInt(s);
			vc.set(count++, clock);
		}
		return vc;
	}
	
	/* Determine the size of vector clock from the input string */
	private static int countSize(String s) {
		int count = 0, index = -1;
		while ((index = s.indexOf(",", index + 1)) != -1)
			++count;
		return count + 1;
	}
	
	/**
	 * Shift all clock values by -1.
	 */
	public void shiftClock() {
		if (clocks == null) return;
		for (int i = 0; i < clocks.length; ++i) {
			--clocks[i];
		}
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(clocks);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof VectorClock) {
			VectorClock thatVC = (VectorClock) that;
			return Arrays.equals(clocks, thatVC.clocks);
		} else return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (clocks.length > 0) sb.append("(" + get(0));
		for (int i = 1; i < clocks.length; ++i) {
			sb.append(",");
			sb.append(get(i));
		}
		if (clocks.length > 0) sb.append(")");
		return sb.toString();
	}
}
