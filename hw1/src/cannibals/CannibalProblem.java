package cannibals;

import java.util.ArrayList;
import java.util.Arrays;


// for the first part of the assignment, you might not extend UUSearchProblem,
//  since UUSearchProblem is incomplete until you finish it.

public class CannibalProblem extends UUSearchProblem {

	// the following are the only instance variables you should need.
	//  (some others might be inherited from UUSearchProblem, but worry
	//  about that later.)

	private int goalm, goalc, goalb;
	private int totalMissionaries, totalCannibals; 

	public CannibalProblem(int sm, int sc, int sb, int gm, int gc, int gb) {
		// I (djb) wrote the constructor; nothing for you to do here.

		startNode = new CannibalNode(sm, sc, 1, 0);
		goalm = gm;
		goalc = gc;
		goalb = gb;
		totalMissionaries = sm;
		totalCannibals = sc;
		
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in UUSearchProblem.
	private class CannibalNode implements UUSearchNode {

		// do not change BOAT_SIZE without considering how it affect
		// getSuccessors. 
		
		private final static int BOAT_SIZE = 2;
	
		// how many missionaries, cannibals, and boats
		// are on the starting shore
		private int[] state; 
		
		// how far the current node is from the start.  Not strictly required
		//  for search, but useful information for debugging, and for comparing paths
		private int depth;  

		public CannibalNode(int m, int c, int b, int d) {
			state = new int[3];
			this.state[0] = m;
			this.state[1] = c;
			this.state[2] = b;
			
			depth = d;

		}

		// Get legal states reachable from current state using valid actions
		public ArrayList<UUSearchNode> getSuccessors() {
			ArrayList<UUSearchNode> successors = new ArrayList<UUSearchNode>();
			
			int dmMax, dcMax; // maximum change in number of missionaries or cannibals
			int dir;	// direction of change (- toward goal, + toward start)
			int b;		// position of boat in next move
			
			if (this.state[2] == 0) { // boat on goal side of river
				dir = 1;
				b = 1;
				dmMax = Math.min(BOAT_SIZE, totalMissionaries - this.state[0]);
				dcMax = Math.min(BOAT_SIZE, totalCannibals - this.state[1]);
			} else { // boat on start side of river
				dir = -1;
				b = 0;
				dmMax = Math.min(BOAT_SIZE, this.state[0]);
				dcMax = Math.min(BOAT_SIZE, this.state[1]);
			}
			
			// Loop through all possible actions; add states to successors if legal
			for (int dm = 0; dm <= dmMax; dm++) {
				for (int dc = 0; dc <= Math.min(BOAT_SIZE - dm, dcMax); dc++) {
					if (dm + dc == 0) {
						continue;	// need at least one to row the boat
					}
					
					int mNew = this.state[0] + dir * dm;
					int nNew = this.state[1] + dir * dc;
					if (isSafeState(mNew, nNew)) {
						successors.add(new CannibalNode(mNew, nNew, b, this.depth + 1));
					}
				}
			}
			
			return successors;
		}
		
		private boolean isSafeState(int m, int c) {
			// More cannibals than missionaries on start side?
			if ((c > m) && (m > 0)) {
				return false;
			}
			// More cannibals than missionaries on goal side?
			if ((totalCannibals - c > totalMissionaries - m) && 
					(totalMissionaries - m > 0)) {
				return false;
			}
			return true;
		}
		
		@Override
		public boolean goalTest() {
			return this.state[0] == goalm && this.state[1] == goalc && this.state[2] == goalb;
		}

		

		// an equality test is required so that visited lists in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state, ((CannibalNode) other).state);
		}

		@Override
		public int hashCode() {
			return state[0] * 100 + state[1] * 10 + state[2];
		}

		@Override
		public String toString() {
			return "(" + state[0] + ", " + state[1] + ", " + state[2] + ")";
		}

		
        //You might need this method when you start writing 
        //(and debugging) UUSearchProblem.
        
		@Override
		public int getDepth() {
			return depth;
		}
	}
	
	public void test() {
		CannibalNode start = new CannibalNode(2, 2, 0, 0);
		ArrayList<UUSearchNode> successors = start.getSuccessors();
		for (UUSearchNode s : successors) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
		// Test successors function
		CannibalProblem c = new CannibalProblem(3, 3, 1, 0, 0, 1);
		c.test();
	}
	

}
