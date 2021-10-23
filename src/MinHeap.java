import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * Created by User on 10/24/2015.
 */
public class MinHeap<T extends Comparable<T>> implements Iterable<T>{

    // Array used to hold the heap elements/data
    private T[] elements;
    // Used to hold our heap size
    private int size = 0;
    // Default initial heap capacity
    private static final int DEFAULT_INITIAL_CAPACITY = 256;
    // Array getIndex of the root element
    private static final int ROOT_INDEX = 0;
    // Index returned when no element is located at a specified location
    private static final int NO_ELEMENT_FOUND_INDEX = -1;

    // Default private constructor which intializes the element array to a default initial capacity.
    private MinHeap() {
        elements = (T[]) new Comparable [DEFAULT_INITIAL_CAPACITY];
    }

    // Constructor which takes an initial capacity and initializes the element array
    @SuppressWarnings("unchecked")
    private MinHeap(final int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("The initial capacity must be greater than 0.");
        elements = (T[]) new Object[initialCapacity];
    }

    // Static factory method which returns a new heap instance with default initial capacity
    public static <T extends Comparable<T>> MinHeap<T> newInstance() {
        return new MinHeap<T>();
    }

    // Return the number of elements in the heap
    public int getSize() {
        return size;
    }

    // Return true if our heap has no elements in it; false otherwise.
    public boolean isEmpty() {
        return (getSize() == 0);
    }

    // Return true if our heap has reached its capacity; false otherwise.
    public boolean isFull() {
        return (getSize() == elements.length);
    }

    // Get the parent element getIndex of the child
    private int getParentIndex (int childIndex) { return (childIndex - 1) / 2; }

    // Get the left child getIndex of the input parent element getIndex
    private int getLeftChildIndex ( int parentIndex ) { return parentIndex * 2 + 1; }

    // Get the right child getIndex of the input parent element getIndex
    private int getRightChildIndex ( int parentIndex ) { return parentIndex * 2 + 2; }

    // Return true if the input getIndex is within range and in our heap; false otherwise
    private boolean isValidIndex (int index ) { return index >= 0 && index < size; }

    // Get the getIndex of the child element with the minimum value - if our parent element has no children, return
    // a default element not found out of range getIndex to indicate there is no valid element getIndex to return
    private int getMinChildIndex ( int parentIndex ) {
        // Get the child element indexes of the parent getIndex
        int leftChildIndex = getLeftChildIndex ( parentIndex );
        int rightChildIndex = getRightChildIndex ( parentIndex );
        // If both the child indexes are valid
        if (isValidIndex(leftChildIndex) && isValidIndex(rightChildIndex)) {
            // Compare the child elements and return the child element with the minimum value
            if (elements[ leftChildIndex ].compareTo( elements [ rightChildIndex ]) < 0) {
                return leftChildIndex;
            } else {
                return rightChildIndex;
            }
        }
        // If only our left child getIndex is valid, return it
        else if (isValidIndex(leftChildIndex)) return leftChildIndex;
        // If our right child getIndex is the only valid value, return it
        else if (isValidIndex(rightChildIndex)) return rightChildIndex;
        // Otherwise, our element has no children, so we return an invalid getIndex value as an indicator that
        // there is no child getIndex element to return
        else return NO_ELEMENT_FOUND_INDEX;
    }

