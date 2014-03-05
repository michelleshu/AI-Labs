public class Robot {
	double[][] beliefs;
	GridWorld grid;
	Sensor sensor;
	
	public Robot(GridWorld g) {
		this.grid = g;
		this.sensor = new Sensor(grid);
		this.beliefs = new double[grid.height][grid.width];
		// Initialize belief state with uniform probabilities
		Matrix.setAll(beliefs, grid, 1.0 / (grid.validSquares));
	}
	
	public void update() {
		
		
		// Move and detect color from sensor
		char color = sensor.getNextReading();
	}
	
	private double[][] getTransitionUpdate() {
		double[][] newBelief = new double[grid.height][grid.width];
		for (int i = 0; i < grid.height)
	}
}