package assignment_maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class InformedSearchProblem extends SearchProblem {
	
	public List<SearchNode> astarSearch() {	
			resetStats();
			PriorityQueue<SearchNode> pq = new PriorityQueue<SearchNode>();
			HashMap<SearchNode, SearchNode> visited = 
					new HashMap<SearchNode, SearchNode>();
			
			pq.add(startNode);
			visited.put(startNode, startNode);
			
			while (! pq.isEmpty()) {
				incrementNodeCount();
				updateMemory(pq.size() + visited.size());
				
				SearchNode currentNode = pq.poll();
				SearchNode visitedNode = visited.get(currentNode);
				
				if (visitedNode != null && 
						visitedNode.getCost() < currentNode.getCost()) {
					continue; // ignore if already seen at lower cost.
				}
				
				if (currentNode.goalTest()) { // found goal, return the path
					return backchain(currentNode);
				}
				
				ArrayList<SearchNode> successors = currentNode.getSuccessors();
				
				for (SearchNode n : successors) {
					// Add the node if it has not been visited or if it was
					// previously found via a higher-cost path.
					SearchNode v = visited.get(n);
					if (v == null || v.getCost() > n.getCost()) {
						pq.add(n);
						visited.put(n, n);
						n.setParent(currentNode);
					}
				}
			}
			return null; // no path found
		}
	
	
}
