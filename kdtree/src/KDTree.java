/**
 * A KDTree for 2D game
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;


/**
 * KDTree to represent the objects in 2D. Helps in finding overlapping objects in O(n^0.5) 
 * @author naresh 
 */
public class KDTree <Value>{
	private KDNode<Value> root;
	public KDTree() {
		root = null;
	}
	
	public void insert(Value actor, int x, int y) {
		if (actor == null) return;
		if (root == null) {
			root = new KDNode<Value>(actor, x, y, 0);
			return;
		}
		insert(root, actor, x, y, 0);
	}
	
	private KDNode<Value> insert(KDNode<Value> current, Value actor, int x, int y, int depth) {
		if (current == null) {
			System.out.println("KDTree: Adding a new node :("+x+", "+y+") at depth:"+depth);
			return new KDNode<Value>(actor, x, y, depth);
		} else if (current.equals(x, y, actor)) {
			System.out.println("KDTree: Node already exists:("+x+", "+y+"):");
			return current;
		}
		if (current.compare(x,y,depth) < 0) {
			current.left = insert(current.left, actor,x,y,depth+1);
		} else {
			current.right = insert(current.right, actor,x,y,depth+1);
		}
		return current;
	}

	public boolean exists(Value actor, int x, int y) {
		return exists(root,actor, x,y);
	}
	
	private boolean exists(KDNode<Value> current, Value actor, int x, int y) {
		if (current==null) return false;
		int depth = current.depth;
		if (current.equals(x, y, actor)) return true;
		
		if (current.compare(x,y, depth) < 0) {
			return exists(current.left, actor, x, y);
		} else {
			return exists(current.right, actor, x, y);
		}
		
	}
	
	/**
	 * finding min on subtree with root divided by x-axis will return the node with min x value in that tree
	 * same holds for the y-axis
	 * @param current
	 * @param subtree_root_depth
	 * @return
	 */
	private KDNode<Value> findMin(KDNode<Value> current, int subtree_root_depth) {
		if (current == null) return null;
		int depth = current.depth;
		KDNode<Value> min = current;
		if ((depth & 0x1)== (subtree_root_depth & 0x1)) { //if it is divided based same axis as root
			if (current.left == null) return current;
			else return findMin(current.left, subtree_root_depth);
		} else {
			// we need to search on both the subtrees
			KDNode<Value> sub_min = findMin(current.left, subtree_root_depth);
			if ( sub_min != null && sub_min.compare(min, subtree_root_depth) < 0){
				min = sub_min;
			}
			sub_min = findMin(current.right, subtree_root_depth);
			if (sub_min != null && sub_min.compare(min, subtree_root_depth) < 0){
				min = sub_min;
			}
		}
		return min;
	}
	
	/**
	 * finding max on subtree with root divided by x-axis will return the node with max x value in that tree
	 * same holds for the y-axis
	 * @param current
	 * @param subtree_root_depth
	 * @return
	 */
	private KDNode<Value> findMax(KDNode<Value> current, int subtree_root_depth) {
		if (current == null) return null;
		int depth = current.depth;
		KDNode<Value> max = current;
		if ((depth & 0x1)== (subtree_root_depth & 0x1)) { //if it is divided based same axis as root
			if (current.right == null) return current;
			else return findMax(current.right, subtree_root_depth);
		} else {
			// we need to search on both the subtrees
			KDNode<Value> sub_max = findMax(current.left, subtree_root_depth);
			if (sub_max!=null && sub_max.compare(max, subtree_root_depth) > 0){
				max = sub_max;
			}
			sub_max = findMax(current.right, subtree_root_depth);
			if (sub_max!=null && sub_max.compare(max, subtree_root_depth) > 0){
				max = sub_max;
			}
		}
		return max;
	}
	
	/**
	 * credits: http://www.cs.umd.edu/~meesh/420/Notes/MountNotes/lecture18-kd2.pdf
	 * @param current
	 * @param actor
	 * @param depth
	 */
	public void delete(Value actor, int x, int y){
		delete(root,actor,x,y);
	}
	
