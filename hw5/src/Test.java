import java.util.ArrayList;

public class Test {
	public static void main(String[] args) {
		ArrayList<Integer> a = new ArrayList<Integer>(5);
		a.add(5);
		a.add(3);
		a.add(7);
		a.add(4);
		a.add(2);
		
		System.out.println(a);
		a.remove((Integer)7);
		System.out.println(a);
	}
}