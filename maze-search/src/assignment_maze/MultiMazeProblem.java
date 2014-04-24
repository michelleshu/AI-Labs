package assignment_maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Find a path for a single agent to get from a start location (xStart, yStart)
//  to a goal location (xGoal, yGoal)

public class MultiMazeProblem extends InformedSearchProblem {

	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	
	private int k;	// number of robots in this problem
	
	// start and goal locations 
	private int[] xStarts, yStarts, xGoals, yGoals;

	private Maze maze;
	
	public MultiMazeProblem(Maze m, int[] xs, int[] ys, int[] xg, int[] yg, int k) {
		this.k = k;
		startNode = new MazeNode(xs, ys, 0, 0);
		this.xGoals = xg;
		this.yGoals = yg;
		this.maze = m;	
	}
	
	public void test() {
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in SearchProblem.
	public class MazeNode implements SearchNode {
		private MazeNode parent;

		// locations of robots + current robot
		protected int[] state; 
		
		// how far the current node is from the start.
		private double cost;  

		public MazeNode(int[] xLocs, int[] yLocs, int currentRobot, double c) {
			// Location of robot i is (state[i], state[k+i])
			// Last dimension of state is index of robot who is next to move
			state = new int[2 * k + 1];
			for (int i = 0; i < k; i++) {
				state[i] = xLocs[i];
				state[i + k] = yLocs[i];
			}
			state[2 * k] = currentRobot;
			cost = c;
		}
		
		public int getX() { // get x location of last robot to move
			int lastRobot = (state[2 * k] + k - 1) % k;
			return state[lastRobot];
		}
		
		public int getY() { // get y location of current robot
			int lastRobot = (state[2 * k] + k - 1) % k;
			return state[lastRobot + k];
		}
		
		public SearchNode getParent() {
			return parent;
		}
		
		public void setParent(SearchNode p) {
			this.parent = (MazeNode) p;
		}
		
		public boolean robotsCollide(int currentRobot, int xNew, int yNew) {
			for (int otherRobot = 0; otherRobot < k; otherRobot++) {
				if (otherRobot != currentRobot) {
					if ((state[otherRobot] == xNew) && 
						(state[otherRobot + k] == yNew)) {
						return true;
					}
				}
			}
			return false;
		}

		public ArrayList<SearchNode> getSuccessors() {
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();
			int r = state[2 * k]; // index of current robot
			
			for (int[] action: actions) {
				int xNew = state[r] + action[0];
				int yNew = state[k + r] + action[1];
				// make copy of current state to modify for successors
				int[] xStates = Arrays.copyOfRange(state, 0, k);
				int[] yStates = Arrays.copyOfRange(state, k, 2*k);
				
				if(maze.isLegal(xNew, yNew) && 
						! this.robotsCollide(r, xNew, yNew)) {
					xStates[r] = xNew;
					yStates[r] = yNew;
					SearchNode succ = new MazeNode(xStates, yStates, (r + 1) % k,
							getCost() + 1.0);
					successors.add(succ);
				}	
			}
			
			// Also add current node to successors (choose no movement)
			int[] xStates = Arrays.copyOfRange(state, 0, k);
			int[] yStates = Arrays.copyOfRange(state, k, 2*k);
			successors.add(new MazeNode(xStates, yStates, (r + 1) % k, 
					getCost() + 1.0));
			return successors;
		}
		
		@Override
		public boolean goalTest() {
			// For all robots, check if location is equal to their goal location
			for (int i = 0; i < k; i++) {
				if (state[i] != xGoals[i] || state[i + k] != yGoals[i]) {
					return false;
				}
			}
			return true;
		}

		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state, ((MazeNode) other).state);
		}

		@Override
		public int hashCode() {
			int code = 0;
			int multiplier = maze.width;
			for (int i = 0; i < state.length; i++) {
				code += multiplier * state[i];
				multiplier *= multiplier;
			}
			return code;
		}

		@Override
		public String toString() {
			return new String("(" + state[0] + ", " + state[2] + "); (" + state[1] + 
					", " + state[3] + "); " + state[4]);
		}

		@Override
		public double getCost() {
			return cost;
		}
		

		@Override
		public double heuristic() {
			// sum of Manhattan distance metric for robots
			int sum = 0;
			for (int i = 0; i < k; i++) {
				sum += Math.abs(xGoals[i] - state[i]);
				sum += Math.abs(yGoals[i] - state[k + i]);
			}
			return sum;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		
		@Override
		public double priority() {
			return heuristic() + getCost();
		}

	}

	public static void main(String[] args) {
		Maze m = Maze.readFromFile("simple.maz");
		int[] startX = {0, 1};
		int[] startY = {0, 0};
		int[] goalX = {6, 6};
		int[] goalY = {1, 0};
		MultiMazeProblem p = new MultiMazeProblem(m, startX, startY, goalX, goalY, 2);
		List<SearchNode> path = p.astarSearch();
		for (SearchNode s : path) {
			System.out.println(s);
		}
	}
}