	private KDNode<Value> delete(KDNode<Value> current, Value actor, int x, int y){
		if (current==null){
			System.out.println("KDTree: No node found to be deleted:("+x+","+y+")");
			/*TODO: Remove from production code*/
		//	boolean isPresent = this.searchEntireTree(actor, x, y);
		//	if (isPresent) {
		//		throw new RuntimeException("Something wrong with tree. Not able to find an existing point");
		//	}
			return null;
		}
		int depth = current.depth;
		if (current.equals(x,y,actor)) {
			if(current.right != null) {
				KDNode<Value> min = findMin(current.right, depth);
				current.val = min.val;
				current.x = min.x;
				current.y = min.y;
				System.out.println("\t: R:new node to be deleted:("+min.x+","+min.y+")");
				current.right = delete(current.right, min.val, min.x, min.y);
			} else if (current.left !=null) {
				KDNode<Value> min = findMin(current.left, depth);
				current.val = min.val;
				current.x = min.x;
				current.y = min.y;
				System.out.println("\t: L:new node to be deleted:("+min.x+","+min.y+")");
				current.right = delete(current.left, min.val, min.x, min.y);
				current.left = null;
			} else {
				System.out.println("\t: deleting :("+current.x+","+current.y+")");
				current=null;
			}
		} else if (current.compare(x, y, depth) < 0) {
			current.left = delete(current.left, actor, x, y);
		} else {
			current.right = delete(current.right, actor, x, y);
		}
		return current;
	}
	
	/**
	 * Function does not allocate new KDNode<Value>s but reuses the one in list. Make sure we don't change the list
	 * while this function is in progress
	 * @param list
	 */
	public void createKDTreeFromList(List<KDNode<Value>> list){
		this.root = this.createKDTreeFromList(list, 0, 0, list.size()-1);
	}

	private KDNode<Value> createKDTreeFromList(List<KDNode<Value>> list, int depth, int left, int right) {
		int median;
		if (right<left) return null;
		if (left==right) {
			KDNode<Value> node = list.get(left);
			node.depth = depth;
			node.left = null;
			node.right = null;
			return node;
		}
		
		median = findMedian(list, left, right, depth);
		KDNode<Value> node = list.get(median);
		node.depth = depth;
		node.left = createKDTreeFromList(list,depth+1,left,median-1);
		node.right = createKDTreeFromList(list,depth+1,median+1, right);

		return node;
	}
	
	public void swap(List<KDNode<Value>> list, int i, int j) {
		if (i==j) return;
		KDNode<Value> temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}
	
	public void printList(List<KDNode<Value>> list, int left, int right){
		for (int i=left; i<= right;i++) {
			System.out.println("KDTree:"+i+ ":= ("+list.get(i).x+","+list.get(i).y+"), ");
		}
	}
	
	private int partition(List<KDNode<Value>> list, int left, int right, int pivotIdx, int depth){
		KDNode<Value> pivot = list.get(pivotIdx);
		this.swap(list,pivotIdx, right);
		int storeIdx = left;
		//printList(list,left,right);
		for (int i=left; i<=right-1;i++) {
			if (pivot.compare(list.get(i), depth) > 0) {
				swap(list, storeIdx, i);
				storeIdx++;
			}
		}
		swap(list,right, storeIdx);
		//printList(list,left,right);
		return storeIdx;
	}
	
