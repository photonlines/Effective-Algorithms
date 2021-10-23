import java.util.*;

/**
 * Created by User on 4/5/2016.
 */
public class SkipList <T extends Comparable<T>> implements Iterable<T> {

    // The default number of level specified for our skip list
    private static final int DEFAULT_LEVEL_NUMBER = 4;

    // This is used to store our skip list start node.
    private SkipListNode<T> startNode;

    // The maximum number of levels/height of our skip list
    private final int numberOfLevels;

    // Used to hold our list size
    private int size = 0;

    // Used to generate the random numbers we use to generate a level for each linked list node
    private Random random = new Random();

    // The three types of nodes we have in our linked list - the first and end nodes have no elements/values,
    // they are used as simple representations of the beginning and end of our list. The internal nodes
    // are the nodes we use to actually hold the values.
    private enum NodeType {
        START_NODE, INTERNAL_NODE, END_NODE;
    }

    // A linked list node represntation we use to store our values, next node pointers, and node types,
    // as well as a few utility methods for manipulating the nodes.
    private class SkipListNode<T extends Comparable<T>> {

        // This is the linked list element or value
        private final T element;

        // This is a list of next nodes located to the right of our current element (and thus composed of
        // larger elements. Each node in the list represents a different skip list level, with level 0
        // nodes representing the lowest level nodes (no skipped elements) and the higher indexed nodes
        // representing the higher levels of our skip list
        private List<SkipListNode<T>> nextNodes;

        // This is the node type of our current node (START, END, OR INTERNAL)
        private NodeType nodeType;

        // A constructor which is used to create start and end nodes for our list and hold no internal elements
        private SkipListNode(NodeType nodeType) {
            this.element = null;
            this.nextNodes = new ArrayList<SkipListNode<T>>();
            this.nodeType  = nodeType;
        }

        // A constructor used to create our regular internal nodes and which have an element/value
        private SkipListNode(T element) {
            this.element = element;
            this.nextNodes = new ArrayList<SkipListNode<T>>();
            this.nodeType = NodeType.INTERNAL_NODE;
        }

        // Return the element/value of our current node
        private T getElement() { return element; }

        // Return the current node node-type (START, INTERNAL, END)
        private NodeType getNodeType() { return nodeType; }

        // Get our current node level/number of levels
        private int getNumberOfLevels() { return this.nextNodes.size(); }

        // Return the next node at the specified level located to the right of our current node - or, if
        // there is no node located on the specified level, return and empty end node.
        private SkipListNode<T> getNextNodeAtLevel (int level) {
            return level < this.getNumberOfLevels()
                ? nextNodes.get(level)
                    : new SkipListNode<T>(NodeType.END_NODE);
        }

        // Set our current node next node to point to the input node at the specified level - if no current
        // levels exist at our current level, or our bottom most levels, we initialize them to be end nodes
        private void setNextNodeAtLevel( int level, SkipListNode<T> node) {
            initializeAllLowerLevelNodes( level );
            this.nextNodes.set(level, node != null ? node : new SkipListNode<T>(NodeType.END_NODE) );
        }

        // If the nodes below or at the specified level are not initialized, initialize them to contain end nodes
        private void initializeAllLowerLevelNodes(int level) {
            for (int i = this.getNumberOfLevels(); i <= level; i++)
                this.nextNodes.add(i, new SkipListNode(NodeType.END_NODE));
        }

        // Compare the element of our current node to the input node, and return true if the element of our node is
        // less than the input node element - and false otherwise
        private boolean isLessThan(SkipListNode<T> inputNode) {
            if (this.element.compareTo(inputNode.getElement()) < 0) return true;
            return false;
        }

        // Compare the element at our input node to the input element, and if it is less than the input node, return
        // true - and return false otherwise. As a note, we always mark the start nodes less than all internal and
        // end nodes, and all end nodes as being greater than all internal and start nodes
        private boolean isLessThan(T element) {
            if (this.nodeType == NodeType.START_NODE) return true;
            else if (this.nodeType == NodeType.END_NODE) return false;
            else return (this.element.compareTo(element) < 0);
        }

        // Check if our internal node element is logically equal to the input element, and return true if they are
        // equal, false otherwise.
        private boolean elementEquals(T element) {
            if (this.getNodeType() == NodeType.START_NODE || this.getNodeType() == NodeType.END_NODE) return false;
            return this.element.compareTo(element) == 0;
        }

