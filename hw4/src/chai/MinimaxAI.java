package chai;

import java.util.Random;
import chesspresso.move.*;
import chesspresso.position.Position;

/** Depth-limited minimax search 
 *  Standard minimax search - no pruning - stops after reaching max depth
 */

public class MinimaxAI implements ChessAI {
	private int maxDepth;
	private short bestMove = Move.NO_MOVE;
	
	public MinimaxAI(int maxd) {
		this.maxDepth = maxd;
	}
	
	// Get the move from this position that will lead to the maximum utility
	public short getMove(Position position) {
		System.out.println("Position before getMove: " + position);
		double maxValue = Double.MAX_VALUE;
		for (short m : position.getAllMoves()) {
			try {
				position.doMove(m);
				double value = minValue(position, 0);
				if (value > maxValue) {
					maxValue = value;
					bestMove = m;
				}
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		System.out.println("Position after getMove: " + position);
		System.out.println("Best move chosen: " + bestMove);
		return bestMove;
	}
	
	// Return maximum utility value from this position forward
	private double maxValue(Position position, int depth) {
		if (cutOffTest(position, depth)) {
			return utility(position);
		}
		double maxValue = Double.MIN_VALUE;
		for (short m : position.getAllMoves()) {
			try {
				position.doMove(m);
				maxValue = Math.max(maxValue, minValue(position, depth + 1));
				// Undo the move on original position so that we can try another
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return maxValue;
	}
	
	// Return minimum utility value from this position forward
	private double minValue(Position position, int depth) {
		if (cutOffTest(position, depth)) {
			return utility(position);
		}
		double minValue = Double.MAX_VALUE;
		for (short m: position.getAllMoves()) {
			try {
				position.doMove(m);
				minValue = Math.min(minValue, maxValue(position, depth + 1));
				// Undo the move on original position so that we can try another
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return minValue;
	}
	
	// Get utility value of current position
	private double utility(Position position) {
		if (position.isMate()) {
			return Double.MAX_VALUE;
		} else {
			Random rand = new Random();
			return rand.nextDouble() * Double.MAX_VALUE;
		}
	}
	
	// Stop search when cutoff test evaluates to true
	public boolean cutOffTest(Position position, int depth) {
		if (position.isTerminal()) { // no remaining moves from here
			return true;
		}
		if (depth >= this.maxDepth) {
			return true;
		}
		return false;
	}
}