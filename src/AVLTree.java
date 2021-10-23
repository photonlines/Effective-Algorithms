import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by User on 10/21/2015.
 */
public class AVLTree<T extends Comparable<T>> implements Iterable<T> {

    // The root node of our tree
    private AVLNode<T> rootNode;

    // Objects used to hold our AVL tree nodes and elements
    private class AVLNode<T extends Comparable<T>> {
        // Element node containing data
        private T element;
        // left and right children of the current element
        private AVLNode<T> leftChild;
        private AVLNode<T> rightChild;
        // Element constructor and helper methods
        private AVLNode(T element) { this.element = element; }
        private T leftElement() { return leftChild == null ? null : leftChild.element; }
        private T rightElement() { return rightChild == null ? null : rightChild.element; }
        private boolean hasLeftChild() { return leftChild != null; }
        private boolean hasRightChild() { return rightChild != null; }
        // Return true if the parameter element is less than the left child element; false otherwise
        private boolean isLessThanLeftChildElement( T element ) {
            return leftElement() == null ? false : element.compareTo(leftElement()) < 0;
        }
        // Return true if the parameter element is larger than the right child element; false otherwise
        private boolean isLargerThanRightChildElement( T element ) {
            return rightElement() == null ? false : element.compareTo(rightElement()) > 0;
        }

    }

    private AVLTree() { }
    // Static factory method which returns a new AVL tree
    public static <T extends Comparable<T>> AVLTree<T> newInstance() {
        return new AVLTree<T>();
    }

    // Returns true if we have an element in our binary search tree; false otherwise
    public boolean isEmpty( ) { return rootNode == null; }

    // Return the tree height
    public int getHeight ( ) {
        return rootNode == null ? 0 : getHeight ( rootNode );
    }

    // Return the height of the input node
    private int getHeight ( AVLNode<T> node ) {
        // Recursively add 1 to the largest child element height to compute our the current node height
        return node == null ? 0 : 1 + Math.max (
                getHeight ( node.leftChild )
                , getHeight ( node.rightChild )
        );
    }

    // Return the balance factor of the input node. The balance factor is the height difference between the left
    // subtree and right subtree of the current node - if the left subtree height is larger than our right subtree
    // height, a negative factor is returned ; an inverse positive factor is returned if the right subtree height
    // exceeds the left subtree height
    private int getBalanceFactor ( AVLNode<T> node ) {
        if (node == null) return 0;
        return getHeight ( node.leftChild ) - getHeight(node.rightChild);
    }

    // Return whether the input parameter node is balanced or not. A node is balanced if the difference between its
    // left and right subtree heights is not greater than 1.
    private boolean isBalanced ( AVLNode <T> node ) {
        // If the node is null, it is balanced, so return true
        if (node == null) return true;
        // Calculate the balance factor by comparing the left and right child nodes
        int balanceFactor = getBalanceFactor(node);
        // Return whether the balance factor is off by more than one - if it isn't, our node is balanced
        return Math.abs(balanceFactor) <= 1;
    }

    // Insert element of type T into our AVL tree
    public void insert ( T element ) {
        // If the element is null, we simply return
        if (element == null) return;
        // Transverse down the root element and insert the new element node into an appropriate position
        rootNode = insertNewNode(rootNode, element);
    }

    // Insert a new element node rooted under the input node by transversing down the input node recursively until
    // a free insertion spot is found. The balance factor of each subtree is calculated after the insertion, and if
    // any subree is unbalanced, it is re-balanced by performing the appropriate rotations.
    private AVLNode<T> insertNewNode (AVLNode<T> node, T element) {
        // If our transversed node is null, we found a free spot; we insert the new node element and return
        if (node == null) {
            return new AVLNode<T>(element);
            // If the element to insert is less than the current node element, continue transversing down the
            // left child node until a free spot is found
        } else if (element.compareTo(node.element) < 0) {
            node.leftChild = insertNewNode(node.leftChild, element);
            // If the element to insert is larger than the current node element, continue transversing down the
            // right child node until a free spot is found
        } else if (element.compareTo(node.element) > 0) {
            node.rightChild = insertNewNode(node.rightChild, element);
        }
        // Check if our node is still balanced, and if it is, simply return the node
        if (isBalanced(node)) return node;
        // This means that our node is out of balance, so we balance it and return the resulting node.
        else return balance ( node );
    }

