import data_structures.Node;
import data_structures.NodeTree;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

// TODO make another implementation of tree that uses an underlying array instead
public class SolverTree {
    NodeTree<ClueInfo> tree; // TODO MAKE THIS STORED AS A SET IN HERE!!!!!
    // NOTE: I think technically speaking, the player we are from the perspective of is not needed if info is properly used (not giving too much info)
    CluePlayer player; // which player we are solving from the perspective of 
    Node<ClueInfo> bottom; // tree grows from this node


    public SolverTree(CluePlayer player) {
        Node<ClueInfo> nullHead = new Node<>(null, 3); // should have 3 children if 3 players, otherwise more TODO
        tree = new NodeTree<ClueInfo>(nullHead);
        this.player = player;
        this.bottom = tree.getHead();
    }

    public String toString() {
        String out = "[";
        Node<ClueInfo>[] info = tree.getNodesBFS();
        for (Node<ClueInfo> n: info) {
            out += n.getValue() + ", ";
        }
        return out.substring(0, out.length()-2) + "]";
    }

    public NodeTree<ClueInfo> getTree() {
        return tree;
    }

    /*
     * Clears tree of all children, leaving only head.
     */
    public void clear() {
        tree.clear();
    }

    /* 
     * When a player passes on a guess, we know they do not have any of the three cards in the guess.
     *  This covers that possibility
    */
    private void growNullInfo(ClueInfo info) { // makes 3 (vertical) children of info, as we know they do not have ANY of the three cards
        for (ClueCard card: info.guess().getCardSet()) { // put each card possibility in child\
            // add child to leaf
            ClueInfo newInfo = info.copyWithNewCard(card, false); // new card as possibility t
            Node<ClueInfo> newChildNode = new Node<ClueInfo>(newInfo, 3); 
            bottom.addChild(newChildNode); // new ClueInfo with a given 
            
            // move down to new leaf (leaf's child) to build next info node
            bottom = bottom.getChildren()[0]; // we know leaf has only one child (just created)
        }
        // bottom is now correct node in tree
    }   

    /*
     * For case in which something is revealed to another player, but we do not know what.
     * When another player reveals a card, we know they must have one of those three cards from the guess.
     * This method branches into those possibilities.
     */
    private void growRevealedInfo(ClueInfo info) {
        // give bottom node children for each possibility of card
        for (ClueCard potentialCard: info.guess().getCardSet()) {
            ClueInfo newPossibility = info.copyWithNewCard(potentialCard);
            bottom.addChild(new Node<ClueInfo>(newPossibility, 3));
        }
        // add null node to make branches rejoin
        Node<ClueInfo> nullJoinNode = new Node<ClueInfo>(null, 3); // 
        for (Node<ClueInfo> node: bottom.getChildren()) { // joins horizontally split node to make main trunk
            node.addChild(nullJoinNode);
        }
        bottom = nullJoinNode; // update bottom node
    }

    /*
     * Given a piece of information, grows tree of possibilities accordingly 
     */
    public void grow(ClueInfo info) {
        // 3 cases: We saw card revealed, we didn't see card revealed, no card was revealed (doesn't matter if our guess or another player)
        if (info.guessingPlayer()==player && info.hasCard()) { // if this player made guess and card was revealed, we know card
            if (info.card()==null) { // this should not happen, if info is correct
                throw new RuntimeException("If player made guess and was shown card, info object should contain that card.");
            } 
            // in this case, add a single child representing this new information to each leaf (same info object is ok since they aren't modified) 
            bottom.addChild(new Node<ClueInfo>(info, 3));
        }
        else if (info.guessingPlayer()!=player && info.hasCard()) {
            growRevealedInfo(info);
        }
        else if (!info.hasCard()) { // if no card revealed, we know what cards they don't have (note it does not matter which player caused this)
            if (info.card()!=null) { // since no card reveals (hadCard=false), card should be null
                throw new RuntimeException("If player made guess and was not shown card, info object should have null card.");
            } 
            growNullInfo(info); // grows info for cards opponents don't have
        }
        else {
            throw new RuntimeException("Conditions of info did not satify any condition to grow SolverTree.");
        }
    }

