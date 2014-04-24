package chai;

import java.util.HashMap;
import java.util.Random;
import chesspresso.move.*;
import chesspresso.position.Position;

/** Iterative deepening minimax search with alpha-beta pruning
 */

public class AlphaBetaTAI implements ChessAI {
	public class TEntry {
		public int depth;
		public int utility;
		public TEntry(int d, int u) {
			depth = d;
			utility = u;
		}
	}
	private HashMap<Long, TEntry> transpositionTable = 
			new HashMap<Long, TEntry>();
	
	private int finalDepth;	// absolute max depth to explore, return after this
	private int color;
	private short bestMove;
	private int bestMoveValue;
	private int nodesExplored = 0;
	private Random rand = new Random();
	
	public AlphaBetaTAI(int c, int fd) {
		this.color = c; 		// color of this player
		this.finalDepth = fd;
	}
	
	public short getMove(Position position) {
		// Reset bestMove and bestMoveValue
		bestMove = Move.NO_MOVE;
		bestMoveValue = - Integer.MAX_VALUE;
		
		if (position.isTerminal()) { return Move.NO_MOVE; }	// Game over
		for (int cutoffDepth = 1; cutoffDepth <= this.finalDepth; cutoffDepth++) {
			getMoveAtDepth(position, cutoffDepth);
			// If timer runs out before we explore at finalDepth, return the
			// best value we have found in allotted time.
		}
		System.out.println("Alpha Beta Transposition Nodes explored: " + nodesExplored);
		//System.out.println("Utility: " + bestMoveValue);
		return bestMove;
	}
	 
	// Get the move from this position that will lead to the maximum utility
	private void getMoveAtDepth(Position position, int cutoffDepth) {
		int bestValueFound = - Integer.MAX_VALUE;
		int value = - Integer.MAX_VALUE;
		short[] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			short m = moves[i];
			try {
				position.doMove(m);
				TEntry prevEntry = transpositionTable.get(position.getHashCode());
				if ((prevEntry != null) && (prevEntry.depth <= 1)) {
					// If we've already computed heuristic to depth needed, 
					// return it and avoid recomputing
					value = prevEntry.utility;
				} else {
					value = minValue(position, - Integer.MAX_VALUE, 
							Integer.MAX_VALUE, 1, cutoffDepth);
				}
				
				if (value > bestValueFound) {
					bestValueFound = value;
				}
				if (value > bestMoveValue) {
					bestMove = m;
					bestMoveValue = value;
				}
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		//System.out.println("Best move found at depth " + cutoffDepth + 
		//		" has value " + bestMoveValue);
		transpositionTable.put(position.getHashCode(), new TEntry(0, bestValueFound));
	}
	
	// Return maximum utility value from this position forward
	private int maxValue(Position position, int alpha, int beta, int depth, int cutoffDepth) {
		if (cutOffTest(position, depth, cutoffDepth)) {
			return utility(position);
		}
		int maxValue = - Integer.MAX_VALUE;
		short[] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			short m = moves[i];
			try {
				position.doMove(m);
				TEntry prevEntry = transpositionTable.get(position.getHashCode());
				if ((prevEntry != null) && (prevEntry.depth <= depth)) {
					// Can use previous result for utility value
					maxValue = Math.max(maxValue, prevEntry.utility);
				} else {
					// Must recursively compute utility
					maxValue = Math.max(maxValue, minValue(position, alpha, beta, depth + 1, cutoffDepth));
				}
				// Undo the move on original position so that we can try another
				position.undoMove();
				if (maxValue >= beta) {
					return maxValue;
				}
				alpha = Math.max(alpha, maxValue);
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in alpha beta minimax.");
			}
		}
		transpositionTable.put(position.getHashCode(), new TEntry(depth, maxValue));
		return maxValue;
	}
	
	// Return minimum utility value from this position forward
	private int minValue(Position position, int alpha, int beta, int depth, int cutoffDepth) {
		if (cutOffTest(position, depth, cutoffDepth)) {
			return utility(position);
		}
		int minValue = Integer.MAX_VALUE;
		short[] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			short m = moves[i];
			try {
				position.doMove(m);
				TEntry prevEntry = transpositionTable.get(position.getHashCode());
				if ((prevEntry != null) && (prevEntry.depth <= depth)) {
					// Can use previous result for utility value
					minValue = Math.min(minValue, prevEntry.utility);
				} else {
					// Must recursively compute utility
					minValue = Math.min(minValue, maxValue(position, alpha, beta, depth + 1, cutoffDepth));
				}
				// Undo the move on original position so that we can try another
				position.undoMove();
				if (minValue <= alpha) {
					return minValue; 
				}
				beta = Math.min(beta, minValue);
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in alpha beta minimax.");
			}
		}
		transpositionTable.put(position.getHashCode(), new TEntry(depth, minValue));
		return minValue;
	}
	
	// Get utility value of current position
	private int utility(Position position) {
		nodesExplored++;
		if (position.isTerminal()) {
			if (position.isMate()) {
				if (position.getToPlay() == this.color) {
					// I lose b/c opponent just checkmated me
					return - Integer.MAX_VALUE;
				} else {	// I win!
					return Integer.MAX_VALUE;
				}
			} else {	// no one wins
				return 0;
			}
		} else {
			//return rand.nextInt(Integer.MAX_VALUE) - (Integer.MAX_VALUE / 2);
			return EvaluationFunction.getMaterialUtility(position, this.color);
		}
	}
	
	// Stop search when cutoff test evaluates to true
	public boolean cutOffTest(Position position, int depth, int cutoffDepth) {
		if (position.isTerminal()) { // no remaining moves from here
			return true;
		}
		if (depth >= cutoffDepth) {
			return true;
		}
		return false;
	}
}