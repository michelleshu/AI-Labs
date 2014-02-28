/** Generic Constraint Satisfaction Problem Solver
 *  Solves CSPs with variables represented as indices in array, values as ints
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class ConstraintSatisfactionProblem {
	
	public static final boolean useMRV = true;
	public static final boolean useLCV = false;
	public static final boolean useFC = true;
	public static final boolean useMAC = false;
	
	public Constraint constraint;
	public ArrayList<ArrayList<Integer>> domains;
	public int nodesVisited;
	
	public ConstraintSatisfactionProblem() {
		
	}
	
	public ConstraintSatisfactionProblem(Constraint cons, 
		ArrayList<ArrayList<Integer>> dom) {
		this.constraint = cons;
		this.domains = dom;
	}
	
	public int[] solve2() {
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

	private int[] backtrack2(int[] assignment, ArrayList<Integer> unassigned) {
		nodesVisited++;
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

	public int[] solve() {
		nodesVisited = 0;
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
		nodesVisited++;
		if (unassigned.isEmpty()) { return assignment; }
		
		int var = selectUnassignedVariable(unassigned);
		ArrayList<Integer> values = orderDomainValues(var, unassigned);
		
		for (int i = 0; i < values.size(); i++) {
			int value = values.get(i);
			assignment[var] = value;
			/* Trying the assignment var = value
			   By making this assignment, modifying domains of some variables.
			   Modify domains but save their old values in hash table in case
			   we have to backtrack. */
			HashMap<Integer, ArrayList<Integer>> oldDomains = 
					new HashMap<Integer, ArrayList<Integer>>();
			if (useFC) {
				oldDomains = updateDomains(var, value, unassigned);
			} else if (useMAC) {
				oldDomains = updateMACDomains(var, value, unassigned);
				if (oldDomains == null) { // this assignment not going to work
					return null;
				}
			}
			
			// If consistent, continue forward exploration
			if (constraint.isConsistent(assignment, var)) {
				int[] result = backtrack(assignment, unassigned);
				if (result != null) {	// Successful complete assignment
					return result;
				}
			}
			// If not consistent, reset old domains and try new value
			if (useFC || useMAC) {
				restoreOldDomains(oldDomains);
			}
		}
		
		// No working assignments, have to backtrack and come back to this var
		unassigned.add(var);
		assignment[var] = -1;
		return null;
	}
	
	private int selectUnassignedVariable(ArrayList<Integer> unassigned) {
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
		return var;
	}
	
	private ArrayList<Integer> orderDomainValues(int var, ArrayList<Integer> unassigned) {
		ArrayList<Integer> orderedValues = 
			new ArrayList<Integer>(this.domains.get(var).size());
		if (useLCV) {
			/* Sorting the values by how much the assignment would affect
			 * neighboring states. First, for each value, compute the number
			 * of values deleted from neighboring domains. Store this by 
			 * creating pair (value, constraining effect size). Sort the pairs.
			 */
			
			ArrayList<Pair> valueEffects = new ArrayList<Pair>(); 
			for (int value : this.domains.get(var)) {
				HashMap<Integer, ArrayList<Integer>> origValues = 
						updateDomains(var, value, unassigned);
				// Get number of domain deletions
				int diff = getSizeDifference(origValues);
				valueEffects.add(new Pair(value, diff));
				restoreOldDomains(origValues);
			}
			Collections.sort(valueEffects, new DomainValComparator());
			for (int i = 0; i < valueEffects.size(); i++) {
				orderedValues.add(i, valueEffects.get(i).a);
			}
		} else {
			orderedValues = this.domains.get(var); // no particular order
		}
		return orderedValues;
	}
	
	public class DomainValComparator implements Comparator<Pair> {
		public int compare(Pair p1, Pair p2) {
			return p1.b - p2.b;
		}
	}
	
	private int getSizeDifference(HashMap<Integer, ArrayList<Integer>> origValues) {
		Iterator<Integer> iter = origValues.keySet().iterator();
		int diff = 0;
		while (iter.hasNext()) {
			int var = iter.next();
			diff += origValues.get(var).size() - this.domains.get(var).size();
		}
		return diff;
	}
	
	private HashMap<Integer, ArrayList<Integer>> updateDomains(int assignedVar, 
			int assignedValue, ArrayList<Integer> unassigned) {
		HashMap<Integer, ArrayList<Integer>> oldDomains = 
							new HashMap<Integer, ArrayList<Integer>>();
		
		for (int other : unassigned) {
			HashSet<Pair> allowedPairs = 
				this.constraint.allowedValues.get(new Pair(assignedVar, other));
			
			if (allowedPairs != null) {
				// Save domain before modification
				oldDomains.put(other, new ArrayList<Integer>(this.domains.get(other)));
				// Now clear out other's domain
				this.domains.set(other, new ArrayList<Integer>());
				// Get allowed values for other, with assignedVar = assignedValue
				for (Pair valuePair : allowedPairs) {
					if (valuePair.a == assignedValue && 
							oldDomains.get(other).contains(valuePair.b)) {
						this.domains.get(other).add(valuePair.b);
					}
				}
			}
		}
		// Return copy of old values
		return oldDomains;
	}
	
	private HashMap<Integer, ArrayList<Integer>> updateMACDomains(int assignedVar, 
			int assignedValue, ArrayList<Integer> unassigned) {
		HashMap<Integer, ArrayList<Integer>> oldDomains = 
				new HashMap<Integer, ArrayList<Integer>>();
		LinkedList<Pair> q = new LinkedList<Pair>();
		
		// Add all arcs incident on this last assignment to the queue
		for (int other : unassigned) {
			if (this.constraint.allowedValues.containsKey(new Pair(other, assignedVar))) {
				q.addLast(new Pair(other, assignedVar));
				oldDomains.put(other, new ArrayList<Integer>(this.domains.get(other)));
			}
		}
		
		while (! q.isEmpty()) {
			Pair current = q.removeFirst();
			if (revise(current)) {
				if (this.domains.get(current.a).isEmpty()) { 
					restoreOldDomains(oldDomains);
					return null;
				}
				for (int other : unassigned) {
					if (this.constraint.allowedValues.containsKey(
							new Pair(other, current.a))) {
						if (other != current.b) {
							q.addLast(new Pair(current.a, other));
							oldDomains.put(other, new ArrayList<Integer>(this.domains.get(other)));
						}
					}
				}
			}
		}
		return oldDomains;
	}
	
	/* Return true if we revise the domain of p.a */
	private boolean revise(Pair p) {
		boolean revised = false;
		HashSet<Pair> allowedPairs = this.constraint.allowedValues.get(p);
		ArrayList<Integer> aDomain = new ArrayList<Integer>(this.domains.get(p.a));
		ArrayList<Integer> bDomain = new ArrayList<Integer>(this.domains.get(p.b));
		for (int i : aDomain) {
			boolean allowed = false;
			for (int j : bDomain) {
				if (allowedPairs != null && allowedPairs.contains(new Pair(i, j))) {
					allowed = true;	// allowable, i can remain in p.a's domain
				}
			}
			// No acceptable value for i found, remove i
			if (! allowed) {
				this.domains.get(p.a).remove((Integer) i);
				revised = true;
			}
		}
		return revised;
	}
	
	/* Backtrack: Replace domains with their old values */
	private void restoreOldDomains(HashMap<Integer, ArrayList<Integer>> oldDomains) {
		Iterator<Integer> iter = oldDomains.keySet().iterator();
		while (iter.hasNext()) {
			int var = iter.next();
			this.domains.set(var, oldDomains.get(var));
		}
	}
}