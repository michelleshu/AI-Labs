import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Test {
	public class DomainValComparator implements Comparator<Pair> {
		public int compare(Pair p1, Pair p2) {
			return p1.b - p2.b;
		}
	}
	
	public static void main(String[] args) {
//		ArrayList<Integer> a = new ArrayList<Integer>(5);
//		a.add(5);
//		a.add(3);
//		a.add(7);
//		a.add(4);
//		a.add(2);
//		
//		System.out.println(a);
//		a.remove((Integer)7);
//		System.out.println(a);
		Test t = new Test();
		
		ArrayList<Pair> pList = new ArrayList<Pair>();
		pList.add(new Pair(3, 6));
		pList.add(new Pair(7, 2));
		pList.add(new Pair(4, 1));
		pList.add(new Pair(6, 5));
		pList.add(new Pair(8, 2));
		pList.add(new Pair(5, 7));
		
		Collections.sort(pList, t.new DomainValComparator());
		
		for (int i = 0; i < pList.size(); i++) {
			System.out.println(pList.get(i).b);
		}
	}
}