    // We right rotate the input node by setting its left child node as its parent node, and re-assigning the left
    // child node right subtree as the new left subtree of our new re-balanced node
    private AVLNode<T> rightRotate ( AVLNode<T> node ) {
        // Fetch the left child of the current node
        AVLNode<T> leftChild = node.leftChild;
        // Fetch the right child of the left node
        AVLNode<T> leftRightChild = leftChild.rightChild;
        // Set the left child to be the new parent element of our node
        leftChild.rightChild = node;
        // Set the left subtree of our re-balanced node to be the old right subtree of our new parent node
        node.leftChild = leftRightChild;
        // Return the old left child node, since it is our new parent node
        return leftChild;
    }

    // Left rotate the input node by setting its right child node as its new parent node, and re-assiging the right
    // child node left subtree as the new right subtree of our new re-balanced node
    private AVLNode<T> leftRotate ( AVLNode<T> node ) {
        // Fetch the right child of our current node
        AVLNode<T> rightChild = node.rightChild;
        // Fetch the left child of our right child node
        AVLNode<T> rightLeftChild = rightChild.leftChild;
        // Set the right child node to be the parent node of our current element
        rightChild.leftChild = node;
        // Set the right subtree of our new parent node to be the right subtree of our new re-balanced node
        node.rightChild = rightLeftChild;
        // Return the old right child node, since it is our new root node
        return rightChild;
    }

    // Delete the node containing the element parameter from our AVL tree
    public void delete (T element) {
        // If the element is null, simply return, as we do not permit null elements in the AVL tree
        if (element == null) return;
        // Delete the element node by transversing down the root node of our tree, finding the node element, and
        // removing it from the tree
        rootNode = deleteNode(rootNode, element);
    }

    // Delete the node containing the parameter element from our AVL tree by recursively tranversing down the tree,
    // deleting the node, and then checking if each subtree is balanced to see if we need to left/right shift
    // the nodes in order to re-balance our tree
    private AVLNode<T> deleteNode (AVLNode<T> node, T element) {
        // If the input node is null, no action necessary; return null
        if (node == null) {
            return null;
            // If the element to remove is less than the current node element, it must be in the left subtree, so
            // we recursively continue searching for it by transversing down the left child node
        } else if (element.compareTo(node.element) < 0) {
            node.leftChild = deleteNode(node.leftChild, element);
            // If the element to remove is larger than the current node element, it must be in the right subtree, so
            // we recursively continue searching for it by transversing down the right child node
        } else if (element.compareTo(node.element) > 0) {
            node.rightChild = deleteNode(node.rightChild, element);
            // Otherwise, we found our element, so we remove it
        } else {
            // If the deletion node has both children
            if (node.hasLeftChild() && node.hasRightChild()) {
                // The node which will take place of the current node will be the minimum element rooted in our left
                // subtree, so we fetch it, set our new node element to this value, and we remove the successor node
                AVLNode<T> successorNode = getMinNode(node.rightChild);
                node.element = successorNode.element;
                node.rightChild = deleteNode ( node.rightChild, successorNode.element );
                // If our node only has a right child, we simply copy it to the current node location
            } else if (node.hasRightChild()) {
                node = node.rightChild;
                // Our node must only have a left child, or no child at all, so we simply set the node to reference
                // the left child node, which will either be null or our left child element
            } else {
                node = node.leftChild;
            }
        }
        // Check if the node is balanced, and if it is, we simply return it
        if (isBalanced(node)) return node;
        // Our node is unbalanced, so balance it by performing the proper rotations and return the new balanced node
        else return balance ( node );
    }

