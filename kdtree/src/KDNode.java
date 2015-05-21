
public class KDNode <Value> {
	int depth;
	Value val;
	KDNode<Value> left,right;
	int x, y;
	public KDNode(Value v, int x, int y, int depth) {
		this.depth = depth;
		this.val = v;
		this.x = x;
		this.y = y;
		left = null;
		right = null;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param depth
	 * @return negative value if this > (x,y) at depth
	 */
	public int compare(int x, int y, int depth) {
		if ((depth & 0x1) == 0) {
			return (int)(x-this.x);
		} else {
			return (int)(y-this.y);
		}
	}

	/**
	 * 
	 * @param n
	 * @param depth
	 * @return negative value if this < n at depth
	 */
	public int compare(KDNode<Value> n, int depth) {
		if ((depth & 0x1) == 0) {
			return (int)(this.x-n.x);
		} else {
			return (int)(this.y-n.y);
		}
	}

	public boolean equals(int x, int y, Value actor) {
		return (this.x==x && this.y==y && this.val==actor);
	}

}

