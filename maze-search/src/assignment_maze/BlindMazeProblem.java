package assignment_maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class BlindMazeProblem extends InformedSearchProblem {
	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	private Maze maze;

	public BlindMazeProblem(Maze m) {
		maze = m;
		BlindMazeNode start = new BlindMazeNode(new HashSet<Location>(), 
				Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
		start.populateAllLocs(maze);
		start.updateMinMax();
		startNode = start;
	}
	
	public void test() {
		List<SearchNode> path = this.astarSearch();
		for (SearchNode s : path) {
			System.out.println(s);
		}
	}
	
	public class Location {
		public int x, y;
		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean equals(Object other) {
			Location o = (Location) other;
			return ((this.x == o.x) && (this.y == o.y));
		}
		
		public int hashCode() {
			return (this.x * (maze.height + 1)) + this.y;
		}
		
		public String toString() {
			return new String("(" + this.x + ", " + this.y + ")");
		}
	}
	
	public class BlindMazeNode implements SearchNode {
		private BlindMazeNode parent;
		public int actX; // action took to get here
		public int actY;
		protected HashSet<Location> state;
		private double cost;
		protected int minX, maxX, minY, maxY;
		
		public BlindMazeNode(HashSet<Location> s, int xmi, int xma, 
				int ymi, int yma, double c) {
			state = s;
			minX = xmi;
			maxX = xma;
			minY = ymi;
			maxY = yma;
			cost = c;
		}
		
		public void populateAllLocs(Maze m) {
			// Fill belief state set with all valid locations on maze
			for (int i = 0; i < m.width; i++) {
				for (int j = 0; j < m.height; j++) {
					if (m.isLegal(i, j)) {
						state.add(new Location(i, j));
					}
				}
			}
		}
		
		public SearchNode getParent() {
			return parent;
		}
		
		public void setParent(SearchNode p) {
			this.parent = (BlindMazeNode) p;
		}
		
		public void updateMinMax() {
		// Change minX, maxX, minY, maxY variables as needed
			Iterator<Location> iter = state.iterator();
			
			Location first = iter.next();
			minX = first.x;
			maxX = first.x;
			minY = first.y;
			maxY = first.y;
			
			Location l;
			while(iter.hasNext()) {
				l = iter.next();
				if (l.x < minX) { minX = l.x; }
				if (l.x > maxX) { maxX = l.x; }
				if (l.y < minY) { minY = l.y; }
				if (l.y > maxY) { maxY = l.y; }
			}
		}
		
		public ArrayList<SearchNode> getSuccessors() {
			// For each possible action, check all locations in belief state to
			// see and add and eliminate new belief state locations accordingly
			ArrayList<SearchNode> succ = new ArrayList<SearchNode>();
			
			for (int[] action : actions) {
				int dx = action[0];
				int dy = action[1];
				
				HashSet<Location> stateCopy = new HashSet<Location>();
				for (Location l : this.state) {
					stateCopy.add(l);
				}
				updateBeliefState(stateCopy, dx, dy);
				BlindMazeNode newNode = new BlindMazeNode(stateCopy, this.minX,
						this.maxX, this.minY, this.maxY, this.cost + 1.0);
				
				// Update minX, maxX, minY, maxY for heuristic
				newNode.updateMinMax();
				newNode.setParent(this);
				succ.add(newNode);
			}
			return succ;
		}
		
		public void updateBeliefState(HashSet<Location> newState, int dx, int dy) {
			ArrayList<Location> toRemove = new ArrayList<Location>();
			ArrayList<Location> toAdd = new ArrayList<Location>();
			Iterator<Location> iter = state.iterator();
			
			while (iter.hasNext()) {
				Location l = iter.next();
				if (maze.isLegal(l.x + dx, l.y + dy)) {
					// Can now make this action into possibly new location
					toAdd.add(new Location(l.x + dx, l.y + dy));
					
					// If nothing moves into this location, remove it
					if (! maze.isLegal(l.x - dx, l.y - dy) || 
						! state.contains(new Location(l.x - dx, l.y - dy))) {
						toRemove.add(l);
					}
					
					// If something can move into this location, shift
					// every preceding location over one.
					else if (state.contains(
							new Location(l.x - dx, l.y - dy))) {
						int i = l.x - 2 * dx;
						int j = l.y - 2 * dy;
						while (state.contains(new Location(i, j))) {
							i--;
							j--;
						}
						toRemove.add(new Location(i + 1, j + 1));
					}
				}
			}

			for (Location r : toRemove) {
				newState.remove(r);
			}
			for (Location a : toAdd) {
				newState.add(a);
			}
		}
		
		public boolean goalTest() {
			return state.size() == 1;
		}
		
		public boolean equals(Object other) {
			return this.state.equals(((BlindMazeNode) other).state);
		}
		
		public int hashCode() {
			int multiplier = 1;
			int sum = 0;
			for (int i = 0; i < maze.width; i++) {
				for (int j = 0; j < maze.height; j++) {
					if (this.state.contains(new Location(i, j))) {
						sum += multiplier;
					}
					multiplier *= 2;
				}
			}
			if (multiplier > Math.pow(2,  30)) {
				multiplier = 1;
			}
			return sum;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			Iterator<Location> iter = this.state.iterator();
			
			sb.append("{");
			while (iter.hasNext()) {
				Location l = iter.next();
				sb.append(new String("(" + l.x + ", " + l.y + "); "));
			}
			sb.append("}");
			return sb.toString();
		}
		
		public double getCost() {
			return cost;
		}
		
		public double heuristic() {
			return (maxX - minX) + (maxY - minY) - 2;
		}
		
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		
		public double priority() {
			return heuristic() + getCost();
		}
	}
	
	public static void main(String[] args) {
		Maze m = Maze.readFromFile("maze5.maz");
		BlindMazeProblem bp = new BlindMazeProblem(m);
		bp.test();
	}
	
}