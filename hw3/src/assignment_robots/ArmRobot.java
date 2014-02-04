package assignment_robots;

import java.util.Arrays;

// This is my modified arm robot class.
// The base of the arm is fixed at (0, 0). The length of each link in the arm 
// is constant and fixed. A configuration of the arm consists of a sequence of
// angles of each segment from the previous segment.

public class ArmRobot {
	protected final double BASE_X = 400;
	protected final double BASE_Y = 30;
	// Configuration: angles of links from one another
	protected double[] config;
	// Number of links
	protected int links;
	// Dimensions of each link
	protected static double length = 100;
	protected static double width = 5;
	
	public int getLinks() {
		return links;
	}
	
	// Constructor, input the number of links of this arm;
	// Initialize all the coordinates to 0; the initial width is 10	
	public ArmRobot(int num) {
		links = num;
		config = new double[num];
		int i = 0;
		for (i = 0; i < config.length; i++) {
			config[i] = 0;
		}
	}
	
	// Set the angle of a link
	public void setLinkAngle(int i, double ang) {
		config[i] = ang;
	}
	
	// Set the entire configuration of the arm
	public void set(double[] configuration) {
		for (int i = 0; i < links; i++) {
			setLinkAngle(i, configuration[i]);
		}
	}
	
	// Get the angle of the ith link
	public double getLinkAngle(int i) {
		return config[i];
	}
	
	// Get a copy of the entire configuration of the arm (so we avoid
	// modifying the original)
	public double[] getConfig() {
		double[] configCopy = new double[links];
		System.arraycopy(this.config, 0, configCopy, 0, links);
		return configCopy;
	}
	
	// Get the rectangular coordinates of the ith link
	public double[][] getLinkBox(int i) {
		double[][] rect = new double[4][2];
		
		double x = BASE_X;
		double y = BASE_Y;
		double angle = 0;
		for (int j = 0; j < i - 1; j++) {
			angle = (angle + config[j]) % (2 * Math.PI);
			x = x + length * Math.cos(angle);
			y = y + length * Math.sin(angle);
		}
		double xp = x;
		double yp = y;
		angle = (angle + config[i - 1]) % (2 * Math.PI);
		x = xp + length * Math.cos(angle);
		y = yp + length * Math.sin(angle);
		
		rect[0][0] = xp + width*Math.cos(angle + Math.PI / 2);
		rect[0][1] = yp + width*Math.sin(angle + Math.PI / 2);
		
		rect[1][0] = x + width*Math.cos(angle + Math.PI / 2);
		rect[1][1] = y + width*Math.sin(angle + Math.PI / 2);
		
		rect[2][0] = x + width*Math.cos(angle - Math.PI / 2);
		rect[2][1] = y + width*Math.sin(angle - Math.PI / 2);
		
		rect[3][0] = xp + width*Math.cos(angle - Math.PI / 2);
		rect[3][1] = yp + width*Math.sin(angle - Math.PI / 2);
		
		return rect;
	}
	
	public int hashCode() {
		return Arrays.hashCode(config);
//		int base = 7;
//		int multiplier = 7;
//		int code = 0;
//		for (int i = 0; i < config.length; i++) {
//			code = (int) (code + multiplier * (config[i]));
//			multiplier = multiplier * base;
//		}
//		return code;
	}
	
	public boolean equals(Object o) {
		ArmRobot other = (ArmRobot) o;
		double[] otherConfig = other.getConfig();
		for (int i = 0; i < config.length; i++) {
			if (config[i] != otherConfig[i]) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < this.config.length - 1; i++) {
			sb.append(this.config[i] + ", ");
		}
		sb.append(this.config[this.config.length - 1] + ")");
		return sb.toString();
	}
	
}