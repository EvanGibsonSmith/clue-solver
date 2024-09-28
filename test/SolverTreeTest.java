package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;

import main.SolverTree;
import main.ClueCard;
import main.ClueGame;
import main.ClueGuess;
import main.ClueInfo;
import main.CluePlayer;
import data_structures.Node;

import java.util.Set;
import java.util.HashSet;

class SolverTreeTest {
    // NOTE: See prune for type 1 2 and 3 contradictions

    @Test
    /*
     * For a player having and simulatanously not having a card.
     */
    void Type1Contradiction() {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        SolverTree tree = new SolverTree(players[0], players);

        ArrayList<ClueInfo> info = new ArrayList<>();
        // player 1 has plum and hall
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum")));

        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "hall", "rope"),
                              players[1], new ClueCard("hall")));

        // player 1 reveals something to player 2, so player 0 can deduce player 2 has lead pipe
        info.add(new ClueInfo(players[1], new ClueGuess("plum", "hall", "lead pipe"),
                              players[2], null, true));
        
        tree.buildNoInitialization(info.toArray(new ClueInfo[0]));
        Set<ClueInfo> treeInfoSet = new HashSet<>();
        for (Node<ClueInfo> node: tree.getTree().getNodesBFS()) {
            treeInfoSet.add(node.getValue());
        }
        System.out.println("Unpruned: " +  Arrays.toString(tree.getTree().getNodesBFS()));
        assertTrue(tree.getTree().getNodesBFS().length==7);
        // contains all three possibilities 
        assertTrue(treeInfoSet.contains(new ClueInfo(players[1],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[2], new ClueCard("plum"))));
        assertTrue(treeInfoSet.contains(new ClueInfo(players[1],
                                        new ClueGuess("plum", "hall", 
                                "lead pipe"), players[2], new ClueCard("lead pipe"))));
        assertTrue(treeInfoSet.contains(new ClueInfo(players[1],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[2], new ClueCard("hall"))));
        tree.prune(); // two contradicing nodes in possibilities of plum and hall are gone

        Set<ClueInfo> prunedTreeInfoSet = new HashSet<>();
        for (Node<ClueInfo> node: tree.getTree().getNodesBFS()) {
            prunedTreeInfoSet.add(node.getValue());
        }
        // does not contain contradicting plum or hall
        System.out.println("Pruned: " +  Arrays.toString(tree.getTree().getNodesBFS()));
        assertFalse(prunedTreeInfoSet.contains(new ClueInfo(players[1],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[2], new ClueCard("plum"))));
        assertTrue(prunedTreeInfoSet.contains(new ClueInfo(players[1],
                                        new ClueGuess("plum", "hall", 
                                "lead pipe"), players[2], new ClueCard("lead pipe"))));
        assertFalse(prunedTreeInfoSet.contains(new ClueInfo(players[1],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[2], new ClueCard("hall"))));

        assertTrue(tree.getTree().getNodesBFS().length==5);
    }

    @Test
    /*
     * For pruning possibility of card we know player does not have
     */
    void Type2Contradiction() {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        SolverTree tree = new SolverTree(players[0], players);

        ArrayList<ClueInfo> info = new ArrayList<>();
        // player 1 does not have plum, hall or pistol
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], null, false));

        // player 1 reveals something to player 2, which must be lead pipe, since player 1 doesn't have plum or hall
        info.add(new ClueInfo(players[2], new ClueGuess("plum", "hall", "lead pipe"),
                              players[1], null, true));
        
        tree.buildNoInitialization(info.toArray(new ClueInfo[0]));
        Set<ClueInfo> treeInfoSet = new HashSet<>();
        for (Node<ClueInfo> node: tree.getTree().getNodesBFS()) {
            treeInfoSet.add(node.getValue());
        }
        System.out.println("Unpruned: " + Arrays.toString(tree.getTree().getNodesBFS()));
        assertTrue(tree.getTree().getNodesBFS().length==8);
        // contains all three possibilities 
        assertTrue(treeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[1], new ClueCard("plum"))));
        assertTrue(treeInfoSet.contains(new ClueInfo(players[2],
                                        new ClueGuess("plum", "hall", 
                                "lead pipe"), players[1], new ClueCard("lead pipe"))));
        assertTrue(treeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[1], new ClueCard("hall"))));
        tree.prune(); // two contradicing nodes in possibilities of plum and hall are gone

        Set<ClueInfo> prunedTreeInfoSet = new HashSet<>();
        for (Node<ClueInfo> node: tree.getTree().getNodesBFS()) {
            prunedTreeInfoSet.add(node.getValue());
        }
        // does not contain contradicting plum or hall
        System.out.println("Pruned: " +  Arrays.toString(tree.getTree().getNodesBFS()));
        assertFalse(prunedTreeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[1], new ClueCard("plum"))));
        assertTrue(prunedTreeInfoSet.contains(new ClueInfo(players[2],
                                        new ClueGuess("plum", "hall", 
                                "lead pipe"), players[1], new ClueCard("lead pipe"))));
        assertFalse(prunedTreeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "hall", 
                                         "lead pipe"), players[1], new ClueCard("hall"))));

        assertTrue(tree.getTree().getNodesBFS().length==6);
    }

    @Test
    /*
     * Pruning possibility where all player cards are known, so other cards are not possible
     */
    void Type3Contradiction() {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        SolverTree tree = new SolverTree(players[0], players);

        ArrayList<ClueInfo> info = new ArrayList<>();

        // player 1 reveals plum, hall, and pistol
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum"), true));

        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("hall"), true));
        
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("pistol"), true));              

        // player 1 reveals something to player 2, which must be plum, since all 3 cards in hand are known
        info.add(new ClueInfo(players[2], new ClueGuess("plum", "library", "lead pipe"),
                              players[1], null, true));
        
        tree.buildNoInitialization(info.toArray(new ClueInfo[0]));
        Set<ClueInfo> treeInfoSet = new HashSet<>();
        for (Node<ClueInfo> node: tree.getTree().getNodesBFS()) {
            treeInfoSet.add(node.getValue());
        }
        System.out.println("Unpruned: " + Arrays.toString(tree.getTree().getNodesBFS()));
        assertTrue(tree.getTree().getNodesBFS().length==8);
        // contains all three possibilities 
        assertTrue(treeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "library", 
                                         "lead pipe"), players[1], new ClueCard("plum"))));
        assertTrue(treeInfoSet.contains(new ClueInfo(players[2],
                                        new ClueGuess("plum", "library", 
                                "lead pipe"), players[1], new ClueCard("lead pipe"))));
        assertTrue(treeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "library", 
                                         "lead pipe"), players[1], new ClueCard("library"))));
        tree.prune(); // two contradicing nodes in possibilities of plum and hall are gone

        Set<ClueInfo> prunedTreeInfoSet = new HashSet<>();
        for (Node<ClueInfo> node: tree.getTree().getNodesBFS()) {
            prunedTreeInfoSet.add(node.getValue());
        }
        // does not contain contradicting plum or hall
        System.out.println("Pruned: " +  Arrays.toString(tree.getTree().getNodesBFS()));
        assertTrue(prunedTreeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "library", 
                                         "lead pipe"), players[1], new ClueCard("plum"))));
        assertFalse(prunedTreeInfoSet.contains(new ClueInfo(players[2],
                                        new ClueGuess("plum", "library", 
                                "lead pipe"), players[1], new ClueCard("lead pipe"))));
        assertFalse(prunedTreeInfoSet.contains(new ClueInfo(players[2],
                                         new ClueGuess("plum", "library", 
                                         "lead pipe"), players[1], new ClueCard("library"))));

        assertTrue(tree.getTree().getNodesBFS().length==6);
    }

    @Test
    /*
     * Test of pruning on tiny game with plum and green, rope and pistol, and only hall as room
     */
    void tinyGamePruning() {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        String[] people = {"plum", "green"};
        String[] rooms = {"hall"};
        String[] weapons = {"rope", "pistol"};
        SolverTree tree = new SolverTree(players[0], players, people, rooms, weapons);

        ArrayList<ClueInfo> info = new ArrayList<>();

        // player 1 reveals plum, hall, and pistol
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum"), true));

        info.add(new ClueInfo(players[1], new ClueGuess("plum", "hall", "pistol"),
                              players[0], new ClueCard("pistol"), true));
        
        info.add(new ClueInfo(players[2], new ClueGuess("green", "hall", "pistol"),
                              players[1], null, true));              

        // we can deduce "green hall rope", since player 0 has been shown plum and pistol
        tree.buildNoInitialization(info.toArray(new ClueInfo[0]));
        tree.prune();
        System.out.println(tree.getAnswer());
    }

    @Test
    /*
     * Test of tiny game that allows answer of room (by default), person (by revealing), and weapon (by NOT revealing)
     * by revealedCards (all other possibilities have been revealed).
     */
    void tinyGameGetAnswer() {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        String[] people = {"plum", "green"};
        String[] rooms = {"hall"}; // by default 
        String[] weapons = {"rope", "pistol", "lead pipe"};
        SolverTree tree = new SolverTree(players[0], players, people, rooms, weapons);

        ArrayList<ClueInfo> info = new ArrayList<>();

        // player 1 reveals plum, allowing green to be deduced
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum"), true));

        // player 1 and player 2 both don't have rope, so we know that it must be rope
        // NOTE: Peacock that is not used for answer, but is here so that it doesn't mess with other method by revealing 
        // by getting plum by process of elimintation since the players wouldn't have green.
        // TODO fix this
        
        // below indicates this THIS player doesn't have these cards. In a real game, tree is 
        // initialized to have the cards that this player does and doesn't have (all the known info of given player)
        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "hall", "pistol"),
                              players[0], null, false));

        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "hall", "pistol"),
                              players[1], null, false));

        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "hall", "pistol"),
                              players[2], null, false));
        
        // we can deduce "green hall rope", since player 0 has been shown plum and pistol
        tree.buildNoInitialization(info.toArray(new ClueInfo[0]));
        tree.prune();
        ClueCard[] deducedAnswer = tree.getAnswer();
        ClueCard[] realAnswer = new ClueCard[] {new ClueCard("green"), new ClueCard("hall"), new ClueCard("pistol")};
        for (int idx=0; idx<deducedAnswer.length; ++idx) {
            assertEquals(deducedAnswer[idx], realAnswer[idx]);
        }
    }

    @Test
    /*
     * Test of full sized game where nobody has the first guess Scarlett, Ballroom, Pistol
     */
    void bigGameNobodyHasFirstGuess() {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        SolverTree tree = new SolverTree(players[0], players); // has default players, rooms, and weapons

        ArrayList<ClueInfo> info = new ArrayList<>();

        info.add(new ClueInfo(players[0], new ClueGuess("scarlett", "ballroom", "pistol"),
                              players[0], null, false));

        info.add(new ClueInfo(players[0], new ClueGuess("scarlett", "ballroom", "pistol"),
                              players[1], null, false));

        info.add(new ClueInfo(players[0], new ClueGuess("scarlett", "ballroom", "pistol"),
                              players[2], null, false));

        // we can deduce scarlett ballroom pistol since it isn't anywhere else on the board and player's don't have it
        tree.buildNoInitialization(info.toArray(new ClueInfo[0]));
        ClueCard[] deducedAnswer = tree.getAnswer();
        System.out.println(Arrays.asList(deducedAnswer));
        ClueCard[] realAnswer = new ClueCard[] {new ClueCard("scarlett"), new ClueCard("ballroom"), new ClueCard("pistol")};
        for (int idx=0; idx<deducedAnswer.length; ++idx) {
            assertEquals(deducedAnswer[idx], realAnswer[idx]);
        }
    }

    @Test
    /*
     * Test of full sized game where nobody has the first guess Scarlett, Ballroom, Pistol
     */
    void bigGameNobodyHasFirstGuessWithInitialization() {
        CluePlayer[] players = new CluePlayer[3]; // TODO make other tests like this instead of using game since game deals cards
        for (int i=0; i<players.length; ++i) {
            players[i] = new CluePlayer("Player " + i, 3); // TODO don't set hand size like this, array
        }
        // add cards for each players
        players[0].getHand().addCard(new ClueCard("mustard"));
        players[0].getHand().addCard(new ClueCard("peacock"));
        players[0].getHand().addCard(new ClueCard("hall"));

        players[1].getHand().addCard(new ClueCard("plum"));
        players[1].getHand().addCard(new ClueCard("knife"));
        players[1].getHand().addCard(new ClueCard("lead pipe"));

        players[2].getHand().addCard(new ClueCard("white"));
        players[2].getHand().addCard(new ClueCard("study"));
        players[2].getHand().addCard(new ClueCard("lounge"));

        SolverTree tree = new SolverTree(players[0], players); // has default players, rooms, and weapons

        ArrayList<ClueInfo> info = new ArrayList<>();

        // this line is needed without initilization of cards this player has and doesn't. Initlization adds this 
        // implicitly known information to the tree 

        // this commented out guess shouldn't be needed if initialzation works to implicitly add this known
        // info (that this player doesn't have any of these cards
        //info.add(new ClueInfo(players[0], new ClueGuess("scarlett", "ballroom", "pistol"),
        //                      players[0], null, false));

        info.add(new ClueInfo(players[0], new ClueGuess("scarlett", "ballroom", "pistol"),
                              players[1], null, false));

        info.add(new ClueInfo(players[0], new ClueGuess("scarlett", "ballroom", "pistol"),
                              players[2], null, false));

        // we can deduce scarlett ballroom pistol since it isn't anywhere else on the board and player's don't have it
        tree.build(info.toArray(new ClueInfo[0]));
        System.out.println(tree.getTree().getNodesBFS().length);
        ClueCard[] deducedAnswer = tree.getAnswer();
        ClueCard[] realAnswer = new ClueCard[] {new ClueCard("scarlett"), new ClueCard("ballroom"), new ClueCard("pistol")};
        for (int idx=0; idx<deducedAnswer.length; ++idx) {
            assertEquals(deducedAnswer[idx], realAnswer[idx]);
        }
    }

    @Test
    /*
     * Small game where answer is deduced because all other cards have been revealed
     */
    // NOTE: Somehow the method name smallGameProcessOfElimination causes an error which is complete black magic to me
    void smallGameAllCardsShownToPlayer() {
        CluePlayer[] players = new CluePlayer[3]; // TODO make other tests like this instead of using game since game deals cards
        for (int i=0; i<players.length; ++i) {
            players[i] = new CluePlayer("Player " + i, 3); // TODO don't set hand size like this, array
        }

        // add cards for each players
        players[0].getHand().addCard(new ClueCard("mustard"));
        players[0].getHand().addCard(new ClueCard("peacock"));
        players[0].getHand().addCard(new ClueCard("hall"));

        players[1].getHand().addCard(new ClueCard("plum"));
        players[1].getHand().addCard(new ClueCard("knife"));
        players[1].getHand().addCard(new ClueCard("lead pipe"));

        players[2].getHand().addCard(new ClueCard("white"));
        players[2].getHand().addCard(new ClueCard("study"));
        players[2].getHand().addCard(new ClueCard("lounge"));

        String[] peopleStrings = {"mustard", "peacock", "plum", "white", "scarlett"};
        String[] roomStrings = {"hall", "study", "lounge", "ballroom"};
        String[] weaponStrings = {"knife", "lead pipe", "pistol"};
        SolverTree tree = new SolverTree(players[0], players, peopleStrings, roomStrings, weaponStrings); 

        ArrayList<ClueInfo> info = new ArrayList<>();

        // all card info is revealed (somehow? Doesn't occur in normal player) to player 0

        // player 1 showns all of their cards
        info.add(new ClueInfo(players[0], new ClueGuess("mustard", "hall", "knife"),
                              players[1], new ClueCard("knife"), true));

        info.add(new ClueInfo(players[0], new ClueGuess("mustard", "hall", "lead pipe"),
                              players[1], new ClueCard("lead pipe"), true));

        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum"), true));

        // not enough info for solution yet
        tree.build(info.toArray(new ClueInfo[0]));
        assertNull(tree.getAnswer());

        // player 2 shows all of their cards
        info.add(new ClueInfo(players[0], new ClueGuess("white", "hall", "knife"),
                players[2], new ClueCard("white"), true));

        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "lounge", "knife"),
                players[2], new ClueCard("lounge"), true));

        // still not enough information until final piece revealed
        tree.build(info.toArray(new ClueInfo[0]));
        assertNull(tree.getAnswer()); // CAN figure out scarlett and pistol but not knife yet

        // final piece of info from player 2
        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "study", "knife"),
                              players[2], new ClueCard("study"), true));

        // we can deduce scarlett ballroom pistol since it isn't anywhere else on the board and player's don't have it
        tree.build(info.toArray(new ClueInfo[0]));

        ClueCard[] deducedAnswer = tree.getAnswer();
        ClueCard[] realAnswer = new ClueCard[] {new ClueCard("scarlett"), new ClueCard("ballroom"), new ClueCard("pistol")};
        for (int idx=0; idx<3; ++idx) {
            System.out.println(deducedAnswer[idx]);
            System.out.println(realAnswer[idx]);
            assertEquals(deducedAnswer[idx], realAnswer[idx]);
        }
    }

    
    
    public static void main(String[] args) {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        SolverTree tree = new SolverTree(players[0], players);

        ArrayList<ClueInfo> info = new ArrayList<>();
        // player 1 has plum and hall
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum")));

        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "hall", "rope"),
                              players[1], new ClueCard("hall")));

        // player 1 reveals something to player 2, so player 0 can deduce player 2 has lead pipe
        info.add(new ClueInfo(players[2], new ClueGuess("plum", "hall", "lead pipe"),
                              players[1], null, true));
        
        tree.build(info.toArray(new ClueInfo[0]));
        System.out.println("PRE PRUNE");
        System.out.println("Length: " + tree.getTree().getNodesBFS().length);
        System.out.println(tree);
        tree.prune();
        System.out.println("AFTER PRUNE");
        System.out.println("Length: " + tree.getTree().getNodesBFS().length);
        System.out.println(tree);
    }
    
}