        // Return whether our current node has an internal node ( a node with an element/value) at the specified level
        private boolean hasInternalNodeAtLevel(int level) {
            return (this.getNumberOfLevels() > level
                    && this.getNextNodeAtLevel(level).getNodeType() == NodeType.INTERNAL_NODE);
        }

    }

    // A list constructor used to create skip lists with the maximum number of levels equal to the specified
    // input number, as well as initialize the start and end nodes of our list
    SkipList(int numberOfLevels) {
        this.numberOfLevels = numberOfLevels;
        this.startNode = new SkipListNode(NodeType.START_NODE);
        this.startNode.setNextNodeAtLevel(this.numberOfLevels - 1, new SkipListNode<>(NodeType.END_NODE));
    }

    // Create and return a new skip list with a maximum number of levels set to the input maximum
    private static <T extends Comparable<T>> SkipList<T> getInstance (int max_levels) { return new SkipList<T>(max_levels); }

    // Create a skip list with the maximum number of level set to the default value
    private static <T extends Comparable<T>> SkipList<T> getInstance () { return new SkipList<T>(DEFAULT_LEVEL_NUMBER); }

    // Get the number of levels we have in our skip list
    private int getNumberOfLevels() { return numberOfLevels; }

    // Returns the number of elements in our skip list
    public int getSize() { return size; }

    // Generate a random level number from 0 to our max number of levels - 1, which will be the node level number
    // we use for an inserted node. To generate this number, we simply generate a random number and count the number
    // of leading one bits in this random number, and we use this as our node level.
    private int generateNodeLevel() {

        int level = 0;
        // Generate a random integer which we will use to generate a random node level
        for (int randomNumber = random.nextInt();
             // Check if the leading bit is one, or if we have reach the maximum number of levels
             (randomNumber & 1) == 1 && level < getNumberOfLevels() - 1;
             // Keep shifting right for every leading one bit encountered
             randomNumber = randomNumber >> 1) {

            // Increment the level for each leading one bit
            level = level + 1;

        }

        return level;

    }

    // Add a new element to our skip list with the internal value/element set to the input element of type T
    public boolean add(T element) {

        // If the input element to insert is null, we simply return as we don't allow for null elements in our list
        if (element == null) return false;

        // Generate a node level for the added in element node
        int nodeLevel = generateNodeLevel();

        // Create a new internal skip list node used to hold our element
        SkipListNode newNode = new SkipListNode(element);

        // Declare a new node which we will use to iterate over the elements of our skip list in order to find an insert
        // position for our new element node - we initialize our current element to point to the first node in our list
        SkipListNode currentNode = startNode;

        // For each level of our new node, starting from the highest to lowest, find an appropariate insert position
        // and insert the new element
        for (int currentLevel = nodeLevel; currentLevel >= 0; currentLevel--) {

            // Keep checking if the list has more elements, and if they are less than our new node element,
            // keep moving forward
            while ( currentNode.hasInternalNodeAtLevel(currentLevel)
                    && currentNode.getNextNodeAtLevel(currentLevel).isLessThan(element) ) {
                currentNode = currentNode.getNextNodeAtLevel(currentLevel);
            }

            // Set the new node next level pointer to point to the next node in our iteration - since this
            // node will have an element which is greater than or equal to our new node element (and is thus
            // located to the right of our new node)
            newNode.setNextNodeAtLevel(currentLevel, currentNode.getNextNodeAtLevel(currentLevel));
            // Set the current node pointer to point to the new node, since our current node element is less than the
            // new node, and is thus located on its left side
            currentNode.setNextNodeAtLevel(currentLevel, newNode);

        }

        size = size + 1;

        return true;

    }

    // Return whether our skip list contains the provided element
    public boolean contains(T element) {

        // Our list doesn't allow null elements, so we return false
        if (element == null) return false;

        // Start iterating over our list nodes starting from our start node
        SkipListNode currentNode = startNode;

        // For each level in our list starting from highest to lowest:
        for (int currentLevel = getNumberOfLevels() - 1; currentLevel >= 0; currentLevel--) {

            // Keep checking if the list has more elements, and if they are less than our new node element,
            // keep moving forward
            while ( currentNode.hasInternalNodeAtLevel(currentLevel)
                    && currentNode.getNextNodeAtLevel(currentLevel).isLessThan(element) ) {
                currentNode = currentNode.getNextNodeAtLevel(currentLevel);
            }

            // This means that our next node is either an end node, or a node which is greater than or equal
            // to our element, so check for equality and return true if our elements are equal
            if (currentNode.getNextNodeAtLevel(currentLevel).elementEquals(element)) return true;

        }

        // Return false, since we have iterated over our list and have not found the search element
        return false;

    }

