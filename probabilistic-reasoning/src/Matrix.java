/** General grid (type double) utility functions */

public class Matrix {
	
	// Set all valid locations
	public static void setAll(double[][] matrix, GridWorld g, double val) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				// Avoid obstacles
				if (!g.isObstacle(i, j)) {
					matrix[i][j] = val;
				} else {
					matrix[i][j] = 0;
				}
			}
		}
	}
	
	// Normalize values so that they sum to 1
	public static void normalize(double[][] matrix) {
		// First pass: take sum of all values in matrix
		double sum = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				sum += matrix[i][j];
			}
		}
		// Second pass: divide all values by sum
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				matrix[i][j] /= sum;
			}
		}
	}
	
	// Get average of matrix entries at given (i, j) locations
	public static double getAverage(double[][] matrix, int[][] locs) {
		double sum = 0.0;
		for (int l = 0; l < locs.length; l++) {
			int[] loc = locs[l];
			sum += matrix[loc[0]][loc[1]];
		}
		return (sum / (double) locs.length);
	}
}