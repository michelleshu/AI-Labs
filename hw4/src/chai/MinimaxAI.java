package chai;

import java.util.Random;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MinimaxAI implements ChessAI {
	
	// Get the move from this position that will lead to the maximum utility
	public short getMove(Position position) {
		double maxValue = Double.MAX_VALUE;
		short bestMove = 0;
		for (short m : position.getAllMoves()) {
			try {
				position.doMove(m);
				double value = minValue(position);
				if (value > maxValue) {
					maxValue = value;
					bestMove = m;
				}
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return bestMove;
	}
	
	// Return maximum utility value from this position forward
	private double maxValue(Position position) {
		if (position.isTerminal() || cutOffTest()) {
			return Double.MAX_VALUE;
		}
		double maxValue = Double.MIN_VALUE;
		for (short m : position.getAllMoves()) {
			try {
				position.doMove(m);
				maxValue = Math.max(maxValue, minValue(position));
				// Undo the move on original position so that we can try another
				position.undoMove();
			} catch(IllegalMoveException e) {
				System.out.println("Tried illegal move in minimax.");
			}
		}
		return maxValue;
	}
	
	// Return minimum utility value from this position forward
	private double minValue(Position position) {
		if (position.isMate() || cutOffTest()) {
			return Double.MAX_VALUE;
		}
		double minValue = Double.MAX_VALUE;
		for (short m: position.getAllMoves()) {
			try {
				position.doMove(m);
				minValue = Math.min(minValue, maxValue(position));
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
	public boolean cutOffTest() {
		return false;
	}
}