	/**
	 * find the median with an avg runtime of O(n) 
	 * @param list
	 * @param left
	 * @param right is inclusive. size of the list will be right-left+1
	 * @param depth
	 * @return
	 */
	public int findMedian(List<KDNode<Value>> list, int left, int right, int depth) {
		int medianPos = left + (right-left)/2;
		int originalLeft = left;
		while (left != right) {
			int pivotIdx = left + (int)(Math.random()*(float)(right-left+1));
			pivotIdx = this.partition(list, left, right, pivotIdx, depth);
			if (medianPos == pivotIdx) {
				left = pivotIdx;
				break;
			} else if (medianPos < pivotIdx) {
				right = pivotIdx-1;
			} else {
				left = pivotIdx+1;
			}
		}
		
		/* In case we have node with same value, shift "left" to the first value */
		while (left-1 >= originalLeft && (list.get(left).compare(list.get(left-1), depth) == 0)) {
			left--;
		}
 		
		return left;
	}
	
	/**
	 * Balance current KD tree.
	 */
	public void balance() {
	//	validateTree();
		List<KDNode<Value>> list = this.convertToList(root);
		/* create a balance tree from given list */
		this.root = this.createKDTreeFromList(list, 0, 0, list.size()-1);
	//	validateTree();
	}
	
	/**
	 * This returns all the values in subtree rooted at current. 
	 * @return
	 */
	public HashSet<Value> getAllValues(KDNode<Value> current) {
		Queue<KDNode<Value>> q = new LinkedList<KDNode<Value>>();
		HashSet<Value> set = new HashSet<Value>();
		q.add(current);
		while (!q.isEmpty()) {
			KDNode<Value> top = q.remove();
			if (top.left!=null) q.add(top.left);
			if (top.right!=null) q.add(top.right);
			set.add(top.val);
		}
		return set;
	}
	
	public boolean searchEntireTree(Value val, int x, int y) {
		return searchEntireTree(root, val ,x ,y);

	}
	
	public boolean searchEntireTree(KDNode<Value> current, Value val, int x, int y) {
		if(current == null) return false;
		Queue<KDNode<Value>> q = new LinkedList<KDNode<Value>>();
		q.add(current);
		nodeIndex = 0;
		while (!q.isEmpty()) {
			KDNode<Value> top = q.remove();
			if(top.equals(x, y, val)) {
				return true;
			}
			if (top.left!=null){
				q.add(top.left);
			}
			if (top.right!=null){
				q.add(top.right);
			}
		}
		return false;
	}
	
