package chai;

import java.util.Random;
import chesspresso.move.*;
import chesspresso.position.Position;

/** Iterative deepening minimax search 
 *  Standard minimax - no pruning
 *  Explores at iteratively increasing depth
 */

public class MinimaxAI implements ChessAI {
	private int finalDepth;	// absolute max depth to explore, return after this
	private int color;
	private short bestMove = Move.NO_MOVE;
	private Random rand = new Random();
	
	public MinimaxAI(int c, int fd) {
		this.color = c; 		// color of this player
		this.finalDepth = fd;
	}
	
	public short getMove(Position position) {
		for (int cutoffDepth = 1; cutoffDepth <= this.finalDepth; cutoffDepth++) {
			this.bestMove = getMoveAtDepth(position, cutoffDepth);
			// If timer runs out before we explore at finalDepth, return the
			// best value we have found in allotted time.
		}
		return this.bestMove;
	}
	 
	// Get the move from this position that will lead to the maximum utility
	private short getMoveAtDepth(Position position, int cutoffDepth) {
		if (position.isTerminal()) { return -1;	} // Game over
		
		short bestMoveAtDepth = Move.NO_MOVE;
		int maxValue = - Integer.MAX_VALUE;
		int value;

		short[] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			short m = moves[i];
			try {
				position.doMove(m);
				value = minValue(position, 1, cutoffDepth);
				if (value > maxValue) {
					maxValue = value;
					bestMoveAtDepth = m;
				}
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return bestMoveAtDepth;
	}
	
	// Return maximum utility value from this position forward
	private int maxValue(Position position, int depth, int cutoffDepth) {
		if (cutOffTest(position, depth, cutoffDepth)) {
			return utility(position);
		}
		int maxValue = - Integer.MAX_VALUE;
		short[] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			short m = moves[i];
			try {
				position.doMove(m);
				maxValue = Math.max(maxValue, minValue(position, depth + 1, cutoffDepth));
				// Undo the move on original position so that we can try another
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return maxValue;
	}
	
	// Return minimum utility value from this position forward
	private int minValue(Position position, int depth, int cutoffDepth) {
		if (cutOffTest(position, depth, cutoffDepth)) {
			return utility(position);
		}
		int minValue = Integer.MAX_VALUE;
		short[] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			short m = moves[i];
			try {
				position.doMove(m);
				minValue = Math.min(minValue, maxValue(position, depth + 1, cutoffDepth));
				// Undo the move on original position so that we can try another
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return minValue;
	}
	
	// Get utility value of current position
	private int utility(Position position) {
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
			return rand.nextInt(Integer.MAX_VALUE / 2) - Integer.MAX_VALUE;
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