import java.util.Arrays;

/**
 * This is a priority queue class.
 * 
 * @author Qing Bai
 * @version 29 April 2015
 */
public class MyPriorityQueue<T extends Comparable<T>>{
	
	/**
	 * This is initial capacity of this priority queue.
	 */
	public static final int INITIAL_CAPACITY = 16;
	
	/**
	 * This array stores elements of this priority queue.
	 */
	private T[] myElements;
	
	/**
	 * This is size of this priority queue.
	 */
	private int mySize;
	
	/**
	 * This initializes this priority queue.
	 */
	@SuppressWarnings("unchecked")
	public MyPriorityQueue() {
		myElements = (T[]) new Comparable[INITIAL_CAPACITY];
		mySize = 0;
	}
	
	/**
	 * This checks whether or not this priority queue is empty. 
	 * 
	 * @return true if this priority queue is empty. Otherwise, false
	 */
	public boolean isEmpty() {
		return mySize == 0;
	}
	
	/**
	 * This returns size of this priority queue.
	 * 
	 * @return size of this priority queue
	 */
	public int size() {
		return mySize;
	}
	
	/**
	 * This adds one element to a proper position in this priority queue.
	 * 
	 * @param theElement is the element being added into this queue
	 */
	public void add(final T theElement) {
		if (mySize == myElements.length - 1) {
			myElements = Arrays.copyOf(myElements, myElements.length * 2);
		}
		
		myElements[0] = theElement;
		int hole;
		// the first element in this priority queue is at index 1
		for (hole = ++mySize; theElement.compareTo(myElements[hole / 2]) < 0; hole /= 2) {
			myElements[hole] = myElements[hole / 2];
		}
		
		myElements[hole] = theElement;
	}
	
	/**
	 * This removes and returns the minimum number of this priority queue.
	 * 
	 * @return the minimum number of this priority queue or null when this
	 * priority queue is empty
	 */
	public T poll() {
		if (isEmpty()) {
			return null;
		}
		
		T result = myElements[1];
		myElements[1] = myElements[mySize--];
		
		for (int hole = 1; 2 * hole <= mySize;) {
			int child = 2 * hole;
			// find smaller child
			if (child + 1 <= mySize && myElements[child + 1].compareTo(myElements[child]) < 0) {
				child++;
			}
			
			if (myElements[hole].compareTo(myElements[child]) > 0) {
				T temp = myElements[hole];
				myElements[hole] = myElements[child];
				myElements[child] = temp;
				hole = child;
			} else {
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return Arrays.toString(myElements);
	}
}
