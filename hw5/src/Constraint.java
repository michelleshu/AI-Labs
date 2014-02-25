import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Constraint {
	// Allowable values for each pair of variables
	HashMap<Pair, HashSet<Pair>> allowedValues;
	HashSet<Integer> variablesInvolved;
	
	public Constraint(HashMap<Pair, HashSet<Pair>> allowed, 
			HashSet<Integer> involved) {
		this.allowedValues = allowed;
		this.variablesInvolved = involved;
	}
	
	public boolean isSatisfied(ArrayList<Integer> assignment) {
		// For each pair of variables involved, check that values allowed
		for (int i = 0; i < assignment.size(); i++) {
			if (! isInvolved(i) || i == 0) {
				continue;
			}
			for (int j = i + 1; j < assignment.size(); j++) {
				if (isInvolved(j) && j != 0) {
					if (! allowedValues.get(new Pair(i, j)).contains(
						new Pair(assignment.get(i), assignment.get(j)))) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isSatisfiedWith(ArrayList<Integer> assignment, int var) {
		if (! isInvolved(var) || var == 0) {
			return true;
		} 
		for (int other = 0; other < assignment.size(); other++) {
			if (other == var) { continue; }
			if (isInvolved(other) && other != 0) {
				if (! allowedValues.get(new Pair(var, other)).contains(
					new Pair(assignment.get(var), assignment.get(other)))) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Is var involved in the constraint?
	public boolean isInvolved(int var) {
		return variablesInvolved.contains(var);
	}
	
}