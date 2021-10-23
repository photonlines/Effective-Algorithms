import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Data Structure: Binary Search Tree
 * Time Complexity  - Average Case Access: O(log(n)); Search: O(log(n)); Insertion: O(log(n)); Deletion: O(log(n))
 *                  - Worst Case Access: O(n); Search: O(n); Insertion: O(n); Deletion: O(n)
 * Space Complexity - Worst Case: O(n)
 * Use Cases:       - When data is randomly distributed and un-ordered
 *                  - When in need of efficient in-order transversal and access
 */
public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {

    // Contains our root node
    private BSTNode<T> rootNode;

    // Objects used to hold our binary search tree nodes and elements
    private class BSTNode<T extends Comparable<T>> {
        // Element node containing data
        private T element;
        // left and right children of the current element
        private BSTNode<T> leftChild;
        private BSTNode<T> rightChild;
        // Element constructor and helper methods
        private BSTNode(T element) { this.element = element; }
        private T leftElement() { return leftChild == null ? null : leftChild.element; }
        private T rightElement() { return rightChild == null ? null : rightChild.element; }
        private boolean hasLeftChild() { return leftChild != null; }
        private boolean hasRightChild() { return rightChild != null; }
        private boolean hasBothChildren() { return hasLeftChild() && hasRightChild(); }
    }

    private BinarySearchTree() { }
    // Static factory method which returns a new binary search tree representation
    public static <T extends Comparable<T>> BinarySearchTree<T> newInstance() {
        return new BinarySearchTree<T>();
    }
    // Returns true if we have an element in our binary search tree; false otherwise
    public boolean isEmpty( ) { return rootNode == null; }
    // Returns the number of elements in our binary search tree
    public int getSize() { return getSize(rootNode); }
    // Recursively transverse all BST nodes to get the node count
    private int getSize ( BSTNode<T> node ) {
        // If the node is null, no element stored in node so return 0
        if (node == null) return 0;
        // Variable used to store number of elements in the node children
        int numChildren = 0;
        // Get the number of elements in the left child node
        numChildren += getSize(node.leftChild);
        // Get the number of elements in the right child node
        numChildren += getSize(node.rightChild);
        // return the total element count found in the child nodes and add 1 for our current node
        return 1 + numChildren;
    }

    // Insert element of Type T into our BST
    public void insert ( T element ) {
        // If the element is null, we simply return
        if (element == null) return;
        // Transverse down the root element and insert the new element node into an appropriate position
        rootNode = insertNewNode(rootNode, element);
    }

    // Insert a new element node by transversing down the node parameter and inserting the new element node into the
    // appropriate position
    private BSTNode<T> insertNewNode (BSTNode<T> node, T element) {
        // If our transversed node is null, we found a free spot; we insert the new node element and return
        if (node == null) {
            node = new BSTNode<T>(element);
            // If the element to insert is less than the current node element, continue transversing down the
            // left child node until a free spot is found
        } else if (element.compareTo(node.element) < 0) {
            node.leftChild = insertNewNode (node.leftChild, element);
            // If the element to insert is larger than the current node element, continue transversing down the
            // right child node until a free spot is found
        } else if (element.compareTo(node.element) > 0) {
            node.rightChild = insertNewNode (node.rightChild, element);
        }
        return node;
    }

    // Delete the node containing the element parameter from our BST
    public void delete (T element) {
        // If the element is null, simply return, as we do not permit null elements in the BST
        if (element == null) return;
        // Delete the element node by transversing down the root node of our tree, finding the node element, and
        // removing it from the BST
        rootNode = deleteNode(rootNode, element);

    }

    // Delete the node containing the parameter element from our BST
    private BSTNode<T> deleteNode (BSTNode<T> node, T element) {
        // If our node is null, we have transversed the tree without finding the element and cannot remove it, so we
        // simply return a null value
        if (node == null) {
            return null;
            // If the element to remove is less than the current node element, it means that the element must be in the
            // left subtree, so we continue tranversing down the left child node until we find the element to remove
        } else if (element.compareTo(node.element) < 0) {
            node.leftChild = deleteNode(node.leftChild, element);
            // If the element to remove is larger than the current node element, it means that the element must be in the
            // right subtree, so we continue tranversing down the right child node until we find the element to remove
        } else if (element.compareTo(node.element) > 0) {
            node.rightChild = deleteNode(node.rightChild, element);
        } else {
            // This means that the current node is the node we want to remove, so we remove the node from our tree
            // If our node has both children, we fetch the minimum element from our right subtree (containing the next
            // largest element value after our deleted element) and we remove it from the right subtree and
            // insert it into the current position
            if (node.hasBothChildren()) {
                T minElement = getMin(node.rightChild);
                node.element = minElement;
                node.rightChild = deleteNode (node.rightChild, minElement);
                // If the node we need to remove only has a left child and no right child, we simply set the current
                // node to point to the left child element/sub-tree
            } else if (node.hasLeftChild()){
                node = node.leftChild;
                // Otherwise, our node only has a right child with nothing on its left, so we simply set the current
                // node to point to the right child element/sub-tree
            } else {
                node = node.rightChild;
            }
        }
        return node;
    }

    // Retrieve the input parememter element from the BST; return null if the element does not exist
    public T get ( T element ) {
        // If we are searching for a null element, we simply return null, since we know our tree has no null elements
        if (element == null) return null;
        // Transverse down the root tree node until the node containing the element value is found
        BSTNode<T> node = getNode ( rootNode, element );
        // If no node is found, return null; otherwise, return the found node element
        return (node == null) ? null : node.element;
    }

    // Get the node containing the parameter element by transversing down each left and right child of the input
    // node and comparing the node values to the input element
    private BSTNode<T> getNode (BSTNode<T> node, T element) {
        while( node != null ) {
            // If the search element is less than the current node element, continue transversing down the left subtree
            if( element.compareTo( node.element ) < 0 ) {
                node = node.leftChild;
                // If the search element is larger than the current node element, continue transversing down the right
                // subtree
            } else if( element.compareTo( node.element ) > 0 ) {
                node = node.rightChild;
            } else {
                // Otherwise, we found our node, so we return it
                return node;
            }
        }
        // If we found our node, we've already returned it, so we return null here to indicate we did not find our node
        // element
        return null;
    }

    // Return true is our BST contains the input element; false otherwise
    public boolean contains ( T element ) {
        return get( element ) != null;
    }

    // Get the minimum BST element and return it
    public T getMin ( ) {
        // Find the minimum element node starting from the root element and return it
        return getMin( rootNode );
    }

    // Get the minimum element value rooted at the node
    private T getMin ( BSTNode<T> node ) {
        // If the node is null, return null to indicate that no minimum element value was found
        if( node == null ) return null;
        // Continue transversing down all left child nodes until the left-most child is found, and return the element
        // value at this node since it contains the minimum BST element.
        while( node.leftChild != null ) node = node.leftChild;
        return node.element;
    }

    // Get the maximum BST element value and return it
    public T getMax ( ) {
        // Find the maximum element node starting from the root element and return it
        return getMax(rootNode);
    }

    // Get the maximum element value rooted at the node
    private T getMax ( BSTNode<T> node ) {
        // If the node is null, return null to indicate that no maximum element value was found
        if( node == null ) return null;
        // Continue transversing down all right child nodes until the right-most child is found, and return the element
        // value at this node since it contains the maximum BST element.
        while( node.rightChild != null ) node = node.rightChild;
        return node.element;
    }

    // Return whether our BST maintains all BST properties 1. All nodes on the left subtree
    // have element values that are less than the current node element value; 2. All nodes on the right subtree have
    // element values that are larger than the current node element value; 3. All left and right subtree nodes also
    // are BSTs and maintain the above 2 properties.
    private boolean isBinarySearchTree ( BSTNode<T> node ) {
        if (node == null) return true;
        if (node.hasLeftChild() && node.leftElement().compareTo(node.element) > 0) return false;
        if (node.hasRightChild() && node.rightElement().compareTo(node.element) < 0) return false;
        return (isBinarySearchTree(node.leftChild) && isBinarySearchTree(node.rightChild));
    }

    // Get an in-order list of our BST elements
    public List<T> getInOrderElementList() {
        // New array list used to store our BST elements
        List<T> list = new ArrayList<T>();
        // Tranverse down the root node and retrieve a list of elements contained in each node
        getInOrderElementList( rootNode, list );
        // Return the in-order list
        return list;
    }

    // Get an in-order list of elements rooted at the input node
    private void getInOrderElementList( BSTNode<T> node, List<T> resultList) {
        // If our input node is not null, we recursively transverse down the left subtrees first, followed by the
        // current node element, then the right subtree in order to add all BST elements in proper order to our
        // list
        if (node != null) {
            getInOrderElementList ( node.leftChild, resultList );
            // We only add the current node element after transversing / adding all left elements, since all of the
            // left subtree nodes contain elements less than our current element node
            resultList.add( node.element );
            // Tranverse down the right subtree after adding current node, since we know all elements in this
            // branch must be larger than our current node element
            getInOrderElementList ( node.rightChild, resultList );
        }
    }

    // Returns an iterator which allows us to iterate over each element in our BST in sorted order
    public Iterator<T> iterator()  {
        return getInOrderElementList().iterator();
    }

    // Returns a string representation of all in-order elements in our BST
    @Override
    public String toString() {
        if (isEmpty()) return "";
        // String builder used to build a delimited list of BST elements
        StringBuilder stringBuilder = new StringBuilder();
        // Get the in order element list
        List<T> elementList = getInOrderElementList();
        // Delimiter used to separate the BST elements
        String delimiterString = ", ";
        // Iterate through each BST element appending it to our builder
        for (T element : elementList) {
            stringBuilder.append(element).append(delimiterString);
        }
        // Return the delimited string of elements, with the last delimiter string removed
        return stringBuilder.toString().substring(0, stringBuilder.lastIndexOf(delimiterString));

    }

}


