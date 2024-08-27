package data_structures;

public class Node<T> {
    T value;
    Node<T>[] children;
    int childIndex = 0;

    public Node(T value, int numChildren) {
        this.value = value;
        this.children = new Node[numChildren];
    }

    public Node(T value, Node<T>[] children) {
        this.value = value;
        this.children = children;
        this.childIndex = children.length; 
    }

    public T getValue() {
        return this.value;
    }

    public Node<T>[] getChildren() {
        return this.children;
    }

    public int addChild(Node<T> newChild) {
        if (childIndex<children.length) {
            children[childIndex] = newChild;
            ++childIndex;
            return 0;
        }
        return -1;  // return negative 1 if new child couldn't be added
    }

    public String toString() {
        String out = "";
        for (Node<T> child: children) {
            if (child!=null) {
                out += "\t Child Value: " + child.getValue() + "\n";
            }
        }
        return "Value: " + value + "\n Children: \n" + out;
    }
}
