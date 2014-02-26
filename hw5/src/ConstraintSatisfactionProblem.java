/** Generic Constraint Satisfaction Problem Solver
 *  Solves CSPs with variables represented as indices in array, values as ints
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConstraintSatisfactionProblem {
	Constraint constraint;
	ArrayList<ArrayList<Integer>> domains;
//	ArrayList<Integer> assignment;
	
	public ConstraintSatisfactionProblem() {
	}
	
	public ConstraintSatisfactionProblem(Constraint cons, 
		ArrayList<ArrayList<Integer>> dom) {
		this.constraint = cons;
		this.domains = dom;
	}
	
	public int[] solve() {
		// Initialize assignments to array of zeros. (-1 implies no assignment)
		int[] assignment = new int[this.domains.size()];
		Arrays.fill(assignment, -1);
		// Initialize list of unassigned variables to all variables
		ArrayList<Integer> unassigned = new ArrayList<Integer>();
		for (int i = 0; i < this.domains.size(); i++) {
			unassigned.add(i);
		}
		return backtrack(assignment, unassigned);
	}
	
	private int[] backtrack(int[] assignment, ArrayList<Integer> unassigned) {
		// Test
		for (int i : assignment) {
			System.out.print(i + ", ");
		}
		System.out.println();
		
		if (unassigned.isEmpty()) { return assignment; }
		
		int var = unassigned.remove(unassigned.size() - 1);
		for (int value : domains.get(var)) {
			assignment[var] = value;	// Try this assignment
			// If consistent, continue forward exploration
			if (constraint.isConsistent(assignment, var)) {
				int[] result = backtrack(assignment, unassigned);
				if (result != null) {	// Successful complete assignment
					return result;
				}
			}
		}
		// Didn't work, have to backtrack and come back to this one
		unassigned.add(var);
		return null;
	}
}