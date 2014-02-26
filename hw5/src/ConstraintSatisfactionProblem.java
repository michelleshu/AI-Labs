/** Generic Constraint Satisfaction Problem Solver
 *  Solves CSPs with variables represented as indices in array, values as ints
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ConstraintSatisfactionProblem {
	
	public static final boolean useMRV = true;
	
	public Constraint constraint;
	public ArrayList<ArrayList<Integer>> domains;
//	private PriorityQueue<Integer> minRemainingValues;
//	
//	public class MRVComparator implements Comparator<Integer> {
//		public int compare(Integer a, Integer b) {
//			return domains.get(a).size() - domains.get(b).size();
//		}
//	}
	
	public ConstraintSatisfactionProblem() {
		
	}
	
	public ConstraintSatisfactionProblem(Constraint cons, 
		ArrayList<ArrayList<Integer>> dom) {
		this.constraint = cons;
		this.domains = dom;
		
		// For heuristics
//		if (useMRV) {
//			this.minRemainingValues = new PriorityQueue<Integer>(domains.size(), 
//					new MRVComparator());
//			for (int i = 0; i < domains.size(); i++) {
//				minRemainingValues.add(i);
//			}
//		}
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
		if (unassigned.isEmpty()) { return assignment; }
		
		int var = unassigned.get(0);
		if (useMRV) {
			// Do a linear search of remaining vars to find most constrained one
			int minValues = Integer.MAX_VALUE;
			for (int u : unassigned) {
				if (this.domains.get(u).size() < minValues) {
					minValues = this.domains.get(u).size();
					var = u;
				}
			}
			unassigned.remove((Integer) var);
		} else {
			var = unassigned.remove(unassigned.size() - 1);
		}
		for (int value : domains.get(var)) {
			assignment[var] = value;	
			/* Trying the assignment var = value
			   By making this assignment, modifying domains of some variables.
			   Modify domains but save their old values in hash table in case
			   we have to backtrack. */
			HashMap<Integer, ArrayList<Integer>> oldValues = 
							updateDomains(var, value, unassigned);
			
			// If consistent, continue forward exploration
			if (constraint.isConsistent(assignment, var)) {
				int[] result = backtrack(assignment, unassigned);
				if (result != null) {	// Successful complete assignment
					return result;
				}
			}
			// If not consistent, reset old domains and try new value
			restoreOldDomains(oldValues);
		}
		// No working assignments, have to backtrack and come back to this var
		unassigned.add(var);
		return null;
	}
	
	private HashMap<Integer, ArrayList<Integer>> updateDomains(int assignedVar, 
			int assignedValue, ArrayList<Integer> unassigned) {
		HashMap<Integer, ArrayList<Integer>> oldValues = 
							new HashMap<Integer, ArrayList<Integer>>();
		
		for (int other : unassigned) {
			HashSet<Pair> allowedPairs = 
				this.constraint.allowedValues.get(new Pair(assignedVar, other));
			
			if (allowedPairs != null) {
				// Save domain before modification
				oldValues.put(other, new ArrayList<Integer>(this.domains.get(other)));
				// Now clear out other's domain
				this.domains.set(other, new ArrayList<Integer>());
				// Get allowed values for other, with assignedVar = assignedValue
				for (Pair valuePair : allowedPairs) {
					if (valuePair.a == assignedValue) {
						this.domains.get(other).add(valuePair.b);
					}
				}
			}
		}
		// Return copy of old values
		return oldValues;
	}
	
	/* Backtrack: Replace domains with their old values */
	private void restoreOldDomains(HashMap<Integer, ArrayList<Integer>> oldValues) {
		Iterator<Integer> iter = oldValues.keySet().iterator();
		while (iter.hasNext()) {
			int var = iter.next();
			this.domains.set(var, oldValues.get(var));
		}
	}

	public static void main(String[] args) {
		
		// Simple test case:
		HashMap<Pair, HashSet<Pair>> allowedValues = new HashMap<Pair, HashSet<Pair>>();
		HashSet<Pair> set = new HashSet<Pair>();
		set.add(new Pair(0, 1));
		set.add(new Pair(0, 2));
		set.add(new Pair(1, 0));
		set.add(new Pair(1, 2));
		set.add(new Pair(2, 0));
		set.add(new Pair(2, 1));
		allowedValues.put(new Pair(0, 1), new HashSet<Pair>(set));
		allowedValues.put(new Pair(1, 0), new HashSet<Pair>(set));
		
		HashSet<Integer> variablesInvolved = new HashSet<Integer>();
		variablesInvolved.add(0);
		variablesInvolved.add(1);
		
		Constraint c = new Constraint(allowedValues, variablesInvolved);
		
		ArrayList<Integer> d1 = new ArrayList<Integer>();
		d1.add(0);
		d1.add(1);
		d1.add(2);
		
		ArrayList<Integer> d2 = new ArrayList<Integer>();
		d2.add(0);
		d2.add(1);
		d2.add(2);
		
		ArrayList<ArrayList<Integer>> domain = new ArrayList<ArrayList<Integer>>();
		domain.add(d1);
		domain.add(d2);
		
		ConstraintSatisfactionProblem csp = new ConstraintSatisfactionProblem(c, domain);
		
		ArrayList<Integer> unassigned = new ArrayList<Integer>();
		unassigned.add(1);
		
		HashMap<Integer, ArrayList<Integer>> old = csp.updateDomains(0, 1, unassigned);
		for (ArrayList<Integer> d : csp.domains) {
			System.out.print("{");
			for (int i : d) {
				System.out.print(i + ", ");
			}
			System.out.print("}\n");
		}
		
		System.out.println(old.size());
		System.out.println(old.get(1));
		csp.restoreOldDomains(old);
		
		for (ArrayList<Integer> d : csp.domains) {
			System.out.print("{");
			for (int i : d) {
				System.out.print(i + ", ");
			}
			System.out.print("}\n");
		}
	}
}