    /*
     * Determines if all the info needed for a guess has been gathered.
     * This is done by considering all paths down the tree (assuming it has been properly pruned)
     * and if they all result in only 1 three card possibility for the manila envelope, we are done.
     * Otherwise, there are still multiple possibilities. 
     * Returns null if no answer can be determined yet, otherwise, an array of the answer
     */
    public ClueCard[] getAnswer() {
        // TODO STUB
        return null;
    }

    /*
     * Gets a HashSet of all known information (nodes with no children).
     * This function exploits the fact that nodes with 3 children will 
     * reconnect a null node afterward.
     */
    // TODO make test for this method, and should combine with knownCardInformation
    @Deprecated
    public Set<Node<ClueInfo>> unknownCardInformation() {
        Node<ClueInfo> node = tree.getHead();
        HashSet<Node<ClueInfo>> unknownInfo = new HashSet<>();

        while (node.hasChildren()) {
            if (node.numChildren()!=1) { // add unknown only if multiple children
                for (int childIdx=0; childIdx<node.getChildren().length; ++childIdx) {
                    Node<ClueInfo> childNode = node.getChildren()[childIdx];
                    if (childNode==null) { // if no more children break
                        break;
                    }
                    unknownInfo.add(childNode); // TODO for efficeiency could not iterate through structure twice later to prune, but prune right here based on the three conditions
                }
            }

            node = node.getChildren()[0]; // if single child, keep searching for multi child, if multichild, will reconvene.
        }
        return unknownInfo;
    }

    
    /*
     * Gets a HashSet of all known information (nodes with no children).
     * This function exploits the fact that nodes with 3 children will 
     * reconnect a null node afterward.
     */
    // TODO make test for this method
    private Set<ClueInfo> knownCardInformation() {
        Node<ClueInfo> node = tree.getHead();
        HashSet<ClueInfo> knownInfo = new HashSet<>();
        knownInfo.add(node.getValue()); // add head node, since it has no parents
        while (node.hasChildren()) {
            if (node.numChildren()==1 & node.getChildren()[0].getValue()!=null) { // add info if only one non null child
                knownInfo.add(node.getChildren()[0].getValue()); // add ONLY child to knownInfo since it has no siblings
            }
            // move down tree. If node has multiple chlidren, they will reconvene, so arbitrarily choosing first is ok
            node = node.getChildren()[0]; 
        }
        return knownInfo;
    }

    /*
     * Prunes out the possibilites that result in contradictions and cannot occur.
     * A prune cannot occur on a layer with a single child in proper play since that child should be the only
     * possibility.
     *  Pruning can occur on 3 conditions, all on multiple child layers, or unknown nodes.
     *      1. An unknown node reveals the same card another player is known to have.
     *      2. An unknown node asserts that a player has a card that they are known to not have.
     *          NOTE: An unknown node cannot assert that a player does not have a card, since unknown nodes
     *                occur when the tree branches, which only occurs when a card is revealed.
     *      3. An unknown node, if a player had it, would cause them to have too many cards in hand including known nodes.
     */
    /*@Deprecated
    public void pruneOLD() { // TODO need to actually keep track of the node
        // TODO STUB
        // collect information about tree (using BFS, although tree is somewhat degenerate)
        Set<Node<ClueInfo>> knownInfoSet = knownCardInformation(); // TODO getting knownCardInformation and unknownCardInformation currently takes 2 passed through the tree, which is inefficent
        Set<Node<ClueInfo>> unknownInfoSet = unknownCardInformation();
        Map<CluePlayer, Integer> numKnownCardsPlayer = numKnownPlayerCards(knownInfoSet); // get number of cards that are known for each player
        Set<CluePlayer> seenPlayers = numKnownCardsPlayer.keySet(); // player that have been seen in info tree

        // Given tree information, prune from unknown nodes
        for (ClueInfo info: unknownInfoSet) {
            // 1. in docstring

            // 2. in docstring
            if (knownInfoSet.contains(info.copyWithOppositeHasCard())) {
                
            }

            // 3. in docstring
        }
    }*/

