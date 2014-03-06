import java.text.DecimalFormat;

public class Robot {
	double[][] belief;
	GridWorld grid;
	Sensor sensor;
	
	public Robot(GridWorld g) {
		this.grid = g;
		this.sensor = new Sensor(grid);
		this.belief = new double[grid.height][grid.width];
		// Initialize belief state with uniform probabilities
		Matrix.setAll(belief, grid, 1.0 / (grid.validSquares));
	}
	
	public void update() {
		// Move robot and detect next color from sensor
		char color = sensor.getNextReading();
		// Update belief state
		belief = getSensorUpdate(color, getTransitionUpdate());
		System.out.println("Color read: " + color);
		System.out.println();
		printBeliefState();
		System.out.println();
	}
	
	private double[][] getTransitionUpdate() {
		double[][] newBelief = new double[grid.height][grid.width];
		for (int i = 0; i < grid.height; i++) {
			for (int j = 0; j < grid.width; j++) {
				if (! grid.isObstacle(i, j)) {
					int[][] adjacentLocs = grid.getAdjacentLocs(i, j);
					newBelief[i][j] = Matrix.getAverage(belief, adjacentLocs);
				} else {
					newBelief[i][j] = 0;
				}
			}
		}
		return newBelief;
	}
	
	// Add sensor evidence factor to transition update
	private double[][] getSensorUpdate(char color, double[][] transitionUpdate) {
		double[][] newBelief = new double[grid.height][grid.width];
		for (int i = 0; i < grid.height; i++) {
			for (int j = 0; j < grid.width; j++) {
				if (! grid.isObstacle(i, j)) {
					if (grid.colors[i][j] == color) {
						newBelief[i][j] = transitionUpdate[i][j] * Sensor.P_CORRECT;
					} else {
						newBelief[i][j] = transitionUpdate[i][j] * Sensor.P_INCORRECT;
					}
				} else {
					newBelief[i][j] = 0;
				}
			}
		}
		Matrix.normalize(newBelief);
		return newBelief;
	}
	
	public void printBeliefState() {
		DecimalFormat df = new DecimalFormat("#.0000");
		for (int i = 0; i < belief.length; i++) {
			for (int j = 0; j < belief[0].length; j++) {
				System.out.print(df.format(belief[i][j]));
				System.out.print(' ');
			}
			System.out.print('\n');
		}
	}
	
	public static void main(String[] args) {
		Robot r = new Robot(new GridWorld("src/grid1.txt", 5, 4));
		r.grid.printGrid();
		System.out.println();
		r.printBeliefState();
		System.out.println();
		r.update();
		r.printBeliefState();
		System.out.println();
		r.update();
		r.printBeliefState();
		System.out.println();
		r.update();
		r.printBeliefState();
		System.out.println();
		r.update();
		r.printBeliefState();
	}
}