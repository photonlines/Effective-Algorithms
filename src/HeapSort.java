import java.util.*;

/**
 * Algorithm: Heap Sort
 * Time Complexity  - Average Case: O(n log(n)); Worst Case: O(n log(n))
 * Space Complexity - Worst Case: O(1)
 * Use Cases:       - When in need of an excellent in-place sorting algorithm
 *                  - Because of its O(n log(n)) upper bound on running time and upper bound on auxiliary space,
 *                    a good choice for embedded systems and safety critical applications.
 */
public class HeapSort {

    private HeapSort () { }

    /**
     * Using heap sort, sort a list of elements which are Comparable or which have a superclass
     * which is Comparable. The below heap sort is a variation of the regular max heap sort - to minimize
     * complexity, we build a min heap and continually extract the minimum element from it and then exchanging the
     * last element with our minimum element and sifting down to once again maintain our minimum heap property.
     * The below implementation can be improved by a large factor in terms of both running time and space.
     */
    public static <T extends Comparable<T>> void sort(List<T> list) {
        if (list == null || list.size() <= 1) return;
        // We build our min heap by copying the original list and continually heapifying the top elements in it
        List<T> minHeap = new ArrayList<T>(list);
        minHeapify(minHeap);
        // Build the in-order list by continually extracting the minimum element from our heap and adding it to the
        // front of our list
        int index = 0;
        while (minHeap.size() > 0) {
            // Set the next list element to be the heap root element, since the root holds the minimum value
            list.set(index++, minHeap.get(0));
            // Swap the minimum (root) element with the last heap element
            swap(minHeap, 0, minHeap.size() - 1);
            // Remove the last heap element from our list, which now holds the root element, since we have already
            // added it to our in-order list
            minHeap.remove(minHeap.size() - 1);
            // Sift down the current root element, exchanging it with the lowest valued child element until the
            // min heap property is once again satisfied and our root once again holds the minimum list element
            siftDown(minHeap, 0);
        }
    }

    /**
     * Put the elements in the list in a min heap order.
     */
    private static <T extends Comparable<T>> void minHeapify(List<T> list) {
        // Start heapifying with the rightmost internal element and move up through each internal node, processing
        // the root element last and building our heap by sifting down the larger elements while maintaining our
        // heap order.
        int heapifyIndex = (list.size() - 2) / 2;
        while (heapifyIndex >= 0) {
            siftDown(list, heapifyIndex--);
        }
    }

    /**
     * Sift down the node at the passed in current getIndex so that each note below this getIndex are in heap order
     */
    private static <T extends Comparable<T>> void siftDown(List<T> list, int currentIndex) {
        // Initialize the current node as being the minimum getIndex node
        int minIndex = currentIndex;
        // Fetch the indexes of the left and right children of the current node
        int leftChildIndex = ( currentIndex * 2 ) + 1;
        int rightChildIndex = ( currentIndex * 2 ) + 2;
        // If the current node has a left child and the left child element value is lower than the value stored in
        // our minimum getIndex, we set our minimum getIndex to point to the left child node
        if (list.size() > leftChildIndex
                && list.get(leftChildIndex).compareTo(list.get(minIndex)) < 0) {
            minIndex = leftChildIndex;
        }
        // If the current node has a right child and the right child element value is lower than the value stored in
        // our minimum getIndex, then we set our minimum getIndex to point to the right child node
        if (list.size() > rightChildIndex
                && list.get(rightChildIndex).compareTo(list.get(minIndex)) < 0) {
            minIndex = rightChildIndex;
        }
        // If our minimum getIndex element is set to one of the children of the current element - we swap the current
        // element with the minimum getIndex child element, and continue to sift down the minimum getIndex element
        // recursively to ensure that the min heap property is maintained and all children are larger than their
        // parents
        if (minIndex != currentIndex) {
            swap (list, minIndex, currentIndex);
            siftDown(list, minIndex);
        }
    }

    // Swap the ith and jth elements in list.
    private static final <T> void swap (List<T> list, int i, int j) {
        assert list != null;
        assert i >= 0 && i < list.size();
        assert j >= 0 && j < list.size();
        Collections.<T>swap(list, i, j);
    }
}
