package chai;

import chesspresso.Chess;
import chesspresso.position.*;
import java.util.Random;

public class EvaluationFunction {

	// Material evaluation function
	public static int getMaterialUtility(Position position, int color) {
		// Count differences in number of pieces on board (black - white)
		int pawnd, knightd, bishopd, rookd, queend, kingd, stone;
		pawnd = knightd = bishopd = rookd = queend = kingd = 0;
		for (int col = 0; col < 8; col++) {
			for (int row = 0; row < 8; row++) {
				stone = position.getStone(Chess.coorToSqi(col, row));
				switch (stone) {
					case 1 : knightd++;
						break;
					case -1 : knightd--;
						break;
					case 2 : bishopd++;
						break;
					case -2 : bishopd--;
						break;
					case 3 : rookd++;
						break;
					case -3 : rookd--;
						break;
					case 4 : queend++;
						break;
					case -4 : queend--;
						break;
					case 5 : pawnd++;
						break;
					case -5 : pawnd--;
						break;
					case 6 : kingd++;
						break;
					case -6 : kingd--;
						break;
					default : break;
				}
			}
		}
		
		
		// Use adaptation of Claude Shannon's relative weights, referenced from
		// chessprogramming.wikispaces.com
		//int utility = (90 * queend) + (50 * rookd) + (30 * (bishopd + knightd)) +
		//		(10 * pawnd);
		
		// From ChessBin.com
		// http://www.chessbin.com/post/chess-board-evaluation.aspx
		int utility = (975 * queend) + (500 * rookd) + (325 * bishopd) + 
				(320 * knightd) + (100 * pawnd) + (32767 * kingd);
		
		if (color == Chess.WHITE) { // white's perspective
			utility = - utility;
		}
		
		if (utility == 0) { // level playing field, just randomize it a little
			Random rand = new Random();
			return rand.nextInt(20) - 10;
		}
		
		return utility;
	}
} 