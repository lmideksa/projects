import java.util.Iterator;

interface TreeIterable<T> {
	public Iterator<T> getLevelOrderIterator();
	public Iterator<T> getPreOrderIterator();
	public Iterator<T> getPostOrderIterator();
}