    // Swap array elements at getIndex i and j
    private <T> void swap (T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // Keep swapping the element at the input getIndex with its parent element until it is larger than its parent element
    // and the min heap property is satisfied
    private void heapifyUp (int index) {
        // Make sure that the getIndex is within range
        assert isValidIndex ( index );
        // Get the parent element getIndex
        int parentIndex = getParentIndex(index);
        // Keep swapping the parent element with the child element until the parent element is less than
        // the getIndex element or until our getIndex has become the root
        while ( index != ROOT_INDEX
                && elements[ index ].compareTo( elements[ parentIndex ] ) < 0 ) {
            swap (elements, index, parentIndex );
            index = parentIndex;
            parentIndex = getParentIndex(index);
        }
    }

    // Keep swapping the element located at the getIndex location with its minimum child getIndex until the element
    // is smaller than both its children, and the min heap property is satisfied
    private void heapifyDown (int index) {
        // Make sure that the getIndex is within range
        assert isValidIndex ( index );
        // Get the getIndex of the child element with the minimum value
        int minChildIndex = getMinChildIndex( index );
        // If the returned child element getIndex is valid and the child element value is less than the value located
        // at our getIndex, keep swapping the getIndex element with the minimum child getIndex
        while (isValidIndex( minChildIndex ) && elements[minChildIndex].compareTo(elements[index]) < 0) {
            swap( elements, index, minChildIndex);
            index = minChildIndex;
            minChildIndex = getMinChildIndex( index );
        }

    }

    // Insert the input element into our heap
    public void insert ( T element ) {
        // Check if we have a valid (non-null) element
        if (element == null) return;
        // Check if we have enough array capacity, and if not, we increase it and copy our element into our new array
        if (isFull()) elements = Arrays.copyOf(elements, 2 * size);
        // Add the new element to the end of our new array
        elements[size++] = element;
        // Find the proper placement of the new element in our heap by swapping it with its parent until the min
        // heap property is satisfied
        heapifyUp(size - 1);
    }

    // Get the minimum heap element, which is the root element
    public T getMin () {
        if (isEmpty()) return null;
        else return elements[ROOT_INDEX];
    }

    // Return and remove the minimum element
    public T extractMin () {
        // If the heap is empty, return null
        if (isEmpty()) return null;
        // Fetch the element at the root getIndex, since this is our minimum element
        T minElement = elements[ROOT_INDEX];
        // Delete the root element from our heap
        delete ( ROOT_INDEX );
        return minElement;
    }

    // Remove the element at the specified getIndex location from the heap
    public boolean delete ( int index ) {
        // If the element getIndex is out of range, return false to indicate no element was removed
        if (index < 0 || index >= size) return false;
        // Replace the deleted element with the last heap element and decrement the heap size
        elements[index] = elements[size - 1];
        size = size - 1;
        // Keep swapping down the newly inserted element until the min heap property is satisfied
        heapifyDown( index );
        // Return true to indicate that our deletion was successful
        return true;
    }

    // Return an iterator which iterates over the heap elements
    public Iterator<T> iterator()  {
        return Arrays.asList(Arrays.copyOf(elements, size)).iterator();
    }

    // Returns a string representation of the entire heap
    @Override
    public String toString() {
        if (isEmpty()) return "";
        // String builder used to build a delimited list of heap elements
        StringBuilder stringBuilder = new StringBuilder();
        // Delimiter used to separate the heap elements
        String delimiterString = ", ";
        // Iterate through each heap element appending each getIndex, element pair located in our heap
        for (int i = 0; i < size; i++) {
            stringBuilder.append("(").append(i).append(delimiterString).append(elements[i]).append(")").append(delimiterString);
        }
        // Return the delimited string of elements, with the last delimiter string removed
        return stringBuilder.toString().substring(0, stringBuilder.lastIndexOf(delimiterString));

    }

    public static void main(String[] args) throws Throwable {
        MinHeap<Integer> minHeap = MinHeap.newInstance();
        minHeap.insert(5);
        minHeap.insert(8);
        minHeap.insert(15);
        minHeap.insert(2);
        minHeap.insert(9);
        minHeap.insert(6);
        minHeap.insert(20);
        minHeap.insert(14);
        minHeap.insert(25);
        minHeap.insert(22);
        minHeap.insert(1);
        minHeap.insert(3);
        minHeap.insert(4);
        minHeap.insert(6);
        System.out.println(minHeap);

    }
}
