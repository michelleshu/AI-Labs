package assignment_robots;

import java.util.LinkedList;

public class ArmLocalPlanner {
	
	public static final int STEPS_PER_RADIAN = 10;
	public static final double TOL = 0.2; // maximum acceptable distance
										  // between two configs in path
	
	public static enum Direction { CW, CCW };
	
	// Get shortest directions of rotation
	public static Direction[] getDirections(double[] config1, double[] config2) {
		Direction[] directions = new Direction[config1.length];
		double diffCW, diffCCW;
		for (int i = 0; i < config1.length; i++) {
			diffCW = config1[i] - config2[i];
			diffCCW = config2[i] - config1[i];
			if (diffCW < 0.0) { diffCW += 2.0 * Math.PI; }
			if (diffCCW < 0.0) { diffCCW += 2.0 * Math.PI; }
			if (diffCW < diffCCW) {
				directions[i] = Direction.CW;
			} else {
				directions[i] = Direction.CCW;
			}
		}
		return directions;
	}
	
	// Get the time to move from config 1 to config 2 in direction provided
	public static double getTime(double[] config1, double[] config2, 
			Direction[] dir) {
		double diff = 0; // difference between angles
		double maxDiff = 0;
		for (int i = 0; i < config1.length; i++) {
			if (dir[i].equals(Direction.CW)) {
				diff = config1[i] - config2[i];  // clockwise
			} else {
				diff = config2[i] - config1[i]; // counterclockwise
			}
			if (diff < 0.0) { diff += 2 * Math.PI; }
			if (diff > maxDiff) { maxDiff = diff; }
		}
		return maxDiff * STEPS_PER_RADIAN;
	}
	
	public static double[] getVelocity(double[] config1, double[] config2, 
			Direction[] dir) {
		double time = getTime(config1, config2, dir);
		double[] velocity = new double[config1.length];
		
		for (int i = 0; i < config1.length; i++) {
			if (dir[i].equals(Direction.CW)) {
				velocity[i] = config1[i] - config2[i];
				if (velocity[i] < 0.0) { velocity[i] += 2.0 * Math.PI; }
			} else if (dir[i].equals(Direction.CCW)) {
				velocity[i] = config2[i] - config1[i];
				if (velocity[i] < 0.0) { velocity[i] += 2.0 * Math.PI; }
				velocity[i] = - velocity[i];
			}
			velocity[i] /= time;
		}
		
		return velocity;
	}
	
	public static double distance(ArmRobot a, ArmRobot b) {
		double[] configA = a.getConfig();
		double[] configB = b.getConfig();
		double squaredSum = 0;
		for (int i = 0; i < configA.length; i++) {
			double d = Math.abs(configA[i] - configB[i]);
			// Take shortest distance of no wrap v wrap
			squaredSum += Math.pow(Math.min(d, (2 * Math.PI) - d), 2);
		}
		return Math.sqrt(squaredSum);
	}
	
	// Return a local path from configuration of ArmRobot a to configuration
	// of ArmRobot b, according to most efficient directions of rotation
	// Path goes from node immediately following a through and including b
	public static LinkedList<ArmRobot> getLocalPath(ArmRobot a, ArmRobot b) {
		Direction[] dir = getDirections(a.getConfig(), b.getConfig());
		double[] velocity = getVelocity(a.getConfig(), b.getConfig(), dir);
		
		LinkedList<ArmRobot> localPath = new LinkedList<ArmRobot>();
		ArmRobot current = new ArmRobot(a.getLinks());
		current.set(a.getConfig());

		while (distance(current, b) > TOL) {
			ArmRobot next = new ArmRobot(current.getLinks());
			double[] nextConfig = current.getConfig();
			for (int i = 0; i < nextConfig.length; i++) {
				nextConfig[i] += velocity[i];
				// Wrap
				if (nextConfig[i] < 0.0) { 
					nextConfig[i] += 2.0 * Math.PI; 
				} else if (nextConfig[i] > 2.0 * Math.PI) {
					nextConfig[i] -= 2.0 * Math.PI;
				}
			}
			next.set(nextConfig);
			localPath.addLast(next);
			current = next;
		}
		localPath.addLast(b);
		
		return localPath;
	}
	
	public static void main(String[] args) {
		ArmRobot a = new ArmRobot(2);
		ArmRobot b = new ArmRobot(2);
		
		double[] config1 = {Math.PI, 0};
		double[] config2 = {Math.PI/2, Math.PI/2};
		
		a.set(config1);
		b.set(config2);
		
		LinkedList<ArmRobot> path = getLocalPath(a, b);
		for (ArmRobot r : path) {
			System.out.println(r);
		}
	}
}