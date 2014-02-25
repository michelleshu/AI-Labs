/** Generic Constraint Satisfaction Problem Solver
 *  Solves CSPs with variables represented as indices in array, values as ints
 */

import java.util.ArrayList;
import java.util.Collections;

public class ConstraintSatisfactionProblem {
	Constraint constraint;
	ArrayList<ArrayList<Integer>> domains;
	ArrayList<Integer> assignment;
	ArrayList<Integer> unassigned;	// variables that have yet to be assigned
	
	public ConstraintSatisfactionProblem() {
	}
	
	public ConstraintSatisfactionProblem(Constraint cons, 
		ArrayList<ArrayList<Integer>> dom) {
		this.constraint = cons;
		this.domains = dom;
		// Initialize assignments to array of zeros. (0 implies no assignment)
		this.assignment = 
				new ArrayList<Integer>(Collections.nCopies(dom.size(), 0));
		for (int i = 0; i < assignment.size(); i++) {
			unassigned.add(i);
		}
	}
	
	private ArrayList<Integer> backtrack() {
		if (unassigned.isEmpty()) { return assignment; }
		int var = unassigned.remove(unassigned.size() - 1);
		for (int value : domains.get(var)) {
			assignment.set(var, value);	// Try this assignment
			
		}
	}
}