	public void printTree() {
		System.out.println("KDTree:-----------------------------------------------");
		printTree(root);
		System.out.println("KDTree:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

	}
	
	int nodeIndex=0;
	public void printTree(KDNode<Value> current) {
		if(current == null) return;
		Queue<KDNode<Value>> q = new LinkedList<KDNode<Value>>();
		Queue<String> d = new LinkedList<String>();
		q.add(current);
		nodeIndex = 0;
		d.add("root:"+nodeIndex);
		int depth=current.depth;
		System.out.println();
		while (!q.isEmpty()) {
			KDNode<Value> top = q.remove();
			String debug = d.remove();
			if (depth != top.depth) {
				depth = top.depth;
				System.out.println();
				System.out.println("----------"+depth+"-------------");
			} else {
				System.out.print("\t");
			}
			if (top.left!=null){
				q.add(top.left);
				d.add("L:"+nodeIndex);
			}
			if (top.right!=null){
				q.add(top.right);
				d.add("R:"+nodeIndex);
			}
			System.out.print(nodeIndex+"("+top.x+","+top.y+"):"+debug);
			nodeIndex++;
		}
		System.out.println();
	}
	
	public void validateTree() {
		List<KDNode<Value>> nodes = this.convertToList(root);
		for (KDNode<Value> node:nodes) {
			validateTree(node);
		}
	}

	private void validateTree(KDNode<Value> current) {
		if(current == null) return;
		int depth = current.depth;
		List<KDNode<Value>> left = null;
		if (current.left != null) left = this.convertToList(current.left);
		List<KDNode<Value>> right = null;
		if (current.right !=null) right = this.convertToList(current.right);
		
		if (left !=null) {
			for (KDNode<Value> l : left) {
				if (l.compare(current, depth) >= 0) {
					System.out.println("L:KDTree Validation: For Node:("+current.x+", "+current.y+": "+current.depth+") node:(" +l.x+", "+l.y+") is at wrong place");
				}
			}
		}

		if (right !=null) {
			for (KDNode<Value> r : right) {
				if (r.compare(current, depth) < 0) {
					System.out.println("R:KDTree Validation: For Node:("+current.x+", "+current.y+") node:(" +r.x+", "+r.y+") is at wrong place");
				}
			}
		}

	}

	/**
	 * Return KD tree in a list. Note returned list is not a copy but collection of nodes in tree
	 * @return
	 */
	public List<KDNode<Value>> convertToList(KDNode<Value> current) {
		Queue<KDNode<Value>> q = new LinkedList<KDNode<Value>>();
		List<KDNode<Value>> list = new ArrayList<KDNode<Value>>();
		q.add(current);
		while (!q.isEmpty()) {
			KDNode<Value> top = q.remove();
			if (top.left!=null) q.add(top.left);
			if (top.right!=null) q.add(top.right);
			list.add(top);
		}
		return list;
	}
	
	/**
	 * Given rect bounding box of the node, it trims its left part and returns bounding box with which contains right half
	 * @param parent
	 * @param rect
	 * @return
	 */
	private Rectangle trimLeft(KDNode<Value> parent, Rectangle rect){
		int depth = parent.depth;
		Rectangle bound = new Rectangle(rect);
		if ((depth & 0x1) == 0) {
			bound.width -= (parent.x - bound.x); 
			bound.x = parent.x;
		} else {
			bound.height -= (parent.y - bound.y);
			bound.y = parent.y;
		}
		return bound;
	}
	
	/**
	 * Given rect bounding box of the node, it trims its right part and returns bounding box which contains left half
	 * @param parent
	 * @param rect
	 * @return
	 */
	private Rectangle trimRight(KDNode<Value> parent, Rectangle rect){
		int depth = parent.depth;
		Rectangle bound = new Rectangle(rect);
		if ((depth & 0x1) == 0) {
			bound.width = (parent.x - bound.x); 
		} else {
			bound.height = (parent.y - bound.y);
		}
		return bound;
	}
	
	/**
	 * returns all the values inside given rectangle
	 * @param rect
	 * @return Using HashSet here to avoid the duplicates
	 */
	public HashSet<Value> rangeSearch(Rectangle range) {
		Rectangle bounds = new Rectangle();
		if (this.root == null) { 
			Gdx.app.error("KDTree", "root is null");
			return null;
		}
		int x1 = this.findMin(root, 0).x;
		int x2 = this.findMax(root, 0).x;
		int y1 = this.findMin(root, 1).y;
		int y2 = this.findMax(root, 1).y;
		bounds.x = x1; bounds.y = y1;
		bounds.width = x2-x1;
		bounds.height = y2-y1;
		return rangeSearch(range, root, bounds);
	}
	
	private HashSet<Value> rangeSearch(Rectangle range, KDNode<Value> current, Rectangle bound){
		if (current == null) return null;
		if (!range.overlaps(bound)) return null; //range is outside bounds
		else if (range.contains(bound)) {
			/* add all the nodes in list */
			return this.getAllValues(current);
		}

		HashSet<Value> set = new HashSet<Value>();
		/* current node lies in the range */
		if ((current.x >= range.x && current.x <= range.x+range.width) && (current.y >= range.y && current.y <= range.y+range.height)) {
			set.add(current.val);
		}
		HashSet<Value> temp;
		temp = rangeSearch(range,current.left, this.trimRight(current, bound));
		if (temp!=null && !temp.isEmpty()) {
			set.addAll(temp);
		}
		temp = rangeSearch(range,current.right, this.trimLeft(current, bound));
		if (temp!=null && !temp.isEmpty()) {
			set.addAll(temp);
		}
		return set;
	}
	
	/**
	 * remove all the nodes from tree
	 */
	public void clear() {
		root = null;
	}
}
