/** UUSearchProblem.java
 * Template from Devin Balkcom, modified by Michelle Shu
 * January 11, 2014
 */

package cannibals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class UUSearchProblem {
	
	// used to store performance information about search runs.
	//  these should be updated during the process of searches

	// see methods later in this class to update these values
	protected int nodesExplored;
	protected int maxMemory;

	protected UUSearchNode startNode;
	
	protected interface UUSearchNode {
		public ArrayList<UUSearchNode> getSuccessors();
		public boolean goalTest();
		public int getDepth();
	}

	// breadthFirstSearch:  return a list of connecting Nodes, or null
	// no parameters, since start and goal descriptions are problem-dependent.
	//  therefore, constructor of specific problems should set up start
	//  and goal conditions, etc.
	
	public List<UUSearchNode> breadthFirstSearch() {
		resetStats();
		
		// Queue of nodes to visit next
		LinkedList<UUSearchNode> queue = new LinkedList<UUSearchNode>();
		// Track predecessors of visited nodes in shortest path
		HashMap<UUSearchNode, UUSearchNode> predecessorOf = 
				new HashMap<UUSearchNode, UUSearchNode>(); 
		
		queue.addLast(startNode);
		while (!queue.isEmpty()) {
			UUSearchNode n = queue.removeFirst();
			if (n.goalTest()) {	// goal found!
				return backchain(n, predecessorOf);
			}
			ArrayList<UUSearchNode> successors = n.getSuccessors();
			for (UUSearchNode s : successors) {
				if (! predecessorOf.containsKey(s)) {
					queue.addLast(s);
					predecessorOf.put(s, n);
					incrementNodeCount();
					updateMemory(queue.size() + predecessorOf.size());
				}
			}
		}
		return null;	// no goal found
	}
	
	// backchain should only be used by bfs, not the recursive dfs
	private LinkedList<UUSearchNode> backchain(UUSearchNode node,
			HashMap<UUSearchNode, UUSearchNode> visited) {
		LinkedList<UUSearchNode> path = new LinkedList<UUSearchNode>();
		UUSearchNode current = node;
		while (visited.get(current) != startNode) {
			path.addFirst(current);
			current = visited.get(current);
		}
		path.addFirst(current);
		path.addFirst(startNode);
		return path;
	}

	public LinkedList<UUSearchNode> depthFirstMemoizingSearch(int maxDepth) {
		resetStats(); 
		HashMap<UUSearchNode, Integer> visited = new HashMap<UUSearchNode, Integer>();
		visited.put(startNode, 1);
		return dfsrm(startNode, visited, 0, maxDepth);
	}

	private LinkedList<UUSearchNode> dfsrm(UUSearchNode currentNode, 
			HashMap<UUSearchNode, Integer> visited, int depth, int maxDepth) {
		updateMemory(visited.size());
		incrementNodeCount();
	
		if (currentNode.goalTest()) { // Base case: We found the goal!
			LinkedList<UUSearchNode> path = new LinkedList<UUSearchNode>();
			path.add(currentNode);
			return path;
		}
		
		// Keep looking recursively through successors not already visited.
		if (depth < maxDepth) {
			ArrayList<UUSearchNode> successors = currentNode.getSuccessors();
			for (UUSearchNode s : successors) {
				if (! visited.containsKey(s)) {
					visited.put(s, 1);
					LinkedList<UUSearchNode> path = dfsrm(s, visited, depth + 1, maxDepth);
					if (path != null) {
						path.addFirst(currentNode);
						return path;
					}
				}
			}
		}
		return null; // Goal not reachable from currentNode
	}
	
	
	// set up the iterative deepening search, and make use of dfspc
	public LinkedList<UUSearchNode> IDSearch(int maxDepth) {
		resetStats();
		LinkedList<UUSearchNode> path = null;
		int depth = 0;
		while ((path == null) && (depth <= maxDepth)) {	// Search at increasing depth
			path = depthFirstPathCheckingSearch(depth);
			depth++;
		}
		return path;
	}

	// set up the depth-first-search (path-checking version), 
	//  but call dfspc to do the real work
	public LinkedList<UUSearchNode> depthFirstPathCheckingSearch(int maxDepth) {
		resetStats();
		HashSet<UUSearchNode> currentPath = new HashSet<UUSearchNode>();
		currentPath.add(startNode);
		return dfsrpc(startNode, currentPath, 0, maxDepth);
	}

	// recursive path-checking dfs. Private, because it has the extra
	// parameters needed for recursion.
	private LinkedList<UUSearchNode> dfsrpc(UUSearchNode currentNode, 
			HashSet<UUSearchNode> currentPath, int depth, int maxDepth) {
		updateMemory(currentPath.size());
		incrementNodeCount();
	
		if (currentNode.goalTest()) { // Base case: We found the goal!
			LinkedList<UUSearchNode> path = new LinkedList<UUSearchNode>();
			path.add(currentNode);
			return path;
		}
		
		// Keep looking recursively through successors not already visited.
		if (depth < maxDepth) {
			ArrayList<UUSearchNode> successors = currentNode.getSuccessors();
			for (UUSearchNode s : successors) {
				if (! currentPath.contains(s)) {
					currentPath.add(s);
					LinkedList<UUSearchNode> path = 
							dfsrpc(s, currentPath, depth + 1, maxDepth);
					if (path != null) {
						path.addFirst(currentNode);
						return path;
					}
					currentPath.remove(s);
				}
			}
		}
		return null; // Goal not reachable from currentNode
	}

	protected void resetStats() {
		nodesExplored = 0;
		maxMemory = 0;
	}
	
	protected void printStats() {
		System.out.println("Nodes explored during last search: " + nodesExplored);
		System.out.println("Maximum memory usage during last search: " + maxMemory);
	}
	
	protected void updateMemory(int currentMemory) {
		maxMemory = Math.max(currentMemory, maxMemory);
	}
	
	protected void incrementNodeCount() {
		nodesExplored++;
	}

}