    // Balance the input node by calculating the balance factor and performing the proper rotations
    private AVLNode<T> balance ( AVLNode<T> node ) {
        // Get the balance factor, which will be larger than one if our left subtree is larger than our right subtree,
        // and less than one if the right subtree is larger than our left subtree
        int balance = getBalanceFactor(node);
        // If our left subtree is larger than the right
        if (balance > 1) {
            // If the left child node has a left child, we simply right rotate the current element
            if (getBalanceFactor(node.leftChild) >= 0) {
                return rightRotate(node);
            } else {
                // If the left child node has a right child, we first left rotate the left child node and then we
                // rotate the current node
                node.leftChild = leftRotate(node.leftChild);
                return rightRotate(node);
            }
        }
        // If our right subtree is larger than our left
        if (balance < -1) {
            // If our right child node has a right child node element, we perform a simple left rotation on our node
            if (getBalanceFactor(node.rightChild) <= 0) {
                return leftRotate(node);
            } else {
                // Otherwise, our right child node has a left child, so we first do a right rotation on our right
                // child node prior to performing a left rotation on the current node
                node.rightChild = rightRotate(node.rightChild);
                return leftRotate(node);
            }
        }
        return node;
    }

    // Get the minimum AVL tree element and return it
    public T getMin ( ) {
        // Find the minimum element node starting from the root element
        AVLNode<T> minNode = getMinNode( rootNode );
        // If the minimum node exists, return its associated element
        return minNode == null  ? null : minNode.element;
    }

    // Get the minimum element node
    private AVLNode<T> getMinNode ( AVLNode<T> node ) {
        // If the node is null, return null to indicate that no minimum element value was found
        if( node == null ) return null;
        // Continue transversing down all left child nodes until the left-most child is found, and return the element
        // value at this node since it contains the minimum element.
        while( node.leftChild != null ) node = node.leftChild;
        return node;
    }

    // Get the maximum AVL tree element value and return it
    public T getMax ( ) {
        // Find the maximum element node starting from the root element and return it
        return getMax(rootNode);
    }

    // Get the maximum element value rooted at the node
    private T getMax ( AVLNode<T> node ) {
        // If the node is null, return null to indicate that no maximum element value was found
        if( node == null ) return null;
        // Continue transversing down all right child nodes until the right-most child is found, and return the element
        // value at this node since it contains the maximum element
        while( node.rightChild != null ) node = node.rightChild;
        return node.element;
    }

    // Get an in-order list of our AVL tree elements
    public List<T> getInOrderElementList() {
        // New array list used to store our elements
        List<T> list = new ArrayList<T>();
        // Tranverse down the root node and retrieve a list of elements contained in each node
        getInOrderElementList( rootNode, list );
        // Return the in-order list
        return list;
    }

    // Get an in-order list of elements rooted at the input parameter node
    private void getInOrderElementList( AVLNode<T> node, List<T> resultList) {
        // If our input node is not null, we recursively transverse down the left subtrees first, followed by the
        // current node element, then the right subtree in order to add all elements in proper order to our list
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

    // Returns a string representation of all in-order elements in our AVL tree
    @Override
    public String toString() {
        if (isEmpty()) return "";
        // String builder used to build a delimited list of AVL tree elements
        StringBuilder stringBuilder = new StringBuilder();
        // Get the in order element list
        List<T> elementList = getInOrderElementList();
        // Delimiter used to separate the AVL tree elements
        String delimiterString = ", ";
        // Iterate through each AVL tree element appending it to our builder
        for (T element : elementList) {
            stringBuilder.append(element).append(delimiterString);
        }
        // Return the delimited string of elements, with the last delimiter string removed
        return stringBuilder.toString().substring(0, stringBuilder.lastIndexOf(delimiterString));

    }

    // Return whether our AVL tree maintains all BST properties 1. All nodes on the left subtree
    // have element values that are less than the current node element value; 2. All nodes on the right subtree have
    // element values that are larger than the current node element value; 3. All left and right subtree nodes also
    // are BSTs and maintain the above 2 properties.
    private boolean isBinarySearchTree ( AVLNode<T> node ) {
        if (node == null) return true;
        if (node.hasLeftChild() && node.leftElement().compareTo(node.element) > 0) return false;
        if (node.hasRightChild() && node.rightElement().compareTo(node.element) < 0) return false;
        return (isBinarySearchTree(node.leftChild) && isBinarySearchTree(node.rightChild));
    }
}
