package util;

/**
 * An implementation of stack without any safety check, e.g., 
 * thread synchronization, range checking, etc. 
 * The stack is faster than JDK Stack, but use with precautions. 
 * @author yenjung
 *
 */
public class UnsafeIntStack {
	final int[] data;
	int ptr = -1;

	public UnsafeIntStack(int size) {
		data = new int[size];
	}

	public void push(int val) {
		data[++ptr] = val;
	}
	
	public int pop() {
		return data[ptr--];
	}
	
	public int popUntil(final int k) {
		while (data[ptr] > k) { --ptr; }
		return data[ptr];
	}
	
	public int peek() {
		return data[ptr];
	}
	
	public void clear() {
		ptr = -1;
	}
}