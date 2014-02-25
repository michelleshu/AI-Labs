/** The map coloring problem */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MapColoringCSP extends ConstraintSatisfactionProblem {
	
	HashMap<String, ArrayList<String>> graph; // adj list representation
	// mapping from variable name to int
	HashMap<String, Integer> variableToInt = new HashMap<String, Integer>();
	// mapping from int to variable name
	HashMap<Integer, String> intToVariable = new HashMap<Integer, String>();
	HashMap<String, Integer> colorToInt = new HashMap<String, Integer>();
	HashMap<Integer, String> intToColor = new HashMap<Integer, String>();
	
	public MapColoringCSP(String filename) {
		// Load graph
		loadGraphFromFile(filename);
		// Generate constraint
		this.constraint = generateColorConstraint();
		// Generate domains
		this.domains = generateDomains();
		
		this.unassigned = new ArrayList<Integer>();
		// Initialize assignments to array of zeros. (0 implies no assignment)
		this.assignment = 
				new ArrayList<Integer>(Collections.nCopies(this.domains.size(), 0));
		for (int i = 0; i < this.assignment.size(); i++) {
			this.unassigned.add(i);
		}
		
		// Tests to be printed

	}
	
	
	private void loadGraphFromFile(String filename) {
		BufferedReader br = null;
		ArrayList<String> values = new ArrayList<String>();
		HashMap<String, ArrayList<String>> adjacencyList = 
				new HashMap<String, ArrayList<String>>();
		
		try {
			br = new BufferedReader(new FileReader(filename));
			
			// Read variable values
			String[] vals = br.readLine().split(",");
			for (String v : vals) { values.add(v.trim()); }
			adjacencyList.put("values", values);
			
			// Add adjacency list
			String vertex;
			String[] adjacentVerts;
			while ((vertex = br.readLine()) != null) {
				adjacencyList.put(vertex.trim(), new ArrayList<String>());
				adjacentVerts = br.readLine().split(",");
				for (String a : adjacentVerts) {
					adjacencyList.get(vertex.trim()).add(a.trim());
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
		this.graph = adjacencyList;
	}
	
	private ArrayList<ArrayList<Integer>> generateDomains() {
		ArrayList<ArrayList<Integer>> domains = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> allColors = new ArrayList<Integer>();
		for (String color : colorToInt.keySet()) {
			allColors.add(colorToInt.get(color));
		}
		for (int i = 0; i <= variableToInt.size(); i++) {
			domains.add(i, allColors);
		}
		return domains;
	}
	
	private Constraint generateColorConstraint() {
		// Map variable names to integers and store mapping in hash tables
		// variableToInt and intToVariable
		this.initializeVariableMapping(this.graph.keySet());
		// Map colors (variable values) to integers also
		this.initializeColorMapping(this.graph.get("values"));
		
		// Get all valid pairs of colors (no repeats)
		HashSet<Pair> validPairs = getValidColorPairs();
		
		// Define constraint as set of allowed values for each possible pair
		// and set of variables involved in the constraint
		HashMap<Pair, HashSet<Pair>> allowedValues = 
				new HashMap<Pair, HashSet<Pair>>();
		HashSet<Integer> variablesInvolved = new HashSet<Integer>();
		
		Set<String> vertices = this.graph.keySet();
		for (String vertex : vertices) {
			if (vertex.equals("values")) {
				continue;
			}
			int vi = variableToInt.get(vertex);
			variablesInvolved.add(vi);
			for (String neighbor : this.graph.get(vertex)) {
				if (neighbor.equals("none")) { 
					// No neighbors, this variable not involved in constraint
					variablesInvolved.remove(vi);
					break; 
				}
				int ni = variableToInt.get(neighbor);
				Pair vPair = new Pair(vi, ni);
				allowedValues.put(vPair, validPairs);
			}
		}
		
		Constraint cons = new Constraint(allowedValues, variablesInvolved);
		return cons;
	}
	
	private void initializeVariableMapping(Set<String> variableNames) {
		int i = 1;
		for (String v : variableNames) {
			if (v != "values") {
				this.variableToInt.put(v, i);
				this.intToVariable.put(i, v);
				i++;
			} 
		}
	}
	
	private void initializeColorMapping(ArrayList<String> colors) {
		int i = 1;
		for (String c : colors) {
			this.colorToInt.put(c, i);
			this.intToColor.put(i, c);
			i++;
		}
	}
	
	private HashSet<Pair> getValidColorPairs() {
		HashSet<Pair> validPairs = new HashSet<Pair>();
		int numColors = this.colorToInt.size();
		for (int i = 0; i < numColors; i++) {
			for (int j = 0; j < numColors; j++) {
				if (i != j) {
					validPairs.add(new Pair(i, j));
				}
			}
		}
		return validPairs;
	}
	
	public static void main(String[] args) {
		MapColoringCSP mc = new MapColoringCSP("src/Australia.txt");
	}
}