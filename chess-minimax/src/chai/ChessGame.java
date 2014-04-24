package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class ChessGame {

	public Position position;

	public int rows = 8;
	public int columns = 8;

	public ChessGame() {
		position = new Position(
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		
//		for (int row = 0; row < 8; row++) {
//			for (int col = 0; col < 8; col++) {
//				System.out.println(getStone(col, row));
//			}
//		}
		
		// Mate in 1
		//position = new Position("k2b4/2pp4/1Q6/7r/5Bq1/5pP1/PP3P1P/4nRK1 b - - 0 1");
		
		// Mate in 2
		//position = new Position("r5r1/7Q/2RNbkN1/5pp1/p7/3q4/PP3PPP/4R1K1 w - - 0 1");
		
		// Mate in 3
		//position = new Position(
		//		"r4k2/pppb1Pp1/2np3p/2b5/2B2Bnq/2N5/PP2Q1PP/4RR1K w - - 0 1");
		
		// Error positions
		//position = new Position("8/7k/P6P/2P3Q1/8/4K3/8/8 b - - 0 116");

	}

	public int getStone(int col, int row) {
		return position.getStone(Chess.coorToSqi(col, row));
	}
	
	public boolean squareOccupied(int sqi) {
		return position.getStone(sqi) != 0;
		
	}

	public boolean legalMove(short move) {
		
		for(short m: position.getAllMoves()) {
			if(m == move) return true;
		}
		//System.out.println(java.util.Arrays.toString(position.getAllMoves()));
		//System.out.println(move);
		return false;
	
	}

	// find a move from the list of legal moves from fromSqi to toSqi
	// return 0 if none available
	public short findMove(int fromSqi, int toSqi) {
		
		for(short move: position.getAllMoves()) {
			if(Move.getFromSqi(move) == fromSqi && 
					Move.getToSqi(move) == toSqi) return move;
		}
		return 0;
	}
	
	public void doMove(short move) {
		try {
			//System.out.println("making move " + move);
			position.doMove(move);
			//System.out.println(position);
			if (position.isCheck()) {
				System.out.println("Check!");
			}
			if (position.isMate()) {
				System.out.println("Checkmate!");
			}
			if (position.isStaleMate()) {
				System.out.println("Stalemate!");
			}
		} catch (IllegalMoveException e) {
			System.out.println("illegal move!");
		}
	}

	public static void main(String[] args) {
		//System.out.println();

		// Create a starting position using "Forsyth–Edwards Notation". (See
		// Wikipedia.)
		Position position = new Position(
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

		//System.out.println(position);

	}
	
	

}
