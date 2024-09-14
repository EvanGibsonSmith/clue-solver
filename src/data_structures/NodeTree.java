package data_structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;

public class NodeTree<T> {
    Node<T> head;
    ArrayList<Node<T>> leaves;

    // TODO document
    public NodeTree(Node<T> head) {
        this.head = head;
        leaves = new ArrayList<Node<T>>(); // TODO not used right now, could save time on getting leaves by storing within structure rather that recalculating each time
        leaves.add(head);
    }

    public Node<T> getHead() {
        return head;
    }

    /*
     * Clear all children, leaving only head.
     */
    public void clear() {
        this.getHead().clearChildren();
    }

    public Node<T>[] getLeaves() { // TODO make not recalculate everytime, but update leaves field when tree is grown?
        Queue<Node<T>> todoQueue = new LinkedList<>();
        todoQueue.add(head);
        ArrayList<Node<T>> out = new ArrayList<>();
        while (!todoQueue.isEmpty()) { // traverse and add node when removed from todo
            Node<T> next = todoQueue.poll();
            for (Node<T> child: next.getChildren()) {
                if (child!=null) {
                    todoQueue.add(child);
                }
            }
            if (!next.hasChildren()) { // if no chlid added, then leaf
                out.add(next);
            }
        }
        return out.toArray(new Node[0]);
    }

    // TODO document
    public Node<T>[] getNodesBFS() {
        Queue<Node<T>> todoQueue = new LinkedList<>();
        todoQueue.add(head);
        Set<Node<T>> visited = new HashSet<>(); // since tree can rejoin layers, need to keep track of visited
        ArrayList<Node<T>> out = new ArrayList<>();
        while (!todoQueue.isEmpty()) { // traverse and add node when removed from todo
            Node<T> next = todoQueue.poll();
            for (Node<T> child: next.getChildren()) {
                if (child!=null && !visited.contains(child)) {
                    todoQueue.add(child);
                    visited.add(child);
                }
            }
            out.add(next);
        }
        return out.toArray(new Node[0]);
    }

    public Node<T>[] getNodesLayer(int layer) {
        Queue<Node<T>> todoQueue = new LinkedList<>();
        todoQueue.add(head);
        Set<Node<T>> visited = new HashSet<>(); // since tree can rejoin layers, need to keep track of visited
        int currLayer = 0;
        while (!todoQueue.isEmpty() & layer!=currLayer) { // traverse and add node when removed from todo
            int layerSize = todoQueue.size(); // size this layer to work through in queue
            for (int layerIdx=0; layerIdx<layerSize; ++layerIdx) { // traverse layer
                Node<T> next = todoQueue.poll();
                for (Node<T> child: next.getChildren()) { // add all (non null) children
                    if (child!=null  && !visited.contains(child)) {
                        todoQueue.add(child);
                        visited.add(child);
                    }
                }
            }
            ++currLayer;
        }
        // now the queue contains just the layer we want
        Node<T>[] out = todoQueue.toArray(new Node[0]);
        return out;
    }

    // TODO document
    public Node<T>[] getNodesDFS() {
        Stack<Node<T>> todoStack = new Stack<>();
        todoStack.add(head);
        Set<Node<T>> visited = new HashSet<>(); // since tree can rejoin layers, need to keep track of visited
        ArrayList<Node<T>> out = new ArrayList<>();
        while (!todoStack.isEmpty()) { // traverse and add node when removed from todo
            Node<T> next = todoStack.pop();
            for (Node<T> child: next.getChildren()) {
                if (child!=null && !visited.contains(child)) {
                    todoStack.add(child);
                }
            }
            out.add(next);
        }
        return out.toArray(new Node[0]);
    }

    public static void main(String[] args) {
        // TODO add a test here
        Node<Integer> headNode = new Node<Integer>(0, 3);

        Node<Integer> child1 = new Node<Integer>(1, 3);
        Node<Integer> child2 = new Node<Integer>(2, 3);

        headNode.addChild(child1);
        headNode.addChild(child2);

        Node<Integer> node4 = new Node<Integer>(3, 3);
        child1.addChild(node4);

        node4.addChild(new Node<Integer>(4, 3));

        NodeTree<Integer> tree = new NodeTree<>(headNode);
        for (Node<Integer> n: tree.getNodesBFS()) {
            System.out.println(n);
        }

    }
}
