package util;

public class PerformanceMon {
	long startTime;
	long totalTime;
	
	public void start() {
		startTime = System.currentTimeMillis(); // don't put anything after this line
	}
	
	public void end(){
		totalTime = System.currentTimeMillis() - startTime; // don't put anything before this line
	}
	
	public String getStat() {
		return "Time:" + totalTime + "ms";
	}
}
