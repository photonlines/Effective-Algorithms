import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Data Structure: Queue
 * Implementation: Linked List
 * Time Complexity  - Average Case Access: O(n); Search: O(n); Insertion: O(1); Deletion: O(1)
 *                  - Worst Case Access: O(n); Search: O(n); Insertion: O(1); Deletion: O(1)
 * Space Complexity - Worst Case: O(n)
 * Use Cases:       - When a collection of elements need to be stored in FIFO (First In, First Out) Order
 *                  - Resource sharing among consumers; CPU scheduling; asynchronous data transfer
 */
public class LinkedListQueue<T> implements Queue<T>, Iterable<T> {

    private LinkedListNode<T> firstNode;
    private LinkedListNode<T> lastNode;
    // Used to hold our stack size
    private int size = 0;

    // Objects used to hold our linked list nodes and their elements
    private class LinkedListNode<T> {
        private T element;
        private LinkedListNode<T> nextNode;
    }

    LinkedListQueue() { }

    // Static factory method which returns a new queue instance
    public static <T> LinkedListQueue<T> newInstance() {
        return new LinkedListQueue<T>();
    }

    // Returns the number of elements in our queue
    public int getSize() { return size; }

    // Return true if our queue has no elements in it; false otherwise.
    public boolean isEmpty() {
        return (getSize() == 0);
    }

    // Add element of type T to the queue
    public void enqueue(T element) {
        // Create a new linked list node and initialize it to hold our element
        LinkedListNode<T> newNode = new LinkedListNode<T>();
        newNode.element = element;
        // If the queue is empty, set our first and last node references to point to the new node
        if (isEmpty()) {
            lastNode = newNode;
            firstNode = newNode;
        } else {
            // If the queue is not empty, we add a new reference to the new node using our last linked list node
            // and update the last node to pount to the newly inserted element
            lastNode.nextNode = newNode;
            lastNode = newNode;
        }
        // Increment the size counter
        size = size + 1;
    }

    // Removes and returns the head element of the queue
    public T dequeue() {
        // If our queue is empty, return a null element
        if (isEmpty()) return null;
        // Fetch the head element in the queue (the first node element)
        T result = firstNode.element;
        // Set our first linked list node to point to the next element, since we are dequeing the first element from
        // the queue
        firstNode = firstNode.nextNode;
        // Decrement the size
        size = size - 1;
        // If our queue is now empty, we eliminate the obsolete last element reference
        if (isEmpty()) lastNode = null;
        // Return the dequeued element
        return result;
    }

    // Return the head element from the queue without removing it
    public T peek() {
        // If our queue is empty, there is no head element; throw and illegal state exception
        if (isEmpty())
            return null;
        // Return the top stack element
        return firstNode.element;
    }

    // Return an iterator which iterates over the elements in our queue
    public Iterator<T> iterator()  {
        return new LinkedListIterator<T>(firstNode);
    }

    // Iterator class which allows us to iterate through the queue elements
    private class LinkedListIterator<T> implements Iterator<T> {
        // Holds the next node to process
        private LinkedListNode<T> nextNode;
        // Constructor which takes in the first node of our queue
        public LinkedListIterator(LinkedListNode<T> firstNode) {
            nextNode = firstNode;
        }
        // Return if there are more elements left to process
        public boolean hasNext()  {
            return nextNode != null;
        }
        // Throw an unsupported operation exception since we only want to implement element removal through our
        // queue interface dequeue operation
        public void remove()      {
            throw new UnsupportedOperationException();
        }
        // Return the next element to process from the queue and set our next node element to the next queue node
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            T element = nextNode.element;
            nextNode = nextNode.nextNode;
            return element;
        }
    }

    // Return a string representation of the queue collection
    @Override
    public String toString() {
        if (isEmpty()) return "";
        // String builder used to build a delimited list of queue elements
        StringBuilder stringBuilder = new StringBuilder();
        // Start at the first linked list node
        LinkedListNode<T> node = firstNode;
        // Delimiter used to separate the queue elements
        String delimiterString = ", ";
        // Iterate through each queue element appending it to our builder
        for (T element : this) {
            stringBuilder.append(element).append(delimiterString);
        }
        // Return the delimited string of elements, with the last delimiter string removed
        return stringBuilder.toString().substring(0, stringBuilder.lastIndexOf(delimiterString));
    }

}
