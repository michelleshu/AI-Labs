public class Robot {
	double[][] beliefs;
	GridWorld grid;
	Sensor sensor;
	
	public Robot(GridWorld g) {
		this.grid = g;
		this.sensor = new Sensor(grid);
		this.beliefs = new double[grid.height][grid.width];
		// Initialize belief state with uniform probabilities
		Grid.setAll(beliefs, grid, 1.0 / (grid.validSquares));
	}
	
	public void update() {
		sensor.getNextReading()
	}
	
	
}