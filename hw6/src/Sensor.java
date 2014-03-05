/** Collect appropriate sensor readings when robot moves in the world */

import java.util.Random;

public class Sensor {
	public static final double P_CORRECT = 0.88;
	public static final double P_INCORRECT = 0.04;
	
	char[] colors = {'R', 'G', 'B', 'Y'};
	int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
	
	GridWorld grid;
	int x, y;	// current location on grid; sensor knows this in order to make
				// (imperfect) readings, but this position hidden from robot
	Random rand;
	
	public Sensor(GridWorld g) {
		this.grid = g;
		this.rand = new Random();
		// Start the robot out in a random position on the board
		this.x = rand.nextInt(grid.width);
		this.y = rand.nextInt(grid.height);
	}
	
	// Get imperfect sensor reading
	public char getNextReading() {
		move();
		char correctReading = grid.colors[y][x];
		if (rand.nextDouble() < P_CORRECT) {
			// Return the correct color certain percentage of the time
			return correctReading;
		} else {
			char wrongReading = colors[rand.nextInt(colors.length)];
			while (wrongReading == correctReading) {
				wrongReading = colors[rand.nextInt(colors.length)];
			}
			return wrongReading;
		}
	}
	
	// Move the robot in a random direction
	public void move() {
		int[] dir = directions[rand.nextInt(directions.length)];
		if (grid.inBounds(x + dir[0], y + dir[1]) && 
				! grid.isObstacle(x + dir[0], y + dir[1])) {
			x += dir[0];
			y += dir[1];
		}
	}
	
}