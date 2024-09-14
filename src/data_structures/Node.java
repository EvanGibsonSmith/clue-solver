package data_structures;

public class Node<T> {
    T value;
    Node<T>[] children;
    int childIndex = 0;
    int childCapacity;

    public Node(T value, int childCapacity) {
        this.value = value;
        this.children = new Node[childCapacity];
        this.childCapacity = childCapacity;
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

    public int numChildren() {
        return childIndex;
    }

    public int childCapacity() {
        return childCapacity;
    }

    public boolean hasChildren() {
        return (childIndex!=0);
    }

    public int addChild(Node<T> newChild) {
        if (childIndex<children.length) {
            children[childIndex] = newChild;
            ++childIndex;
            return 0;
        }
        return -1;  // return negative 1 if new child couldn't be added
    }

    public int removeChild(int idx) {
        children[idx] = null; 
        if ((childIndex-1)!=0) { // fill gap created in children array
            children[idx] = children[(childIndex-1)]; // move final child in array to new gap
            children[(childIndex-1)] = null; // remove final child from old location
        }
        --childIndex;
        return -1;  // return negative 1 if new child couldn't be added
    }

    /*
     * Removes connections to all children
     */
    public void clearChildren() {
        childIndex = 0; // reset index 
        children = new Node[childCapacity];
    }

    @Override
    public String toString() {
        if (value==null) {
            return null;
        }
        return value.toString();
    }

    public String verboseToString() {
        String out = "";
        for (Node<T> child: children) {
            if (child!=null) {
                out += "\t Child Value: " + child.getValue() + "\n";
            }
        }
        return "Value: " + value + "\n Children: \n" + out;
    }

    public static void main(String[] args) {
        // TODO add tests for adding and removing children that test underlying array structure
    }
}
