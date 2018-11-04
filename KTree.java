import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
/****************************************/
/* YOUR CODE SECTION!                   */
/****************************************/

//add your methods here...

/****************************************/
/* EDIT THIS MAIN METHOD FOR TESTS. PUT */
/* HELPER TEST METHODS IN THIS SECTION  */
/* AS WELL. TESTS REQUIRED FOR FULL     */
/* CREDIT.                              */
/****************************************/

// use array of nodes for children
// root will always be 0
// use parent to keep track
public class KTree<E> implements TreeIterable<E> {
	private static class Node<E> { //helper method - structure of the tree
		private E value;
		private Node<E> firstChild; 
		private Node<E> nextSibling;
		//node class takes in value, first child and next sibling
		public Node(E value, Node<E> firstChild, Node<E> nextSibling) { 
			this.value = value;
		}

		public E getValue() {
			return value;
		}
		public void setValue(E value) {
			this.value = value;
		}
		public Node<E> getFirstChild() {
			return firstChild;
		}
		public void setFirstChild(Node<E> firstChild) {
			this.firstChild = firstChild;
		}
		
		public Node<E> getNextSibling() {
			return nextSibling;
		}

		public void setNextSibling(Node<E> nextSibling) {
			this.nextSibling = nextSibling;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			if (value == null)
				return "null";
			return value.toString();
		}
	}
	//helper class 
	private class MyQueue<E> {
		private int total;
		private Node<E> front, back;

		private class Node<T> {
			private E value;
			private Node<E> next;

			public Node(E val) { //constructor takes in value
				this.value = val;
			}
		}

		public void enqueue(E value) { //puts in value at the end of the queue
			Node<E> current = back;
			back = new Node<>(value);
			if (total == 0) { //if it is the first value coming in, the front and back are the same
				front = back; 
			} else {
				current.next = back; //links the next value from current as the last value aka back 
			}
			total++; //keeps track of amount of values in the queue

		}

		public E dequeue() { //removes value from the front
			if (total == 0) {
				return null;
			}
			E value = front.value;
			front = front.next; //deletes the previous node and replaces it with the next one
			if (total == 0) {
				back = null;
			}
			total--;
			return value;
		}

		@Override
		public String toString() { //helper method, queue toString
			StringBuilder result = new StringBuilder();
			for (Node<E> n = front; n != null; n = n.next) {

				result.append(n.value.toString());

				result.append("--> ");

			}

			return result.toString();
		}
	}

	private int k;
	private int size;
	private Node<E> root;
	private E[] arrayTree;
	private int height;
	private int capacity = 0;
	
	public KTree(E[] arrayTree, int k) { //constructor
		this.arrayTree = arrayTree;
		this.k = k;
		root = createTree(arrayTree, k); //calls the helper method to create a tree
	}
	private Node<E> createTree(E arrayTree[], int k) {
		Node<E> root = new Node<E>(arrayTree[0], null, null); //the first array value is always root
		size = 1; // size starts with one because of the root
		Node<E> temp = root;
		MyQueue<Node<E>> queue = new MyQueue<>();
		int count = 0, count3 = 1; // for size: keeps track of first child
		// for height: keeps track of length from height to current parent respectively
		int branch = k; //keeps track of amount of children
		if (k < 2) {
			throw new InvalidKException();
		}
		for (int i = 1; i < arrayTree.length; i++) {
			capacity++; //amount of every element coming in 
			Node<E> current;
			if (branch > 0) { //if there are kids
				if(arrayTree[i] != null) {
					current = new Node<E>(arrayTree[i], null, null);
					size++; // increment size for every node with value
				} else
					current = new Node<E>(arrayTree[i], null, null); 
				queue.enqueue(current); //put nodes except the root in queue
				if (count == 0) { //first time putting a node under a parent: firstchild
					temp.setFirstChild(current);
					branch--;
					count++;
					temp = current;
				} else { //if node is a sibling
					temp.setNextSibling(current);
					branch--; 
					if (branch == 0) { //when there are no more children 
						temp = queue.dequeue(); // the dequeued node becomes a parent
						if (capacity >= count3) { //calculates height
							count3 *= k + 1;  
							height++;
						}
						branch = k; //reset when starting a new level
						count = 0; 
					} else {
						temp = current;
					}
				}
			}

		}
		capacity++;
		return root;
	}

