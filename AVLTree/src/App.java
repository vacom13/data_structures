import java.util.*;

class Node<T extends Comparable<T>> {
    private T data;
    private Node<T> left, right;
    private int size;
    private int freq;
    private int height;

    Node(T data, Node<T> left, Node<T> right) {
        this.data = data;
        this.left = left;
        this.freq = 1;
        this.right = right;
        this.size = (left != null ? left.size : 0) + (right != null ? right.size : 0) + 1;
        this.height = 1 + Math.max(left != null ? left.height : 0, right != null ? right.height : 0);
    }

    public void updateAttributes() {
        this.height = 1 + Math.max(left != null ? left.height : 0, right != null ? right.height : 0);
        this.size = (left != null ? left.size : 0) + (right != null ? right.size : 0) + freq;
    }

    public T getData() {
        return this.data;
    }

    public int getHeight() {
        return this.height;
    }

    public int getSize() {
        return this.size;
    }

    public void increaseFreq() {
        this.freq++;
    }

    public Node<T> getLeftChild() {
        return this.left;
    }

    public Node<T> getRightChild() {
        return this.right;
    }

    public void setLeftChild(Node<T> node) {
        this.left = node;
    }

    public void setRightChild(Node<T> node) {
        this.right = node;
    }

    public String toString() {
        return data.toString();
    }

}

class AVLTree<T extends Comparable<T>> {

    private Node<T> root;
    private int size;
    private Comparator<? super T> comparator;

    AVLTree(T[] dataArray) {
        this.comparator = null;
        if(dataArray == null){
            this.root = null;
            return;
        }
        for (T data : dataArray) {
            addNode(data);
            // levelOrderTraversalOfTree();
        }
    }

    AVLTree(T[] dataArray, Comparator<? super T> comparator) {
        this.comparator = comparator;
        if(dataArray == null){
            this.root = null;
            return;
        }
        for (T data : dataArray) {
            addNode(data);
            levelOrderTraversalOfTree();
        }
    }


    public T getData() {
        return root.getData();
    }

    public void addNode(T data) {
        // Recursively add, backtrack and check and perform rotations.
        root = addNodeInternal(data, root);
        size = root.getSize();
        levelOrderTraversalOfTree();
    }

    public T getNthKey(int nth) {
        if (nth <= 0 || nth > size) {
            return null;
        }
        return getNthNodeData(nth, root);

    }

    private T getNthNodeData(int nth, Node<T> current) {
        int leftChildSize = getNodeSize(current.getLeftChild());
        if (nth <= leftChildSize) {
            return getNthNodeData(nth, current.getLeftChild());
        } else if (nth <= leftChildSize + current.getSize()) {
            return current.getData();
        } else {
            return getNthNodeData(nth - leftChildSize - current.getSize(), current.getRightChild());
        }
    }

    public int getIdx(T data) {
        return getDataIdx(data, root);
    }

    private int getDataIdx(T data, Node<T> current) {
        if (current == null) {
            return -1;
        }
        int compareResult;
        T currentData = current.getData();
        if(this.comparator != null){
            compareResult = comparator.compare(data, currentData);
        }
        else{
            compareResult = data.compareTo(current.getData());
        }
        if (compareResult == 0) {
            return current.getSize() - getNodeSize(current.getRightChild());
        } else if (compareResult < 0) {
            return getDataIdx(data, current.getLeftChild());
        } else {
            return current.getSize() - getNodeSize(current.getRightChild()) + getDataIdx(data, current.getRightChild());
        }
    }

