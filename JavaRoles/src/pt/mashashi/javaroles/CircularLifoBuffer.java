package pt.mashashi.javaroles;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.Buffer;

/**
 * Class implementing a simple circular LIFO buffer.
 * 
 * This buffer is always limited to the size specified when the constructor is invoked.
 * The underlying structure used is an <code>ArrayList</code>.
 * The insertion of an element is always successful unless the element is null.
 * The addition of a new element is always performed on the head (i.e. position 0) of the <code>ArrayList</code>.
 * The insertion of a new element causes the shift of all the other existing elements to the right.
 * If the number of elements stored in the buffer equals the maximum size allowed, each new insertion determines
 * the loss of the oldest element added.
 * The deletion of the latest element causes the shift of all the other existing elements to the left.
 * An iterator is provided to speed up search operations according a LIFO policy: elements are listed in the reverse
 * order of insertion, that is from the more to the less recent. 
 * 
 *
 */
@SuppressWarnings("rawtypes")
public class CircularLifoBuffer extends AbstractCollection implements Buffer, BoundedCollection {
	
	private static final int DEFAULT_SIZE = 32;
	
	private static final int NEGATIVE = -1;
	
	/** The structure used to manage the addition, removal and search of elements in the buffer */
	private ArrayList elements = null;
	
	private int maxSize = NEGATIVE;
	
	private int indexOfFirst = NEGATIVE;
	
	/**
	 * Constructor.
	 * 
	 * @param size The maximum size of the buffer.
	 */
	public CircularLifoBuffer(int size) {
		elements = new ArrayList(size);
		maxSize = size;
	}

	/**
	 * Constructor using default size.
	 */
	public CircularLifoBuffer() {
		this(DEFAULT_SIZE); 
	}

	/**
	 * Updates indexes as resulting after a successful operation of insertion.
	 */
	private synchronized void updateIndexesOnAdd() {
		if (indexOfFirst == NEGATIVE) {
			// The array is empty
			/*
			 *   [0] [1] [2] ... [n]
			 * F ->
			 */
			indexOfFirst = 0;
		} else {
			if (indexOfFirst < maxSize - 1) {
				/*
				 *   [0] [1] [2] ... [n]
				 *            F ->
				 */
				 indexOfFirst++;
			} 
		} 
	}

	/**
	 * Updates indexes as resulting after a successful operation of removal.
	 */
	private synchronized void updateIndexesOnRemove() {
		if (indexOfFirst == 0) {
			// The array contains only one element.
			/*
			 *   [0] [1] [2] ... [n]
			 * <- F
			 */
			indexOfFirst = NEGATIVE;
		} else {
			/*
			 *   [0] [1] [2] ... [n]
			 *         <- F
			 */
			indexOfFirst--;
		} 
	}
	
	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return elements.size();
	}

	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		/*
		 * An iterator is provided to speed up search operations according a LIFO policy: elements are listed in the reverse
		 * order of insertion, that is from the more to the less recent.
		 *
		 */
        return new Iterator() {
        	
        	private int currentIndex = NEGATIVE;
        	
            /**
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
            	boolean result = false;
            	if (!elements.isEmpty() && currentIndex < elements.size() - 1) {
            		result = true;
            	}
            	return result;
            }

            /**
             * @see java.util.Iterator#next()
             */
            public Object next() {
                if (!hasNext()) {
                	throw new NoSuchElementException();
                }
                currentIndex++;
                return elements.get(currentIndex);
            }

            /**
             * @see java.util.Iterator#remove()
             */
            public void remove() {
                if (currentIndex == NEGATIVE) {
                	throw new IllegalStateException();
                }
                elements.remove(currentIndex);
                currentIndex--;
            }
        };
	}

	/**
	 * @see org.apache.commons.collections.Buffer#remove()
	 */
	public synchronized Object remove() {
		return removeLast();
	}

	/**
	 * Returns the removed first element inserted or null if the buffer is empty.
	 * @return The removed first element inserted or null if the buffer is empty. 
	 */
 	private Object removeFirst() {
		Object firstElement = null;
		if (!elements.isEmpty()) {
			firstElement = elements.remove(indexOfFirst);
			updateIndexesOnRemove();
		}
		return firstElement;
	}
	
	/**
	 * Returns the removed last element inserted or null if the buffer is empty.
	 * @return The removed last element inserted or null if the buffer is empty. 
	 */
	private Object removeLast() {
		Object lastElement = null;
		if (!elements.isEmpty()) {
			lastElement = elements.remove(0);
			updateIndexesOnRemove();
		}
		return lastElement;
	}

	/**
	 * @see org.apache.commons.collections.Buffer#get()
	 */
	public Object get() {
		Object lastElement = null;
		if (!elements.isEmpty()) {
			lastElement = elements.get(0);
		}
		return lastElement;
	}

	/**
	 * @see org.apache.commons.collections.BoundedCollection#isFull()
	 */
	public boolean isFull() {
		return false;
	}

	/**
	 * @see org.apache.commons.collections.BoundedCollection#maxSize()
	 */
	public int maxSize() {
		return elements.size();
	}
	
    /**
     * Adds the given element to this buffer.
     *
     * @param element the element to add.
     * @return false only if the given element is null.
     * @see java.util.Collection#add(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
	public synchronized boolean add(Object element) {
    	boolean result = false;
    	
    	if (element != null) {
    		// The last element is inserted at the head of the array.
    		// The first element shifts on the right together with the other elements.
    		if (elements.size() == maxSize) {
    			// If the buffer is full the first elemented inserted is removed.
    			removeFirst();
    		}
    		elements.add(0, element);
    		updateIndexesOnAdd();
    	}
    	return result;
    }
    
    /**
     * Print all the elements in the buffer.
     */
    public void printAll() {
    	Iterator i = iterator();
    	int counter = 0;
    	while(i.hasNext()) {
    		System.out.println("b[" + counter + "]=" + i.next().toString());
    		counter++;
    	}
    	if (counter == 0) {
    		System.out.println("buffer empty");
    	}
    }
}