	public int getK() { 
		// System.out.println(this.k);
		return this.k;
	}
	@Override
	public String toString() { //helper method prints out in level order
		StringBuilder res = new StringBuilder();
		Iterator<E> it = getLevelOrderIterator();
		int j = 0;
		int c = 1 * k;
		res.append(get(0).toString() + "\n");
		for (int i = 1; i < capacity; i++) {
			// System.out.println(c);
			if (j < c) {
				if (get(i) != null)
					res.append(get(i).toString());
				else
					res.append("null");
				if (j < c - 1)
					res.append(" ");
				j++;
			} else {
				res.append("\n");
				c = k * c;
				j = 0;
				if (get(i) != null)
					res.append(get(i).toString());
				else
					res.append("null");
				if (j < c - 1)
					res.append(" ");
				j++;
			}
		}
		return res.toString();

	}

	public int size() {
		return size; //returns size of the tree - amount of elements 
	}

	public int height() {
		return height; //returns height of the tree - length from root to lowest leaf
	}
	public E get(int i) {
		if (i < 0 || i >= capacity) { //if index is less or beyond expected amount, throw exception 
			throw new IllegalArgumentException();
		}
		int count = 0;
		Node<E> temp;
		MyQueue<Node<E>> que = new MyQueue<Node<E>>();
		temp = root;
		que.enqueue(temp); //put the root in the queue first
		//int j = k;
		while (que.total != 0) {
			// temp = que.dequeue();
			while (temp.nextSibling != null) {
				que.enqueue(temp.nextSibling);  //adds the rest of the siblings into the queue
				temp = temp.nextSibling;
			}
			temp = que.dequeue();
			if (count == i) { 
				return temp.value;
			}
			if (temp.firstChild != null) {
				que.enqueue(temp.firstChild); //adds first child in the queue
				temp = temp.firstChild;
			}
			count++; //keeps track of the index of the nodes

		}
		return null;
	}

	public boolean set(int i, E value) {
		int parentI = i % k == 0 ? (i / k) - 1 : i / k;
		if (i != 0 && get(parentI) == null) {
			//checks if the index that wants to be replaced has a parent
			throw new InvalidTreeException();
		}
		if (i > capacity) { // if the index has parent, and the index doesn't exist
			//we have to make a new tree.
			@SuppressWarnings("unchecked") //allowed according to specs 
			E[] newArray = (E[]) new Object[i + 1];
			for (int j = 0; j < newArray.length; j++) {
				if (j < capacity)
					newArray[j] = get(j);
				else {
					newArray[j] = null;
				}
			}
			newArray[i] = value; //replace the value first before recreating the tree
			capacity = 0;
			height = 0;
			size = 0;
			root = createTree(newArray, k); //call create tree method
			//System.out.println(capacity);
			return true;

		} else {
			Node<E> temp = root;
			int index = 0;
			MyQueue<Node<E>> que = new MyQueue<Node<E>>();
			que.enqueue(temp);

			while (index >= 0) {
				if (index == i) {
					if (value == null) {
			//if node is being replaced by null and it has children, return false
						Node<E> temp2 = temp.firstChild;
						while (temp2 != null) {
							if (temp2.value != null) {
								return false;
							}
			//since first child can be null while the sibling is not,check for every child
							temp2 = temp2.nextSibling;
						}
						temp.value = value;
						return true;
					} else {
						// if it doesn't have kids, you can replace it
						temp.value = value;
						return true;
					}
				} else {
					if (temp.nextSibling != null) { //checks next index
						temp = temp.nextSibling;
						que.enqueue(temp);
						index++;
					} else {
						temp = que.dequeue(); //checks next index at a new level
						temp = temp.firstChild;
						que.enqueue(temp);
						index++;
					}
				}
			}
		}
		return false;
	}

