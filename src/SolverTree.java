import java.util.Arrays;

import data_structures.Node;
import data_structures.NodeTree;

// TODO make another implementation of tree that uses an underlying array instead
public class SolverTree {
    NodeTree<ClueInfo> tree;

    public SolverTree() {
        Node<ClueInfo> nullHead = new Node<>(null, 3); // should have 3 children if 3 players, otherwise more TODO
        tree = new NodeTree<ClueInfo>(nullHead);
    }

    public String toString() {
        String out = "[";
        Node<ClueInfo>[] info = tree.getNodesBFS();
        for (Node<ClueInfo> n: info) {
            out += n.getValue() + ", ";
        }
        return out.substring(0, out.length()-2) + "]";
    }
    // TODO document
    private void growNullInfo(ClueInfo info) { // makes 3 children of info, as we know they do not have ANY of the three cards
        for (Node<ClueInfo> leaf: tree.getLeaves()) { // grow 3 children (TODO won't be 3 when generalized)  
            for (ClueCard card: info.guess().getCardSet()) { // put each card possibility in child\
                // add child to leaf
                ClueInfo newInfo = info.copyWithNewCard(card, false); // new card as possibility t
                Node<ClueInfo> newChildNode = new Node<ClueInfo>(newInfo, 3); // TODO change numChildren here from other than just 3
                leaf.addChild(newChildNode); // new ClueInfo with a given 
                
                // move down to new leaf (leaf's child) to build next info node
                leaf = leaf.getChildren()[0]; // we know leaf has only one child (just created)
            }
        }
    }   

    private void growRevealedInfo(ClueInfo info) {
        // give node children for each possibility of card
        for (Node<ClueInfo> node: tree.getLeaves()) {
            // get possibilities for each part of the guess
            for (ClueCard potentialCard: info.guess().getCardSet()) {
                // TODO could make new class that is not clue info to better capture the actually needed information within the tree?
                ClueInfo newPossibility = info.copyWithNewCard(potentialCard);
                node.addChild(new Node<ClueInfo>(newPossibility, 3));
            }
        }
    }

    // TODO add case where you are the player guessing and get to see all of the info yourself.

    /*
     * Given a piece of information, grows tree of possibilities accordingly 
     */
    public void grow(ClueInfo info) {
        if (info.card()==null) { // if player didn't reveal card, add 3 children to leaves representing this
            growNullInfo(info);
        }
        else { // otherwise, each node has one child giving information. // TODO need to consider if this players gets to see this info! (Based on who is guessing)
            System.out.println(Arrays.toString(tree.getLeaves()));
            for (Node<ClueInfo> node: tree.getLeaves()) {
                node.addChild(new Node<ClueInfo>(info, 3));
            }
        }
    }

    /*
     * Determines if all the info needed for a guess has been gathered.
     * This is done by considering all paths down the tree (assuming it has been properly prunes)
     * and if they all result in only 1 three card possibility for the manila envelope, we are done.
     * Otherwise, there are still multiple possibilities. 
     * Returns null if no answer can be determined yet, otherwise, an array of the answer
     */
    public ClueCard[] getAnswer() {
        // TODO STUB
        return null;
    }

    /*
     * Prunes out the possibilites that result in contradictions and cannot occur
     */
    public void prune() {
        // TODO STUB
    }

    /*
     * Given an array of info gathered throughout the game, builds a tree of possibilities for each player
     */
    public void build(ClueInfo[] totalInfo) {
        for (ClueInfo info: totalInfo) {
            grow(info);
        }
    }

    public static void main(String[] args) {
        // TODO add tests here (especially for prune) 
    }
}
