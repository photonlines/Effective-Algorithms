import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Data Structure: Stack
 * Implementation: Array
 * Time Complexity  - Average Case Access: O(n); Search: O(n); Insertion: O(1); Deletion: O(1)
 *                  - Worst Case Access: O(n); Search: O(n); Insertion: O(1); Deletion: O(1)
 * Space Complexity - Worst Case: O(n)
 * Use Cases:       - When a collection of elements need to be stored in LIFO (Last In, First Out) Order
 *                  - Expression evaluation and syntax parsing; backtracking; runtime memory management
 *                  - When speed is more important than space requirements/upper bounds.
 */
public class ArrayStack<T> implements Iterable<T> {
    // Generic array used to hold our stack elements
    private T[] elements;
    // Used to hold our stack size
    private int size = 0;
    // Default initial stack capacity
    private static final int DEFAULT_INITIAL_CAPACITY = 32;

    // Default private constructor which intializes the array to a default initial capacity.
    private ArrayStack() {
        elements = (T[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    // Constructor which takes an initial capacity and initializes the element array
    @SuppressWarnings("unchecked")
    private ArrayStack(final int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("The initial capacity must be greater than 0.");
        elements = (T[]) new Object[initialCapacity];
    }

    // Static factory method which returns a new stack instance with default initial capacity
    public static <T> ArrayStack<T> newInstance() {
        return new ArrayStack<T>();
    }

    // Static factory method which returns a new stack instance with the passed in capacity
    public static <T> ArrayStack<T> newInstance(final int initialCapacity) {
        return new ArrayStack<T>(initialCapacity);
    }

    // Return the number of elements in our stack
    public int getSize() {
        return size;
    }

    // Return true if our stack has no elements in it; false otherwise.
    public boolean isEmpty() {
        return (getSize() == 0);
    }

    // Return true if our stack has reached its capacity; false otherwise.
    public boolean isFull() {
        return (getSize() == elements.length);
    }

    // Push element of type T to the top of the stack
    public void push(T element) {
        // If the stack element array is full, double the array size
        if (isFull()) elements = Arrays.copyOf(elements, 2 * size );
        // Push the element to the end of the array and increment the size
        elements[size++] = element;
    }

    // Pop the top element from the stack
    public T pop() {
        // If our stack is empty, a pop cannot be performed; return null
        if (isEmpty()) return null;
        // Fetch the top element from the array and decrement the stack size
        T result = elements[--size];
        // Null the popped element from the array to eliminate the unneeded reference
        elements[size] = null;
        // Return the popped element
        return result;
    }

    // Return the top element from the stack without removing it from the element array
    public T peek() {
        // If our stack is empty, there is no top element; return null
        if (isEmpty()) return null;
        // Return the top stack element
        return elements[size - 1];
    }

    // Return an iterator which iterates over the elements in our stack FIFO order
    public Iterator<T> iterator()  {
        // Pass in a copy of our original array only containing our stack elements
        return new ArrayIterator<T>(Arrays.copyOf(elements, size));
    }

    // Iterator class which allows us to iterate through the stack elements
    private class ArrayIterator<T> implements Iterator<T> {
        // Array containing our stack elements
        private T elements[];
        // The getIndex of the next array element to process
        private int nextIndex = 0;
        // Constructor which takes in the stack elements as an array
        public ArrayIterator(T elements[]) {
            this.elements = elements;
            nextIndex = elements.length - 1;
        }
        // Return if the iteration has more elements to process
        public boolean hasNext() {
            return nextIndex >= 0;
        }
        // Throw an unsupported operation exception since we only want to implement element removal through our
        // stack interface pop operation
        public void remove() {
            throw new UnsupportedOperationException();
        }
        // Return the next element to process from the stack and decrement the getIndex to reference the next element
        // we need to process
        public T next() throws NoSuchElementException {
            if (hasNext())
                return elements[nextIndex--];
            else
                throw new NoSuchElementException();
        }
    }

    // Return a string representation of the stack collection
    @Override
    public String toString() {
        if (isEmpty()) return "";
        // Return an array string representation of the sub array containing our stack elements
        return java.util.Arrays.toString(Arrays.copyOf(elements, size));
    }

    public static void main(String[] args) throws Throwable {
        ArrayStack<String> greeting = ArrayStack.newInstance();

        for (String s : greeting) {
            System.out.println(s);
        }

        System.out.println(greeting.toString()); // Hello, World!

    }
}