    private Node<T> addNodeInternal(T nodeData, Node<T> current) {
        if (current == null) {
            return new Node<T>(nodeData, null, null);
        }
        T currentData = current.getData();
        int compareResult;
        if(this.comparator != null){
            compareResult = comparator.compare(nodeData, currentData);
        }
        else{
            compareResult = nodeData.compareTo(currentData);
        }
        if (compareResult < 0) {
            Node<T> child = addNodeInternal(nodeData, current.getLeftChild());
            current.setLeftChild(child);
            current.updateAttributes();
        } else if (compareResult > 0) {
            Node<T> child = addNodeInternal(nodeData, current.getRightChild());
            current.setRightChild(child);
            current.updateAttributes();
        } else {
            current.increaseFreq();
            current.updateAttributes();
            return current;
        }
        int leftChildHeight = getNodeHeight(current.getLeftChild());
        int rightChildHeight = getNodeHeight(current.getRightChild());
        int heightDiff = leftChildHeight - rightChildHeight;
        if (heightDiff == 2) {
            int leftChildOfLeftChildHeight = getNodeHeight(current.getLeftChild().getLeftChild());
            int rightChildOfLeftChildHeight = getNodeHeight(current.getLeftChild().getRightChild());
            int childHeightDiff = leftChildOfLeftChildHeight - rightChildOfLeftChildHeight;
            if (childHeightDiff == 1) {
                // Only make right rotation on current.
                return rightRotation(current);
            } else if (childHeightDiff == -1) {
                // Make left Rotation on child and then right rotation on current
                current.setLeftChild(leftRotation(current.getLeftChild()));
                current.updateAttributes();
                return rightRotation(current);
            }
        } else if (heightDiff == -2) {
            int leftChildOfRightChildHeight = getNodeHeight(current.getRightChild().getLeftChild());
            int rightChildOfRightChildHeight = getNodeHeight(current.getRightChild().getRightChild());
            int childHeightDiff = leftChildOfRightChildHeight - rightChildOfRightChildHeight;
            if (childHeightDiff == 1) {
                // Make right Rotation on child and then left rotation on current
                current.setRightChild(rightRotation(current.getRightChild()));
                current.updateAttributes();
                return leftRotation(current);
            } else if (childHeightDiff == -1) {
                // Only make left rotation
                return leftRotation(current);
            }
        }

        return current;
    }

    private int getNodeHeight(Node<T> node) {
        if (node == null)
            return 0;
        return node.getHeight();
    }

    private int getNodeSize(Node<T> node) {
        if (node == null)
            return 0;
        return node.getSize();
    }

    private Node<T> leftRotation(Node<T> node) {
        // Return the new root. Also recalculate everything?
        Node<T> rightChild = node.getRightChild();
        Node<T> leftChildOfRightChild = rightChild.getLeftChild(); // This will exist for sure
        node.setRightChild(leftChildOfRightChild);
        rightChild.setLeftChild(node);
        node.updateAttributes();
        rightChild.updateAttributes();
        return rightChild;

    }

    private Node<T> rightRotation(Node<T> node) {
        // Return the new root.
        Node<T> leftChild = node.getLeftChild();
        Node<T> rightChildOfLeftChild = leftChild.getRightChild(); // This will exist for sure
        node.setLeftChild(rightChildOfLeftChild);
        leftChild.setRightChild(node);
        node.updateAttributes();
        leftChild.updateAttributes();
        return leftChild;
    }

    private void levelOrderTraversalOfTree() {
        Queue<Node<T>> queue = new LinkedList<Node<T>>();
        queue.add(root);
        System.out.println("Level Order Traversal after addition: ");
        while (queue.size() > 0) {
            Node<T> element = queue.remove();
            System.out.print(element + " ");
            if (element != null) {
                queue.add(element.getLeftChild());
                queue.add(element.getRightChild());
            }
        }
        System.out.println();
        return;

    }

    public int getSize() {
        return this.size;
    }
}

public class App {
    // Left, Left = 2,1
    // Left, Right = 2, -1
    // Right, Right = -2,-1
    // Right, Left = -2, 1

    // Bad practice to direcly manipulate variables. Should use getters and setters

    public static void main(String[] args) throws Exception {
        Integer[] input = { 21, 26, 30, 9, 4, 14, 28, 18, 15, 10, 2, 3, 7 };
        AVLTree<Integer> bst = new AVLTree<>(input);
        AVLTree<Integer> bst2 = new AVLTree<>(input, (Integer a, Integer b) -> b - a);

    }
}