    // Delete the element from our list - if our list doesn't contain the element, do nothing.
    public boolean remove(T element) {

        // If the element is null, return false since we don't allow null values in the list
        if (element == null) return false;

        // We use this node to iterate over our entire skip list - starting with our start node
        SkipListNode currentNode = startNode;

        // For each level in our list starting from highest to lowest:
        for (int currentLevel = getNumberOfLevels() - 1; currentLevel >= 0; currentLevel--) {

            // Keep checking if the list has more elements, and if they are less than our new node element,
            // keep moving forward
            while ( currentNode.hasInternalNodeAtLevel(currentLevel)
                    && currentNode.getNextNodeAtLevel(currentLevel).isLessThan(element) ) {
                currentNode = currentNode.getNextNodeAtLevel(currentLevel);
            }

            // Check if our next node contains the element we want to remove, and if it does, we remove the
            // node by setting our current node pointer to our deleted element's next node and we decrement
            // the size
            if (currentNode.getNextNodeAtLevel(currentLevel).elementEquals(element)) {
                currentNode.setNextNodeAtLevel(currentLevel, currentNode.getNextNodeAtLevel(currentLevel).getNextNodeAtLevel(currentLevel));

                // If we are on our lowest level, decrement the size
                // and return true to indicate that we succesfully removed the element
                if (currentLevel == 0) {
                    size = size - 1;
                    return true;
                }
            }

        }

        // Return false since we know that our list did not contain the element
        return false;

    }

    // Return an iterator which iterates over all of the elements in our skip list
    public Iterator<T> iterator()  {
        return new SkipListLevelIterator<T>(startNode, 0);
    }

    public Iterator<T> levelIterator(int level) { return new SkipListLevelIterator<T>(startNode, level); }

    // Iterator class which allows us to iterate through the skip list elements
    private class SkipListLevelIterator<T extends Comparable<T>> implements Iterator<T> {

        // Holds the next node to process
        private SkipListNode<T> nextNode;

        // The skip list level we are iterating over
        private int level;

        // Constructor which takes in the first node of our linked list and iterates over all of the
        // nodes at the specified level
        public SkipListLevelIterator(SkipListNode<T> startNode, int level) {
            this.level = level;
            this.nextNode = startNode.getNextNodeAtLevel(level);
        }
        // Check if the next element in our list is an internal node which we can process
        public boolean hasNext()  {
            return nextNode.getNodeType() == NodeType.INTERNAL_NODE;
        }

        // Throw an unsupported operation exception since we do not want to support removal operations
        public void remove()      {
            throw new UnsupportedOperationException();
        }

        // Return the next element to process
        public T next() {
            // Check if our next skip list node is a valid internal node, and throw and exception if it is not
            if (!hasNext()) throw new NoSuchElementException();
            // Fetch the element from our next node, and set the next node pointer to point to the next node
            // for out level
            T element = nextNode.getElement();
            nextNode = nextNode.getNextNodeAtLevel(level);

            return element;
        }
    }

    // Return a string representation of the skip list, which will show all of the elements at each level that
    // has inserted elements.
    @Override
    public String toString() {

        // String builder used to build a delimited list of skip list elements
        StringBuilder stringBuilder = new StringBuilder();

        // Delimiter used to separate the list elements
        String delimiterString = ", ";

        // For each level in our list starting from highest to lowest:
        for (int currentLevel = getNumberOfLevels() - 1; currentLevel >= 0; currentLevel--) {

            // Get an iterator to iterate over the element nodes at the current level
            Iterator<T> iterator = this.levelIterator(currentLevel);

            // If our level has valid element nodes:
            if (iterator.hasNext()) {

                // Append our current level index information to the string builder
                stringBuilder.append("Level " + currentLevel + " Node Elements: ");

                // Append each internal element in our level followed by a string delimiter
                while (iterator.hasNext())
                    stringBuilder.append(iterator.next()).append(delimiterString);

                // Remove the last delmiter string from the string builder (since we don't need it for the last element)
                stringBuilder.setLength(stringBuilder.length() - delimiterString.length());

                // We add a new line to the end of each level list
                stringBuilder.append(System.getProperty("line.separator"));

            }
        }

        // Return the string representation of our skip list
        return stringBuilder.toString();

    }

}