    /*
     * TODO document
     */
    public Map<CluePlayer, Integer> numKnownPlayerCards(Set<ClueInfo> knownInfoSet) {
        Map<CluePlayer, Integer> numKnownCardsPlayer = new HashMap<>();
        for (ClueInfo node: knownInfoSet) {
            CluePlayer revPlayer = node.revealingPlayer();
            if (!numKnownCardsPlayer.containsKey(revPlayer)) { // if player not seen, set to 0 cards
                numKnownCardsPlayer.put(revPlayer, 0);
            }
            // add card to revealing player since it is known
            numKnownCardsPlayer.put(revPlayer, numKnownCardsPlayer.get(revPlayer)+1);
        }
        return numKnownCardsPlayer;
    }

    /*
     * Removes the nodes passed in nodesSet from the tree by passing through tree.
     */
    private void removeNodes(Set<Node<ClueInfo>> nodesSet) {
        // TODO complete me
    }

    // TODO Could make a marginally better pruning algorithm that looks through tree and indentifies all possible 
    // disqualifying nodes, and then searches tree once more for those disqualifying nodes
    // (Also, since the order of information over time throughout the game isn't important, it may be better to store as a set, and maybe a bit faster)
    /*
     * Finds and removes nodes with ClueInfo that would disqualify 
     * unknown nodes with contradictory ClueInfo if present in tree, based on 3 pruning conditions:
     *      1. An unknown node reveals the same card another player is known to have.
     *      2. An unknown node asserts that a player has a card that a known node states they do not have.
     *          NOTE: An unknown node cannot assert that a player does not have a card, since unknown nodes
     *                occur when the tree branches, which only occurs when a card is revealed.
     *      3. An unknown node, if a player had it, would cause them to have too many cards in hand including known nodes.
     */
    public void prune() {
        Node<ClueInfo>[] nodes = tree.getNodesBFS();
        Set<ClueInfo> knownNodesInfo = knownCardInformation();
        Map<CluePlayer, Integer> playerCards = numKnownPlayerCards(knownNodesInfo);
        Set<CluePlayer> seenPlayers = playerCards.keySet();
        Map<ClueInfo, Node<ClueInfo>> nodeMap = new HashMap<>();

        Set<Node<ClueInfo>> nodesToRemove = new HashSet<>(); // stores nodes to remove when passing through tree
        // build map to get node with given clueInfo (No clueInfo should occur twice)
        for (Node<ClueInfo> node: nodes) { // from 1. in docstring
           nodeMap.put(node.getValue(), node);
        }

        // prune on conditions
        boolean anyNodeRemoved = false; // if any card has been 
        for (Node<ClueInfo> node: nodes) {
            ClueInfo cInfo = node.getValue();
            // from 1., 2., and 3., in docstring
            boolean cond1 = false;
            for (CluePlayer cp: seenPlayers) {
                if (knownNodesInfo.contains(cInfo.copyWithNewRevealingPlayer(cp))) {
                    cond1 = true;
                }
            }

            boolean cond2 = knownNodesInfo.contains(cInfo.copyWithOppositeHasCard());
            boolean cond3 = (playerCards.get(cInfo.revealingPlayer()) + 1) > cInfo.revealingPlayer().getMaxHandSize();
            if (cond1 | cond2 | cond3) { 
                nodesToRemove.add(node);
            }
        }

        removeNodes(nodesToRemove); 
    }

    /*
     * Given an array of info gathered throughout the game, builds a tree of possibilities for each player
     */
    public void build(ClueInfo[] totalInfo) {
        tree.clear(); // clear 
        for (ClueInfo info: totalInfo) {
            grow(info);
        }
    }

    public static void main(String[] args) {
        // TODO add tests here (especially for prune) 
    }
}