	public Object[] toArray() { //returns in an array form
		@SuppressWarnings("unchecked")
		E newArray[] = (E[]) new Object[capacity];
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = get(i);
		}
		return newArray;
	}

	public Iterator<E> getLevelOrderIterator() {
		return new LevelOrderIter();
	}

	private class LevelOrderIter implements Iterator<E> {
		ArrayDeque<E> queue = new ArrayDeque<>();

		public LevelOrderIter() {
			for(int i = 0; i < capacity; i++) {
				if (get(i) != null) //gets every value in the tree and adds it in the queue
					queue.add(get(i));
			}
		}
		@Override
		public boolean hasNext() {
			return !queue.isEmpty(); //checks queue if there is value
		}
		@Override
		public E next() {
			return queue.remove(); //gets value

		}
	}
	public String toStringLevelOrder() { //toString of level order iterator
		StringBuilder res = new StringBuilder();
		Iterator<E> it = getLevelOrderIterator();
		while (it.hasNext()) {
			res.append(it.next());
			res.append(" ");
		}
		return res.toString();
	}

	public Iterator<E> getPreOrderIterator() {
		return new PreOrderIter();
	}

	private class PreOrderIter implements Iterator<E> { //root,left,right
		ArrayDeque<Node<E>> stack = new ArrayDeque<>();
		Node<E> cursor = null;
		int ind;
		public PreOrderIter() {
			ind = size;
		}
		@Override
		public boolean hasNext() {
			if (ind == size) {
				stack.push(root);
				return true;
			}
			return ind != 0;
		}

		@Override
		public E next() {
			if (cursor == null && root != null) { //gets root value
				cursor = root;
				ind--;
				return cursor.value;
			}
			while(!stack.isEmpty()) {
				while(cursor.firstChild != null) { //process children on the left children
					stack.push(cursor.firstChild); //put in every firstchild in stack
					cursor = cursor.firstChild; 
					if (cursor.value != null) {
						ind--;
						return cursor.value;
					}
				}
				cursor = stack.pop(); 

				while (cursor.nextSibling == null) {//processes children on the right
					if (stack.isEmpty())
						break;
					cursor = stack.pop();
				}
				while (cursor.nextSibling != null) {
					stack.push(cursor.nextSibling);
					cursor = cursor.nextSibling;
					if (cursor.value != null) {
						ind--;
						return cursor.value;
					}
				}
			}
			return null;
		}

	}

	public String toStringPreOrder() { //returns tostring of preorder
		StringBuilder res = new StringBuilder();
		Iterator<E> it = getPreOrderIterator();
		while (it.hasNext()) {
			res.append(it.next());
			res.append(" ");
		}
		return res.toString();
	}

	public Iterator<E> getPostOrderIterator() {
		return new PostOrderIter();
	}

	private class PostOrderIter implements Iterator<E> { //left,right,root
		ArrayDeque<Node<E>> stack = new ArrayDeque<>();
		Node<E> cursor = null;
		int ind;

		public PostOrderIter() {
			// TODO Auto-generated constructor stub

			ind = size;
		}

		@Override
		public boolean hasNext() {
			if (ind == size) {
				stack.push(root);
				cursor = root;
				return true;
			}
			return ind != 0;
		}

		@Override
		public E next() {
			while (!stack.isEmpty()) {
				while (cursor != null && cursor.firstChild != null) {//processes left children
					stack.push(cursor.firstChild);
					cursor = cursor.firstChild;
				}
				cursor = stack.pop();
				while (cursor != null) {
					E temp = null;
					if (cursor.value != null) {
						ind--;
						temp = cursor.value;
					}
					// if (cursor.nextSibling != null)
					cursor = cursor.nextSibling; //processes right children
					if (cursor != null)
						stack.push(cursor);
					if (temp != null)
						return temp;

					break;

				}

			}
			return null;

		}
	}

	public String toStringPostOrder() {//tostring of postorder
		StringBuilder res = new StringBuilder();
		Iterator<E> it = getPostOrderIterator();
		while (it.hasNext()) {
			res.append(it.next());
			res.append(" ");
		}
		return res.toString();

	}

	public static String decode(KTree<String> tree, String codedMessage) {
		StringBuilder res = new StringBuilder();
		Node<String> node = tree.root;
		int n = 0, i = 0;

		node = node.firstChild;
		while (i < codedMessage.length()) {
			if (n == Integer.parseInt(codedMessage.charAt(i) + "")) {
				if (node.value != null && node.value != "_") {
					res.append(node.value.toString());
					node = tree.root.firstChild; //after adding the letter, reset
					i++;
					n = 0; //reset
				} else {
					node = node.firstChild;
					i++;
				}
			} else { //not "0th" child: so sibling of "0th" child 
				node = node.nextSibling;
				n++; //increment to match the coded message 
			}
		}
		return res.toString();

	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//constructor & createTree
//		Integer[] ints2 = { 0, 1, 2, null, null, 5, 6, null, 8, 9};
//		String[] strings = { "_", "_", null, "B", "N", null, null };
//		//String[] strings2 = { "1", "2", "3", "4", "5", "6","7","8","9","10","11","12","13"};
//		Integer[] ints = { 0, null, 2, null, null, 5, 6 };
//		KTree<String> tree = new KTree<>(strings, 3);
//		KTree<Integer> tree2 = new KTree<Integer>(ints2, 3);
//		KTree<Integer> tree3 = new KTree<Integer>(ints, 3);
		//System.out.println(tree);
		//System.out.println(tree2);
		//getK
		//System.out.println("kvalue:"+tree2.getK());
		//System.out.println("kvalue:"+tree.getK());
		//size 
		//System.out.println("size:"+tree2.size);
		//System.out.println("size:"+tree.size);
		//height
		//System.out.println("height"+tree2.height);
		//System.out.println("height"+tree.height);
		//get
		//System.out.println(tree2.get(1));
		//System.out.println(tree2.get(5));
		//System.out.println(tree2.get(10));
		//set
		//System.out.println(tree2.set(0, 5));
		//System.out.println(tree3.set(20, 14));
		// boolean s;
		//while (i < 7) {
		//s = tree.set(i, 14);
		//tree.set(29, 1);
		//toString helper method
//		String s = tree.toString(); // should be "x\n_ A\nB N null null"
//		System.out.println(s);
//		String s2 = "" + tree; 
//		System.out.println(s2);
		//toArray
//		System.out.println(tree2.toArray());
//		System.out.println(tree3.toArray());
//		System.out.println(tree.toArray());
		//levelorderIterator //toString 
//		Iterator<String> it = tree.getLevelOrderIterator();
//		int o = 1;
//		while (it.hasNext()) {
//		 System.out.println(it.next() + " " + o++);
//		}
//		Iterator<Integer> its = tree2.getLevelOrderIterator();
//		int p = 1;
//		while (its.hasNext()) {
//		 System.out.println(its.next() + " " + p++);
//		}
//		System.out.println("LO:"+tree3.toStringLevelOrder());
//		System.out.println("LO:"+tree.toStringLevelOrder());
//		System.out.println("LO:"+tree2.toStringLevelOrder());
		//postOrderIterator
//		Iterator<String> it = tree.getPostOrderIterator();
//		int o = 1;
//		while (it.hasNext()) {
//		 System.out.println(it.next() + " " + o++);
//		}
//		Iterator<Integer> its = tree2.getPostOrderIterator();
//		int p = 1;
//		while (its.hasNext()) {
//		 System.out.println(its.next() + " " + p++);
//		}
		//toString 
//		System.out.println("PO:"+tree3.toStringPostOrder());
//		System.out.println("PO:"+tree.toStringPostOrder());
//		System.out.println("PO:"+tree2.toStringPostOrder());
		//preOrderIterator
//		Iterator<String> it = tree.getPreOrderIterator();
//		int o = 1;
//		while (it.hasNext()) {
//		 System.out.println(it.next() + " " + o++);
//		}
//		Iterator<Integer> its = tree2.getPreOrderIterator();
//		int p = 1;
//		while (its.hasNext()) {
//		 System.out.println(its.next() + " " + p++);
//		}
		//toString 
//		System.out.println("PR:"+tree3.toStringPreOrder());
//		System.out.println("PR:"+tree.toStringPreOrder());
//		System.out.println("PR:"+tree2.toStringPreOrder());
		//decode
//		System.out.println(decode(new KTree<String>(new String[] { "_", "_", "A",
//		"B", "N", null, null }, 2), "001011011"));
//		System.out.println(decode(new KTree<String>(new String[] { "_", "_", "L",
//				"E", "I", null, null }, 2), "100101"));
//		System.out.println(decode(new KTree<String>(new String[] { "_", "_", "E",
//				"C", null, null }, 2), "001001"));
		// // change this method around to test!
		methodSigCheck();

	}

	/****************************************/
	/* DO NOT EDIT ANYTHING BELOW THIS LINE */
	/****************************************/

	public static void methodSigCheck() {
		// This ensures that you've written your method signatures correctly
		// and understand how to call the various methods from the assignment
		// description.
		String[] strings = { "_", "_", "A", "B", "N", null, null };

		KTree<String> tree = new KTree<>(strings, 2);
		int x = tree.getK(); // should return 2

		int y = tree.size(); // should return 5

		int z = tree.height(); // should return 2

		String v = tree.get(0); // should be "_"

		boolean b = tree.set(0, "x"); // should set the root to "x"

		Object[] o = tree.toArray(); // should return [ "x", "_", "A", "B", "N", null, null ]

		String s = tree.toString(); // should be "x\n_ A\nB N null null"
		String s2 = "" + tree; // should also be "x\n_ A\nB N null null"

		Iterator<String> it1 = tree.getLevelOrderIterator(); // gets an iterator
		int i = 0;

		Iterator<String> it2 = tree.getPreOrderIterator(); // gets an iterator

		Iterator<String> it3 = tree.getPostOrderIterator(); // gets an iterator

		String s3 = tree.toStringLevelOrder(); // should be "x _ A B N"

		String s4 = tree.toStringPreOrder(); // should be "x _ B N A"

		String s5 = tree.toStringPostOrder(); // should be "B N _ A x"

		String s6 = decode(tree, "001011011"); // should be "BANANA"
		// Object[] o2 = tree.mirror(); //should return [ "x", "A", "_", null, null,
		// "N", "B" ]
		// Object[] o3 = tree.subtree(1); //should return [ "_", "B", "N" ]
	}
}