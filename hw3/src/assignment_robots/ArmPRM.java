package assignment_robots;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import assignment_robots.World;
import assignment_robots.ArmLocalPlanner;
import assignment_robots.ArmRobot;

/**
 * Probabilistic Roadmap Generator for robotic arm
*/

public class ArmPRM {
	
	public static final int NUM_RAND_SAMPLES = 500; 
	public static final int NUM_ARM_LINKS = 3;
	public static final double TOL = 0.2; // minimum acceptable distance
										   // between two configs in path
	public static final int K = 100; // # of nearest neighbors to explore
	
	public static HashMap<ArmRobot, ArrayList<ArmRobot>> buildPRM(World w) {
		// The graph G is represented as an adjacency list
		HashMap<ArmRobot, ArrayList<ArmRobot>> G = 
				new HashMap<ArmRobot, ArrayList<ArmRobot>>();
		
		// Get random sequence of configuration states to try
		ArrayList<ArmRobot> randomStates = getRandomConfigs(NUM_ARM_LINKS, w);
		
		// Add all non-collision states to graph
		for (ArmRobot state : randomStates) {
			G.put(state, new ArrayList<ArmRobot>());
		}
		
		for (ArmRobot state : G.keySet()) {
			PriorityQueue<ArmRobot> KNN = getNearestNeighbors(state, G.keySet());
			for (int i = 0; i < KNN.size(); i++) {
				ArmRobot neighbor = KNN.poll();
				if ((! w.armCollisionPath(new ArmRobot(NUM_ARM_LINKS), 
					state.getConfig(), neighbor.getConfig())) && 
					(! inSameComponent(G, state, neighbor))) {
					// If these configs can be connected and are not
					// already connected, connect them.
					G.get(state).add(neighbor);
					G.get(neighbor).add(state);
				}
			}
		}
		
		return G;
	}
	
	public static boolean inSameComponent(HashMap<ArmRobot, ArrayList<ArmRobot>> G, 
			ArmRobot a, ArmRobot b) {
		
		return (getBFSPath(G, a, b) != null);
	}
	
	public static PriorityQueue<ArmRobot> getNearestNeighbors(ArmRobot a, 
			Set<ArmRobot> neighbors) {
		PriorityQueue<ArmRobot> kNearest = new PriorityQueue<ArmRobot>(
				new KNNComparator(a));
		
		// Start priority queue by adding first K nodes in graph
		Iterator<ArmRobot> iter = neighbors.iterator();			
		for (int i = 0; i < K; i++) {
			ArmRobot n = iter.next();
			if (! n.equals(a)) {
				kNearest.add(n);
			} else { i--; }
		}
		// For remaining nodes, add only if they are possibly one of the K
		// shortest distances to the source node. (i.e. < max of our heap)
		while (iter.hasNext()) {
			ArmRobot next = iter.next();
			if (distance(a, next) < distance(a, kNearest.peek())) {
				kNearest.poll();
				kNearest.add(next);
			}
		}
		return kNearest;
	}
	
	public static class KNNComparator implements Comparator<ArmRobot> {
		public ArmRobot source;
		
		public KNNComparator(ArmRobot s) { // compare by distance to source robot
			this.source = s;
		}
		
		// Prioritize robot with larger distance to source for max heap
		// So return negative number if dist(a, s) > dist(b, s).
		public int compare(ArmRobot a, ArmRobot b) {
			double diff = distance(b, this.source) - distance(a, this.source);
			if (diff < 0) return -1;
			else if (diff == 0) return 0;
			else return 1;
		}	
	}
	
	// Helper method to get Euclidean distance between two configurations
	public static double distance(ArmRobot a, ArmRobot b) {
		double[] configA = a.getConfig();
		double[] configB = b.getConfig();
		double squaredSum = 0;
		for (int i = 0; i < configA.length; i++) {
			double d = Math.abs(configA[i] - configB[i]);
			// Take shortest distance of no wrap v wrap
			squaredSum += Math.pow(Math.min(d, (2 * Math.PI) - d), 2);
		}
		return Math.sqrt(squaredSum);
	}
	
