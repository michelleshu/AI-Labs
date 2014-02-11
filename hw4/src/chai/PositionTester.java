package chai;

import java.util.Random;

import chesspresso.Chess;
import chesspresso.position.*;

public class PositionTester {
	
	private static double utility(Position position) {
		if (position.isTerminal() && position.isMate()) {
			if (position.getToPlay() == Chess.BLACK) {
				// I lose b/c opponent just checkmated me
				return Double.MIN_VALUE;
			} else {	// I win!
				return Double.MAX_VALUE;
			}
		} else {
			Random rand = new Random();
			return rand.nextDouble() * 1E100;
		}
	}
	
	public static void main(String[] args) {
		Position positionA = new Position("4k3/4Q3/5K2/8/8/8/8/8 b - - 0 1");
		System.out.println(positionA.isTerminal());
		System.out.println(positionA.isMate());
		System.out.println(positionA.canMove());
		System.out.println(positionA.getToPlay());
		System.out.println(utility(positionA));
		System.out.println(- Integer.MAX_VALUE);
		System.out.println(- (Integer.MAX_VALUE - 1));
	}
}