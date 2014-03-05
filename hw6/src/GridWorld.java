/** GridWorld.java
 * Representation of the world of colored tiles and obstacles that the robot
 * must navigate through.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GridWorld {
	public char[][] colors;
	public int width;
	public int height;
	public int validSquares;	// non-obstacle squares
	int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
	
	public GridWorld(int w, int h) {
		this.width = w;
		this.height = h;
		this.colors = new char[height][width];
		this.validSquares = w * h;
	}
	
	public GridWorld(String filename, int w, int h) {
		loadGridFromFile(filename, w, h);
	}
	
	private void loadGridFromFile(String filename, int w, int h) {
		this.width = w;
		this.height = h;
		this.colors = new char[height][width];
		this.validSquares = 0;
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String[] row;
			for (int i = 0; i < height; i++) {
				row = br.readLine().split(",");
				for (int j = 0; j < width; j++) {
					colors[i][j] = row[j].trim().toCharArray()[0];
					if (! isObstacle(i, j)) {
						validSquares++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void printGrid() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.print(colors[i][j]);
				System.out.print(' ');
			}
			System.out.println("\n");
		}
	}
	
	public boolean isObstacle(int i, int j) {
		return colors[i][j] == '.';
	}
	
	public boolean inBounds(int i, int j) {
		return !(i < 0 || j < 0 || i >= height || j >= width);
	}
	
	public int[][] getAdjacentLocs(int i, int j) {
		// Get the 4 adjacent locations to (i, j) from directions N, E, S, W
		// If the neighbor is edge of board or obstacle, replace with (i, j)
		int[][] adjacentLocs = new int[4][2];
		int index = 0;
		for (int[] dir: directions) {
			if (inBounds(i + dir[0], j + dir[1]) && ! isObstacle(i + dir[0], j + dir[1])) {
				adjacentLocs[index][0] = i + dir[0];
				adjacentLocs[index][1] = j + dir[1];
			} else {
				adjacentLocs[index][0] = i;
				adjacentLocs[index][1] = j;
			}
			index++;
		}
		
		return adjacentLocs;
	}
	
	public static void main(String[] args) {
		GridWorld g = new GridWorld("src/grid1.txt", 5, 4);
		g.printGrid();
		g.getAdjacentLocs(0, 0);
	}
	
}
