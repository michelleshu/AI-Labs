package assignment_robots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import assignment_robots.SteeredCar;

public class CarRRT {
	
	public static final double WINDOW_WIDTH = 600;
	public static final double WINDOW_HEIGHT = 400;
	public static final int NUM_RAND_SAMPLES = 300;
	// Relative importance of angle compared to position in distance metric
	public static final double THETA_WEIGHT = 50;
	// How close we require end of search to be to goal
	public static final double TOL = 30;
	
	// Compute a RRT from start configuration to goal configuration
	// Returns a predecessor HashMap containing path from start to goal
	public static HashMap<CarRobot, CarRobot> buildRRT(World w, 
			CarRobot start, CarRobot goal) {
		CarRobot current = new CarRobot();
		current.set(start.getCarState());
		
	}
	
	public CarRobot getBestSuccessorState(World w, CarRobot current, CarRobot goal) {
		CarRobot bestState = null;
		double minDistance = Double.MAX_VALUE;
		Random rand = new Random();
		
		for (int control = 0; control < 6; control++) {
			double time = rand.nextDouble() * 10;
			if (! w.carCollisionPath(new CarRobot(), current.getCarState(), 
					control, time)) {
				CarState newState = SteeredCar.move(current.getCarState(), 
						control, time);
				CarRobot newRobot = new CarRobot();
				newRobot.set(newState);
				if (distance(newRobot, goal) < minDistance) {
					bestState = newRobot;
					minDistance = distance(newRobot, goal);
				}
			}
		}
		
		return bestState;
	}
	
	// Computes distance between two car states
	private double distance(CarRobot a, CarRobot b) {
		CarState sa = a.getCarState();
		CarState sb = b.getCarState();
		double distance = 0;
		
		// Euclidean distance between (x, y) locations
		distance += Math.sqrt(Math.pow(sb.getX() - sa.getX(), 2) + 
				Math.pow(sb.getY() - sb.getY(), 2));
		
		// Angle distance between thetas
		double td = Math.abs(sb.getTheta() - sa.getTheta());
		distance += THETA_WEIGHT * Math.min(td, (2 * Math.PI) - td);
		return distance;
	}
}