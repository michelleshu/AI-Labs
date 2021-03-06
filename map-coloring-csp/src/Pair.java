/** Packaging a pair of indices involved in a constraint */
public class Pair {
	public int a, b;
	public Pair(int indexA, int indexB) {
		this.a = indexA;
		this.b = indexB;
		
	}
	public boolean equals(Object other) {
		Pair o = (Pair) other;
		return ((this.a == o.a) && (this.b == o.b));
	}
	
	public int hashCode() {
		return (57 * this.a) + (57 * 57 * this.b);
	}
}