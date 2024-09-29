package main;

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
    CluePlayer[] players;
    Node<ClueInfo> bottom; // tree grows from this node
    ClueCard[] centerCards = new ClueCard[0]; // keeps track of initially known center cards, by default empty
    Set<ClueGuess> finalGuesses = new HashSet<>(); // final guesses from players, edge case that can be helpful to deduce an answer

    static final String[] defaultRooms = {"conservatory", "dining", "ballroom", "study", "hall", "lounge", "library", "billiard"};
    static final String[] defaultPeople = {"scarlett", "mustard", "white", "green", "peacock", "plum"};
    static final String[] defaultWeapons = {"candlestick", "knife", "lead pipe", "pistol", "rope", "wrench"};
        
    ClueCard[] peopleCards;
    ClueCard[] roomCards;
    ClueCard[] weaponCards;

    private void setupClueCards(String[] peopleStrings, String[] roomStrings, String[] weaponStrings) {
        peopleCards = new ClueCard[peopleStrings.length];
        roomCards = new ClueCard[roomStrings.length];
        weaponCards = new ClueCard[weaponStrings.length];

        for (int personIdx=0; personIdx<peopleStrings.length; ++personIdx) {
            peopleCards[personIdx] = new ClueCard(peopleStrings[personIdx], peopleStrings, roomStrings, weaponStrings);
        }
        for (int roomIdx=0; roomIdx<roomStrings.length; ++roomIdx) {
            roomCards[roomIdx] = new ClueCard(roomStrings[roomIdx], peopleStrings, roomStrings, weaponStrings);
        }
        for (int weaponIdx=0; weaponIdx<weaponStrings.length; ++weaponIdx) {
            weaponCards[weaponIdx] = new ClueCard(weaponStrings[weaponIdx], peopleStrings, roomStrings, weaponStrings);
        }
    }

    public SolverTree(CluePlayer player, CluePlayer[] players) {
        Node<ClueInfo> nullHead = new Node<>(null, 3); 
        tree = new NodeTree<ClueInfo>(nullHead);
        this.player = player;
        this.players = players;
        this.bottom = tree.getHead();

        setupClueCards(defaultPeople, defaultRooms, defaultWeapons);
    }

    public SolverTree(CluePlayer player, CluePlayer[] players, ClueCard[] centerCards) {
        this(player, players);
        this.centerCards = centerCards;
    }

    public SolverTree(CluePlayer player, CluePlayer[] players, ClueCard[] peopleCards, ClueCard[] roomCards, ClueCard[] weaponCards) {
        this(player, players);
        this.peopleCards = peopleCards;
        this.roomCards = roomCards;
        this.weaponCards = weaponCards;
    }
    
    public SolverTree(CluePlayer player, CluePlayer[] players, ClueCard[] centerCards, ClueCard[] peopleCards, ClueCard[] roomCards, ClueCard[] weaponCards) {
        this(player, players, peopleCards, roomCards, weaponCards);
        this.centerCards = centerCards;
    }

    public SolverTree(CluePlayer player, CluePlayer[] players, ClueCard[] centerCards, String[] peopleStrings, String[] roomStrings, String[] weaponStrings) {
        this(player, players, peopleStrings, roomStrings, weaponStrings);
        this.centerCards = centerCards;
    }

    public SolverTree(CluePlayer player, CluePlayer[] players, String[] peopleStrings, String[] roomStrings, String[] weaponStrings) {
        Node<ClueInfo> nullHead = new Node<>(null, 3); 
        tree = new NodeTree<ClueInfo>(nullHead);
        this.player = player;
        this.players = players;
        this.bottom = tree.getHead();

        setupClueCards(peopleStrings, roomStrings, weaponStrings);
    }

    @Override
    public String toString() {
        String out = "[";
        Node<ClueInfo>[] info = tree.getNodesBFS();
        for (Node<ClueInfo> n: info) {
            out += n.getValue() + ", ";
        }
        return out.substring(0, out.length()-2) + "]";
    }

    /*
     * Initialized known cards based on this player. 
     * This includes knowing that player has all cards in their hand, 
     * as well as this player not having all cards not in their hand
     * and any cards in the center of the board given. TODO need to finish the center cards for this
     */
    public void initializeKnown() {
        // initalize known center cards
        for (ClueCard card: centerCards) {
            if (card!=null) {
                // no player's involved, card is known
                grow(new ClueInfo(null, null, null, card, true)); 
            }

            // we know every player does not have this card, so this is info to collect
            for (CluePlayer player: players) {
                // "revealingPlayer" has "revealed" through the center cards they do not have this card
                grow(new ClueInfo(null, null, player, card, false)); 
            }
        }

        // cards this player has
        for (ClueCard card: this.player.getHand().getCards()) {
            // this if shouldn't be needed in game where all hands are full, 
            // but useful for tests or cases where somehow hands aren't evenly filled
            if (card!=null) {
                grow(new ClueInfo(this.player, null, this.player, card, true));
            }
        }

        // cards this player doesn't have
        Set<ClueCard> notPlayerCards = new HashSet<>(); // build set of cards player doesn't have
        notPlayerCards.addAll(Arrays.asList(peopleCards));
        notPlayerCards.addAll(Arrays.asList(roomCards));
        notPlayerCards.addAll(Arrays.asList(weaponCards));
        Set<ClueCard> playerCards = new HashSet<>(Arrays.asList(this.player.getHand().getCards()));
        notPlayerCards.removeAll(playerCards); // make possibliti
        for (ClueCard card: notPlayerCards) {
            grow(new ClueInfo(this.player, null, this.player, card, false));
        }
    }

    public NodeTree<ClueInfo> getTree() {
        return tree;
    }

    /*
     * Clears tree of all children, leaving only head.
     */
    public void clear() {
        tree.clear(); 
        bottom = tree.getHead(); // TODO shouldn't bottom since it is more a part of the tree be handles in NodeTree? I suppose bottom does need to deal with branching but still
    }

    /* 
     * When a player passes on a guess, we know they do not have any of the three cards in the guess.
     *  This covers that possibility
    */
    private void growUnrevealedInfo(ClueInfo info) { // makes 3 (vertical) children of info, as we know they do not have ANY of the three cards
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
     * TODO document
     */
    private void growSeenInfo(ClueInfo info) {
        if (info.card()==null) { // this should not happen, if info is correct
            throw new RuntimeException("If player made guess and was shown card, info object should contain that card.");
        } 
        // in this case, add a single child representing this new information to each leaf (same info object is ok since they aren't modified) 
        bottom.addChild(new Node<ClueInfo>(info, 3));
        bottom = bottom.getChildren()[0];
    }

    private void growInfoDirect(ClueInfo info) {
        // in this case, add a single child representing card this player doesn't have is good
        bottom.addChild(new Node<ClueInfo>(info, 3));
        bottom = bottom.getChildren()[0];
    }

    /*
     * Given a piece of information, grows tree of possibilities accordingly 
     */
    // TODO could break this into growInitialize function as well
    public void grow(ClueInfo info) {
        // 3 cases: We saw card revealed, we didn't see card revealed, no card was revealed (doesn't matter if our guess or another player)
        if (info.guessingPlayer()==player && info.hasCard()) { // if this player made guess and card was revealed, we know card
            growSeenInfo(info);
        }
        else if (info.guessingPlayer()!=player && info.guessingPlayer()!=null && info.hasCard()) {
            growRevealedInfo(info);
        }
        else if (!info.hasCard() && info.guess()!=null) { // if no card revealed, we know what cards they don't have (note it does not matter which player caused this)
            if (info.card()!=null) { // since no card reveals (hadCard=false), card should be null
                throw new RuntimeException("If player made guess and was not shown card, info object should have null card.");
            } 
            growUnrevealedInfo(info); // grows info for cards opponents don't have
        }
        // if clueInfo showing this player doesn't have a card for initialization of this player and center cards
        else if (!info.hasCard() && info.guess()==null) { 
            growInfoDirect(info);
        }   
        // for initalizing center cards, with no guessing player but known card
        else if (info.guessingPlayer()==null && info.hasCard()) { 
            growInfoDirect(info);
        }
        else {
            throw new RuntimeException("Conditions of info did not satify any condition to grow SolverTree.");
        }
    }

    /* 
     * TODO DOCUMENT
     */
    private Set<ClueCard> shownCards(Set<ClueInfo> allInfo) { 
        // TODO stub
        Set<ClueCard> shownCards = new HashSet<>();
        for (ClueInfo info: allInfo) {
            if (info.hasCard()) { // if this card was shown add it
                shownCards.add(info.card());
            }
        }
        return shownCards;
    }

    /*private Set<ClueCard> hasCards(Set<ClueInfo> knownNodesInfo) {
        Set<ClueCard> out = new HashSet<>();
        for (ClueInfo info: knownNodesInfo) {
            if (info.hasCard()) {
                out.add(info.card());
            }
        }
        return out;
    }

    private Set<ClueCard> notHasCards(Set<ClueInfo> knownNodesInfo) {
        Set<ClueCard> out = new HashSet<>();
        for (ClueInfo info: knownNodesInfo) {
            if (!info.hasCard()) {
                out.add(info.card());
            }
        }
        return out;
    }*/

    /*
     * Cards that a player has or are in the center. Builds a set of cards that cannot be in the manila envelope.
     */
    /*private Set<ClueCard> foundCards(Set<ClueInfo> knownNodesInfo) {
        Set<ClueCard> foundCards = new HashSet<>();

        for (ClueInfo info: knownNodesInfo) {
            if (info.hasCard()) { // if a card was revealed (AND known since from knownNodesInfo), remove as possibility
                foundCards.add(info.card());
            }
        }
        return foundCards;
    }*.

    /*
     * Gets the cards that can be deduced out of the possibilities through revealed cards. 
     * If this is only one card, this card is known.
     */
    private Set<ClueCard> byRevealingSet(Set<ClueInfo> knownInfo, ClueCard[] possibilities) {
        Set<ClueCard> outSet = new HashSet<ClueCard>(Arrays.asList(possibilities)); // get possibilities to remove
        for (ClueCard possibility: possibilities) {
            for (ClueInfo info: knownInfo) {
                if (info.card().equals(possibility) && info.hasCard()) {
                    outSet.remove(possibility);
                }
            }
        }
        
        return outSet;
    }

    /*
     * Gets the card that can be deduced through revealing, otherwise returns null.
     */
    private ClueCard byRevealing(Set<ClueInfo> knownInfo, ClueCard[] possibilities) {
        Set<ClueCard> outSet = byRevealingSet(knownInfo, possibilities);
        if (outSet.size()==1) {
            return outSet.iterator().next();
        }
        return null;
    }

    /*
     * Gets the card that can be determined from possibilities by removing 
     * If a single card cannot be determined, returns null.
     */
    private ClueCard byNotRevealing(Set<ClueInfo> knownInfo, ClueCard[] possibilities) {
        for (ClueCard possibility: possibilities) {
            boolean isAnswer = true;
            // Condition 1: No other node can have this card (center cards on table or other players)
            for (ClueInfo info: knownInfo) {
                if (info.card().equals(possibility) && info.hasCard()) { // if somebody has card it is not answer
                    isAnswer = false;
                    break; // exit since we know this possibility doesn't work
                }
            }   
            if (!isAnswer) {continue;} // continue 

            // Condition 2: All players must have a node that indiciates they do not have this card
            Set<CluePlayer> playersWithCard = new HashSet<>(Arrays.asList(players)); // starts with all players, whiddled down
            for (ClueInfo info: knownInfo) {
                if (info.card().equals(possibility) && info.hasCard()==false) { // if player doesn't have this card
                    playersWithCard.remove(info.revealingPlayer());
                }
            }

            // if any player doesn't have info saying they do not have this card, we cannot say it in envelope for certain
            if (!playersWithCard.isEmpty()) { // if a player hasn't been shown not to have this card we don't have answer
                isAnswer = false;
            }
            if (!isAnswer) {continue;} // again continue if not this answer

            return possibility;
        }

        return null; // return null if not output can be determined 
    }


    /* Gets all possibile guesses from sets given. Very slow a memory intensive for large sets. */
    private Set<ClueGuess> getAllPossibilities(Set<ClueCard> peopleSet, Set<ClueCard> roomSet, Set<ClueCard> weaponSet) {
        Set<ClueGuess> allPossibilities = new HashSet<>();
        for (ClueCard personCard: peopleSet) {
            for (ClueCard roomCard: roomSet) {
                for (ClueCard weaponCard: weaponSet) {
                    allPossibilities.add(new ClueGuess(personCard, roomCard, weaponCard));
                }
            }
        }
        return allPossibilities;
    }

    /*
     * Determines if the answer can be determined by using another
     * player's final guess to elimate one of a few possibilites.
     * However, due to the number of potential possibilities and the 
     * very little information somebody failing the final guess gives,
     * this almost always fails.
     */
    // TODO in far future when trying to understannd other player strategy, tracking with player
    // made the guess for finalGuesses may be useful for this
    private ClueGuess finalGuessesEdgeCase(Set<ClueInfo> knownNodesInfo, ClueCard[] possibilitesByNotRevealing) {
        // short circuit to save time as this is often the case, and this doesn't need to be considered
        if (finalGuesses.size()==0) {return null;} 

        Set<ClueCard> peopleSet = byRevealingSet(knownNodesInfo, peopleCards);
        Set<ClueCard> roomsSet = byRevealingSet(knownNodesInfo, roomCards);
        Set<ClueCard> weaponsSet = byRevealingSet(knownNodesInfo, weaponCards);

        // go through people rooms and weapons and collect either number of possibilities or certain ansewr for each
        int numPossibleAnswers = 1;
        if (possibilitesByNotRevealing[0]==null) { // if it is not equal to null, we have the answer, so the only possibility is one, and we do not have to multiply
            numPossibleAnswers *= peopleSet.size();
        }
        else {
            peopleSet.clear(); // since answer is known, make possibilites just include this one
            peopleSet.add(possibilitesByNotRevealing[0]);
        }

        if (possibilitesByNotRevealing[1]==null) {
            numPossibleAnswers *= roomsSet.size();
        }
        else {
            roomsSet.clear(); // since answer is known, make possibilites just include this one
            roomsSet.add(possibilitesByNotRevealing[1]);
        }

        if (possibilitesByNotRevealing[2]==null) {
            numPossibleAnswers *= weaponsSet.size();
        }
        else {
            weaponsSet.clear(); // since answer is known, make possibilites just include this one
            weaponsSet.add(possibilitesByNotRevealing[2]);
        }

        // if there are too many possibilities for the final guesses to give a definite answer, don't even bother
        if (numPossibleAnswers-1>finalGuesses.size()) { 
            return null;
        }

        // in the case there are just enough final guesses to make it possible,
        // check those final guesses actually make the possibilities down to 1.
        // NOTE: Without the check above, this would calculate a HUGE set of far too many possibilites
        // this edge case will then only be considered when this is tractable (unless an imposslble number of final guesses have been made)
        Set<ClueGuess> allPossibilites = getAllPossibilities(peopleSet, roomsSet, weaponsSet);
        for (ClueGuess failedFinalGuess: finalGuesses) { // one by one remove from allPossibilities
            allPossibilites.remove(failedFinalGuess);
        }
        if (allPossibilites.size()==1) { // if final guesses narrowed it down, return answer, otherwise null
            return allPossibilites.iterator().next();
        }
        return null;
    }

    /*
     * Determines if all the info needed for a guess has been gathered. Should be run on a pruned tree.
     * This is done by considering all paths down the tree (assuming it has been properly pruned)
     * and if they all result in only 1 three card possibility for the manila envelope, we are done.
     * Otherwise, there are still multiple possibilities. 
     * Returns null if no answer can be determined yet, otherwise, an array of the answer
     */
    // TODO need an internal set for SolverTree to add final guesses that can be removed from possible answers at the end of this function to make solver actually perfect
    // TODO could make partialInfo function to see what is known, even if the whole thing isn't known instead of all or nothing like this for testing
    public ClueCard[] getAnswer() {
        // TODO (do this after revamping the arguments)
        // TODO seperately consider what people DO and DON'T HAVE FOR ANSWER
        Set<ClueCard> possibleCards = new HashSet<>();
        possibleCards.addAll(Arrays.asList(peopleCards));
        possibleCards.addAll(Arrays.asList(roomCards));
        possibleCards.addAll(Arrays.asList(weaponCards));

        Set<ClueInfo> knownNodesInfo = knownCardInformation();
        
        ClueCard[] possibilitiesByRevealing = new ClueCard[] {byRevealing(knownNodesInfo, peopleCards), byRevealing(knownNodesInfo, roomCards), byRevealing(knownNodesInfo, weaponCards)};
        ClueCard[] possibilitesByNotRevealing = new ClueCard[] {byNotRevealing(knownNodesInfo, peopleCards), byNotRevealing(knownNodesInfo, roomCards), byNotRevealing(knownNodesInfo, weaponCards)};

        ClueCard[] out = new ClueCard[3];
        // combine arrays based on each technique, taking known results for each category
        for (int idx=0; idx<possibilitesByNotRevealing.length; ++idx) {
            if (possibilitiesByRevealing[idx]==null && possibilitesByNotRevealing[idx]==null) { // if both null don't know answer
                // check finalGuesses edge case (which almost always is null) since answer not determined otherwise
                ClueGuess answer = finalGuessesEdgeCase(knownNodesInfo, possibilitesByNotRevealing);
                if (answer==null) {return null;} // usually happens since final guess failure don't provide much info
                return answer.cardArray(); 
            }
            else if (possibilitiesByRevealing[idx]==null) { // if one is not null, select the answer from that one
                out[idx] = possibilitesByNotRevealing[idx];
            }
            else if (possibilitesByNotRevealing[idx]==null) {
                out[idx] = possibilitiesByRevealing[idx];
            }
            else { //if neither are null, they came to the same conclusion through both methods
                out[idx] = possibilitiesByRevealing[idx]; // doesn't matter which is used (revealing or not revealing) since they are the same
            }
        }
    
        // we know answer here since out has no nulls
        return out;
    }
    
    /*
     * Gets a HashSet of all known information (nodes with no children).
     * This function exploits the fact that nodes with 3 children will 
     * reconnect a null node afterward.
     */
    private Set<ClueInfo> knownCardInformation() {
        Node<ClueInfo> node = tree.getHead();
        HashSet<ClueInfo> knownInfo = new HashSet<>();
        // don't add head since value is null
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
     * TODO document
     */
    public Map<CluePlayer, ClueHand> playerCards(Set<ClueInfo> knownInfoSet) {
        Map<CluePlayer, ClueHand> playerCards = new HashMap<>(); // known cards for each player
        for (ClueInfo info: knownInfoSet) {
            CluePlayer revPlayer = info.revealingPlayer();
            if (!playerCards.containsKey(revPlayer)) { // if player not seen, make new empty hand
                playerCards.put(revPlayer, new ClueHand(3)); // TODO make handsize work for actual value based on number of players
            }
            // add card to revealing player since it is known
            if (info.hasCard()==true) { // add if card was actually shown
                playerCards.get(revPlayer).addCard(info.card());
            }
        }
        return playerCards;
    }

    /*
     * Removes the nodes passed in nodesSet from the tree by walking through tree.
     * Nodes set should only contain unknown nodes on multi child layers of the tree
     */
    private void removeNodes(Set<Node<ClueInfo>> nodesSet) {
        // TODO complete me
        Node<ClueInfo> node = tree.getHead();
        while (node!=null && node.hasChildren()) { // node can equal null if only child removed TODO check this
            for (int childIdx=0; childIdx<node.numChildren(); ++childIdx) { // check if any children are violating
                Node<ClueInfo> child = node.getChildren()[childIdx];
                if (nodesSet.contains(child)) {
                    // remove node
                    node.removeChild(childIdx);
                    child.clearChildren(); // not sure if technically needed, but disconnecting child completly remove it from tree
                }
            }
            node = node.getChildren()[0]; // only one parent node to multi child layers, so this works
        }
    }

    /*
     * Performs prune() once on the tree, see docstring of prune for details.
     */
    private boolean pruneOnce() {
        Node<ClueInfo>[] nodes = tree.getNodesBFS();
        Set<ClueInfo> knownNodesInfo = knownCardInformation();
        Set<ClueInfoCardAndRevealingPlayer> revealingPlayerInfo = ClueInfoCardAndRevealingPlayer.buildSet(knownNodesInfo); // used for type 2

        Map<CluePlayer, ClueHand> playerCards = playerCards(knownNodesInfo);
        Set<CluePlayer> seenPlayers = playerCards.keySet();
        Map<ClueInfo, Node<ClueInfo>> nodeMap = new HashMap<>();

        Set<Node<ClueInfo>> nodesToRemove = new HashSet<>(); // stores nodes to remove when passing through tree
        // build map to get node with given clueInfo (No clueInfo should occur twice)
        for (Node<ClueInfo> node: nodes) { // from 1. in docstring
           nodeMap.put(node.getValue(), node);
        }

        // prune on conditions
        for (Node<ClueInfo> node: nodes) { // TODO make prune only unknown nodes in the future
            // filter out nodes that have value null, since they will not be removed. Skips known info
            if (knownNodesInfo.contains(node.getValue()) || node.getValue()==null) { // TODO make iterate over known nodes instead?
                continue;
            }
            ClueInfo cInfo = node.getValue();
            // from 1., 2., and 3., in docstring
            // cond1
            boolean cond1 = false;
            for (CluePlayer player: seenPlayers) {
                if (player==null || player.equals(node.getValue().revealingPlayer())) { // if same player has card again, no contradiction
                    continue;
                }
                ClueInfoCardAndRevealingPlayer newPlayerInfoCopy = new ClueInfoCardAndRevealingPlayer(player, cInfo.card(), cInfo.hasCard);
                if (revealingPlayerInfo.contains(newPlayerInfoCopy)) { // TODO doesn't work, checking if same player for obth
                    cond1 = true;
                }
            }
                
            // cond 2
            ClueInfoCardAndRevealingPlayer newRevealingPlayerInfo = new ClueInfoCardAndRevealingPlayer(cInfo);
            newRevealingPlayerInfo.flipHasCard();
            boolean cond2 = revealingPlayerInfo.contains(newRevealingPlayerInfo); // TODO doesn't work with different guessing players
            
            // cond 3
            ClueHand revPlayerSeenCards = playerCards.get(cInfo.revealingPlayer());
            if (revPlayerSeenCards==null) { // if player for this node has no known node, say hand size is 0
                revPlayerSeenCards = new ClueHand(3); // TODO replace with proper size
            }
            boolean atMaxHandSize = revPlayerSeenCards.size()==revPlayerSeenCards.maxSize();
            HashSet<ClueCard> seenCardsSet = new HashSet<>(Arrays.asList(revPlayerSeenCards.getCards())); // TODO this recomuptation is not very efficient
            // cannot have any unknown cards if at max hand size (all player cards are known)
            boolean cond3 = atMaxHandSize && !(seenCardsSet).contains(cInfo.card());
            if (cond1 | cond2 | cond3) { 
                nodesToRemove.add(node);
            }
        }

        if (nodesToRemove.isEmpty()) { // if empty, nothing more to prune
            return false;
        }
        else {
            removeNodes(nodesToRemove); 
            return true;
        }
    }
    
    /*
     * Finds and removes nodes with ClueInfo that would disqualify
     * unknown nodes with contradictory ClueInfo if present in tree, based on 3 pruning conditions:
     *      1. An unknown node asserts the same card another player is known to have.
     *      2. An unknown node asserts that a player has a card that a known node states they do not have.
     *          NOTE: An unknown node cannot assert that a player does not have a card, since unknown nodes
     *                occur when the tree branches, which only occurs when a card is revealed.
     *      3. An unknown node, if a player had it, would cause them to have too many cards in hand including known nodes.
     * 
     * Prunes until no more removable nodes are found
     */
    public void prune() {
        boolean pruneSuccess = true;
        while (pruneSuccess) { // prune until nothing more to prune
            pruneSuccess = pruneOnce();
        }
    }

    /*
     * Given an array of info gathered throughout the game, builds a tree of possibilities for each player.
     * Does not prune the output, so improper nodes (contradictions) may still remain.
     */
    public void buildNoPrune(ClueInfo[] totalInfo) { 
        clear(); // clear to rebuild
        initializeKnown(); 
        for (ClueInfo info: totalInfo) {
            grow(info);
        }
    }
    
    /*
     * Given an array of info gathered throughout the game, builds a tree of possibilities for each player
     */
    public void build(ClueInfo[] totalInfo) { 
        clear(); // clear to rebuild
        initializeKnown(); 
        for (ClueInfo info: totalInfo) {
            grow(info);
        }
        prune();
    }

     /*
     * Given an array of info gathered throughout the game, builds a tree of possibilities for each player.
     * Also changes the final guesses that have been made by other players within the tree for getAnswer().
     */
    public void build(ClueInfo[] totalInfo, Set<ClueGuess> finalGuesses) { 
        build(totalInfo);
        setFinalGuesses(finalGuesses);
    }

    /*
     * Sets the final guesses that have been played, which are used in edge case 
     * to get answer in getAnswer().
     */
    public void setFinalGuesses(Set<ClueGuess> finalGuesses) {
        this.finalGuesses = finalGuesses;
    }

    /*
     * Given an array of info gathered throughout the game, builds a tree of possibilities for each player.
     * Also does NOT prune tree.
     */
    public void buildNoInitialization(ClueInfo[] totalInfo) { 
        clear(); // clear to rebuild
        for (ClueInfo info: totalInfo) {
            grow(info);
        }
    }
}