	// Get the node in our PRM that has the closest configuration to robot's
	public static ArmRobot getClosestPRMNode(
		HashMap<ArmRobot, ArrayList<ArmRobot>> PRM, ArmRobot robot) {
		Iterator<ArmRobot> iter = PRM.keySet().iterator();
		ArmRobot closest = iter.next();
		KNNComparator comp = new KNNComparator(robot);
		while (iter.hasNext()) {
			ArmRobot current = iter.next();
			if (comp.compare(current, closest) > 0) {
				closest = current;
			}
		}
		return closest;
	}
	
	// Generate samples for configuration space using random number generator
	public static ArrayList<ArmRobot> getRandomConfigs(int links, World w) {
		// Store configurations used to ensure no duplicates
		HashSet<ArmRobot> configsAdded = new HashSet<ArmRobot>();
		Random rand = new Random();
		ArrayList<ArmRobot> randConfigs = new ArrayList<ArmRobot>(NUM_RAND_SAMPLES);
		
		for (int c = 0; c < NUM_RAND_SAMPLES; c++) {
			ArmRobot newRobot = new ArmRobot(links);			
			for (int l = 0; l < links; l++) {
				newRobot.setLinkAngle(l, rand.nextDouble() * (2 * Math.PI));
			}
			
			if (configsAdded.contains(newRobot) || (w.armCollision(newRobot))) {
				c--;	// don't count this one
			} else {
				randConfigs.add(newRobot);
				configsAdded.add(newRobot);
			}
		}
		return randConfigs;
	}

	// Get the full path from configuration start configuration to end
	// configuration  from a PRM (BFS to get shortest path)
	public static LinkedList<ArmRobot> getBFSPath(
		HashMap<ArmRobot, ArrayList<ArmRobot>> PRM, ArmRobot start, ArmRobot end) {
		
		// Queue of nodes to visit next
		LinkedList<ArmRobot> queue = new LinkedList<ArmRobot>();
		// Track predecessors of visited nodes in shortest path
		HashMap<ArmRobot, ArmRobot> prev = new HashMap<ArmRobot, ArmRobot>(); 
		
		queue.addLast(start);
		while (!queue.isEmpty()) {
			ArmRobot n = queue.removeFirst();
			if (n == end) {	
				return backchain(start, end, prev);
			}
			ArrayList<ArmRobot> successors = PRM.get(n);
			for (ArmRobot s : successors) {
				if (! prev.containsKey(s)) {
					queue.addLast(s);
					prev.put(s, n);
				}
			}
		}
		return null;	// no goal found
	}
	
	private static LinkedList<ArmRobot> backchain(ArmRobot start, ArmRobot goal,
			HashMap<ArmRobot, ArmRobot> prev) {
		LinkedList<ArmRobot> path = new LinkedList<ArmRobot>();
		ArmRobot current = goal;
		while (! prev.get(current).equals(start)) {
			// Use local planner to get subpath
			//if (distance(current, prev.get(current)) > TOL) {
			//	appendLocalPath(path, current, prev.get(current));
			//}
			path.addFirst(current);
			current = prev.get(current);
		}
		//if (distance(current, prev.get(current)) > TOL) {
		//	appendLocalPath(path, current, prev.get(current));
		//}
		path.addFirst(start);
		return path;
	}
	
	public static void appendLocalPath(LinkedList<ArmRobot> path, 
			ArmRobot a, ArmRobot b) {
		double[] velocity = ArmLocalPlanner.getPath(a.getConfig(), b.getConfig());
		ArmRobot a1 = new ArmRobot(NUM_ARM_LINKS); // copy of a
		a1.set(a.getConfig());
		while (distance(a1, b) > TOL) {
			ArmRobot r = new ArmRobot(NUM_ARM_LINKS);
			r.set(a1.getConfig());
			path.addFirst(r);
			for (int dim = 0; dim < a.config.length; dim++) {
				a1.config[dim] += velocity[dim];
			}
		}
	}
	
	public static void main(String[] args) {
//		World w = new World();
//		HashMap<ArmRobot, ArrayList<ArmRobot>> PRM = buildPRM(w);
//		System.out.println("All configs: ");
//		for (ArmRobot r : PRM.keySet()) {
//			System.out.println(r);
//		}
//		System.out.println();
//		
//		ArmRobot a = new ArmRobot(2);
//		double[] initConfig = {0, 0};
//		a.set(initConfig);
//		
//		ArmRobot closest = getClosestPRMNode(PRM, a);
//		System.out.println("a = " + a);
//		System.out.println("closest = " + closest);
	}
	
}