package data_structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class NodeTree<T> {
    Node<T> head;

    public NodeTree(Node<T> head) {
        this.head = head;
    }

    // TODO document
    public Node<T>[] getNodesBFS() {
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
            out.add(next);
        }
        return out.toArray(new Node[0]);
    }

    // TODO document
    public Node<T>[] getNodesDFS() {
        Stack<Node<T>> todoStack = new Stack<>();
        todoStack.add(head);
        ArrayList<Node<T>> out = new ArrayList<>();
        while (!todoStack.isEmpty()) { // traverse and add node when removed from todo
            Node<T> next = todoStack.pop();
            for (Node<T> child: next.getChildren()) {
                if (child!=null) {
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
