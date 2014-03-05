/** General grid (type double) utility functions */

public class Grid {
	
	// Set all valid locations
	public static void setAll(double[][] matrix, GridWorld g, double val) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				// Avoid obstacles
				if (!g.isObstacle(i, j)) {
					matrix[i][j] = val;
				}
			}
		}
	}
	
	// Normalize values so that they sum to 1
	public static void normalize()
}