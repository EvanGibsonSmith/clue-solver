package main;
import java.util.ArrayList;
import java.util.Arrays;

public class ClueCheater {
    
    // TODO add more rigorous tests here
    // NOTE: delete later after tests, but this build process seems to be working.
    public static void main(String[] args) {
        ClueGame game = new ClueGame(3);

        System.out.println(Arrays.toString(game.revealedInfo()));
        ArrayList<ClueInfo> info = new ArrayList<>();
        CluePlayer[] players =game.getPlayers();
        SolverTree tree = new SolverTree(players[0], players);

        // add information that we know (if the guessing player was not THIS player, we do not know revealed card).
        // Since we are player 0, we know the revealed card
        //info.add(new ClueInfo(players[0], new ClueGuess("green", "dining", "lead pipe"), players[1], null, false));  // player 1 had nothing to reveal
        //info.add(new ClueInfo(players[0], new ClueGuess("green", "dining", "lead pipe"), players[2], new ClueCard("dining"), true));  
        // For example, below player 2 reveals something to player 1, but we do not know what it is
        info.add(new ClueInfo(players[1], new ClueGuess("green", "hall", "pistol"), players[2], null, true));  // had something to reveal, but we don't know what (hence null card)
        // Player two makes a guess that nobody can return
        //info.add(new ClueInfo(players[2], new ClueGuess("scarlett", "study", "rope"), players[0], null, false)); 
        info.add(new ClueInfo(players[2], new ClueGuess("scarlett", "study", "rope"), players[1], null, false)); 

        tree.build(info.toArray(new ClueInfo[0]));
        System.out.println("BUILD OUTCOME");
        for (int layerIdx=0; layerIdx<10; ++layerIdx) {
            System.out.println(Arrays.toString(tree.getTree().getNodesLayer(layerIdx)));
            System.out.println("\n\n\n");
        }
    }
}
