package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
        SolverTree tree = new SolverTree(players[0]);

        ArrayList<ClueInfo> info = new ArrayList<>();
        // player 1 has plum and hall
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], new ClueCard("plum")));

        info.add(new ClueInfo(players[0], new ClueGuess("peacock", "hall", "rope"),
                              players[1], new ClueCard("hall")));

        // player 1 reveals something to player 2, so player 0 can deduce player 2 has lead pipe
        info.add(new ClueInfo(players[1], new ClueGuess("plum", "hall", "lead pipe"),
                              players[2], null, true));
        
        tree.build(info.toArray(new ClueInfo[0]));
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
        SolverTree tree = new SolverTree(players[0]);

        ArrayList<ClueInfo> info = new ArrayList<>();
        // player 1 does not have plum, hall or pistol
        info.add(new ClueInfo(players[0], new ClueGuess("plum", "hall", "pistol"),
                              players[1], null, false));

        // player 1 reveals something to player 2, which must be lead pipe, since player 1 doesn't have plum or hall
        info.add(new ClueInfo(players[2], new ClueGuess("plum", "hall", "lead pipe"),
                              players[1], null, true));
        
        tree.build(info.toArray(new ClueInfo[0]));
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
        SolverTree tree = new SolverTree(players[0]);

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
        
        tree.build(info.toArray(new ClueInfo[0]));
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

    public static void main(String[] args) {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();
        SolverTree tree = new SolverTree(players[0]);

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
