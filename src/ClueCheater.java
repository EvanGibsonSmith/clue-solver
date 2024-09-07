import java.util.Arrays;

public class ClueCheater {
    
    public static void main(String[] args) {
        ClueGame game = new ClueGame(3);
        game.runRound();
        //game.runRound();
        System.out.println(Arrays.toString(game.revealedInfo()));
        SolverTree tree = new SolverTree();
        tree.build(game.revealedInfo());
        System.out.println(tree);
    }
}
