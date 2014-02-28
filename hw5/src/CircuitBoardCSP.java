import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Circuit Board CSP
 *  Represent location (x, y) on board of height m and width n as y*n + x
 */

public class CircuitBoardCSP extends ConstraintSatisfactionProblem {
	int m, n;
	ArrayList<String> elementNames = new ArrayList<String>();
	ArrayList<Integer> elementWidths = new ArrayList<Integer>();
	ArrayList<Integer> elementHeights = new ArrayList<Integer>();
	HashMap<String, Integer> variableToInt = new HashMap<String, Integer>();
	HashMap<Integer, String> intToVariable = new HashMap<Integer, String>();
	
	public CircuitBoardCSP(String filename) {
		super();
		loadBoardFromFile(filename);
		initializeVariableMapping();
		this.domains = generateDomains();
		this.constraint = generateConstraints(this.domains);
	}
	
	private void loadBoardFromFile(String filename) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			
			// Read board size
			String[] size = br.readLine().split(",");
			this.m = Integer.parseInt(size[0].trim());
			this.n = Integer.parseInt(size[1].trim());
			
			// Read elements
			int i = 0;
			String line;
			String[] element;
			while ((line = br.readLine()) != null) {
				element = line.split(",");
				elementNames.add(i, element[0].trim());
				elementWidths.add(i, Integer.parseInt(element[1].trim()));
				elementHeights.add(i, Integer.parseInt(element[2].trim()));
				i++;
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
	
	private ArrayList<ArrayList<Integer>> generateDomains() {
		ArrayList<ArrayList<Integer>> domains = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < elementNames.size(); i++) {
			domains.add(i, new ArrayList<Integer>());
			// Add all possible locations for this element
			for (int x = 0; x <= this.n - elementWidths.get(i); x++) {
				for (int y = 0; y <= this.m - elementHeights.get(i); y++) {
					domains.get(i).add(getIndex(x, y));
				}
			}
		}
		return domains;
	}
	
	private Constraint generateConstraints(ArrayList<ArrayList<Integer>> domains) {
		HashMap<Pair, HashSet<Pair>> allowedValues = new HashMap<Pair, HashSet<Pair>>();
		HashSet<Integer> variablesInvolved = new HashSet<Integer>();
		
		for (int e1 = 0; e1 < elementNames.size() - 1; e1++) {
			for (int e2 = e1 + 1; e2 < elementNames.size(); e2++) {
				allowedValues.put(new Pair(e1, e2), new HashSet<Pair>());
				for (int loc1 : domains.get(e1)) {
					for (int loc2 : domains.get(e2)) {
						if (! areColliding(e1, e2, loc1, loc2)) {
							allowedValues.get(new Pair(e1, e2)).add(new Pair(loc1, loc2));
						}
					}
				}
			}
		}
		
		for (int i = 0; i < elementNames.size(); i++) {
			variablesInvolved.add(i);
		}
		
		return new Constraint(allowedValues, variablesInvolved);
	}
	
	
	private void initializeVariableMapping() {
		for (int i = 0; i < elementNames.size(); i++) {
			this.variableToInt.put(elementNames.get(i), i);
			this.intToVariable.put(i, elementNames.get(i));
		}
	}
	
	private int getIndex(int x, int y) {
		return y * this.n + x;
	}
	
	private int getX(int index) {
		return index % this.n;
	}
	
	private int getY(int index) {
		return index / this.n;
	}
	
	private boolean areColliding(int e1, int e2, int loc1, int loc2) {
		int x1 = getX(loc1);
		int y1 = getY(loc1);
		int w1 = elementWidths.get(e1);
		int h1 = elementHeights.get(e1);
		
		int x2 = getX(loc2);
		int y2 = getY(loc2);
		int w2 = elementWidths.get(e2);
		int h2 = elementHeights.get(e2);
		
		boolean horiz = ((x1 <= x2 && x2 <= (x1 + w1 - 1)) || 
				(x2 <= x1 && x1 <= (x2 + w2 - 1)));
		boolean vert = ((y1 <= y2 && y2 <= (y1 + h1 - 1)) || 
				(y2 <= y1 && y1 <= (y2 + h2 - 1)));
		return horiz && vert;
	}
	
	public void printSolution(int[] sol) {
		// Initialize m x n grid of '.'
		String [][] grid = new String[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				grid[i][j] = ".";	
			}
		}
		// Now replace '.' in places where circuit board elements go
		for (int element = 0; element < sol.length; element++) {
			int upperLeft = sol[element];
			int x = getX(upperLeft);
			int y = getY(upperLeft);
			int w = elementWidths.get(element);
			int h = elementHeights.get(element);
			String id = intToVariable.get(element);
			for (int i = y; i < y + h; i++) {
				for (int j = x; j < x + w; j++) {
					grid[i][j] = id;
				}
			}
		}
		
		// Print the grid
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		CircuitBoardCSP cb = new CircuitBoardCSP("src/CB3.txt");
		int[] sol = cb.solve();
		System.out.println(cb.nodesVisited);
		cb.printSolution(sol);
	}
}