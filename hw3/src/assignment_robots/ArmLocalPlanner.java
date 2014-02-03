package assignment_robots;

// This is the local planner of the robot arms;
// Can return time between two configurations;
// can get the path between configurations;

public class ArmLocalPlanner {

	// Get the time to move from configuration 1 to configuration 2;
	// two configurations must be valid configurations for the arm; 
	public static double moveInParallel(double[] config1, double[] config2) {
		double d = 0;
		double maxt = 0;
		
		for (int i = 0; i < config1.length; i++) {
			d = Math.abs(config1[i] - config2[i]);
			d = Math.min(d, (2 * Math.PI) - d); // min of wrap, no wrap
			if (d > maxt) {
				maxt = d;
			}
		}
		return maxt * 10;
	}
	
	// Given two configurations, get the "path" between configurations;
	// return is a double array with the same length as configurations;
	// path[i] is the velocity of component config[i];
	// basically, given certain time duration: step, path[i]*step 
	// is the movement of component config[i] during step;
	public static double[] getPath (double[] config1, double[] config2) {
		double time = moveInParallel(config1, config2);
		double[] path = new double[config1.length];
		
		for (int i = 0; i < config1.length; i++) {
			path[i] = (config2[i] - config1[i]) / time;
			// Easier to go counterclockwise?
			if (Math.abs(config2[i] - config1[i]) > Math.PI) {
				path[i] = -path[i];
			}
		}
		
		return path;
	}
	
	public static void main(String[] args) {
		double[] config1 = {0, 2};
		double[] config2 = {0, 1};
		System.out.println(getPath(config1, config2)[1]);
	}
 }
