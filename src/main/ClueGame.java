package main;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

// NOTE: This class isn't used in any way for the solver, and needs to be made into a playable game if used
public class ClueGame {
    CluePlayer[] players;
    int turnCursor = 0;
    ClueDeck deck = new ClueDeck();
    Set<ClueCard> centerCards = new HashSet<>();
    ArrayList<ClueInfo> revealedInfo = new ArrayList<>();
    int numPlayers;

    public ClueGame(int numPlayers) {
        // populate new playerrs
        this.numPlayers = numPlayers;
        players = new CluePlayer[numPlayers];
        for (int i=0; i<players.length; ++i) {
            players[i] = new CluePlayer("Player " + i, 3); // TODO don't set hand size like this, give proper number from game rules
        }
        dealCards();
    }

    public ClueGame(int numPlayers, int turnCursor) {
        this(numPlayers);
        this.turnCursor = turnCursor;
    }

    public ClueGame(CluePlayer[] players) {
        this.numPlayers = players.length;
        this.players = players;
        dealCards();
    }

    public ClueGame(CluePlayer[] players, int turnCursor) {
        this(players);
        this.turnCursor = turnCursor;
    }
    
    private void dealCards() {
        int handSize[] = {-1, 3, 3, 3, 3}; // the hand size for each player depending on the number of players TODO find online
        for (CluePlayer player: players) {
            for (int c=0; c<handSize[numPlayers]; ++c) {
                player.getHand().addCard(deck.drawCard());
            }
        }
    }

    public CluePlayer[] getPlayers() {
        return this.players;
    }
    
    public void incrementCursor() {
        turnCursor = incrementCursor(turnCursor);
    }

    private int incrementCursor(int cursor) {
        return (cursor + 1) % numPlayers;
    }

    public CluePlayer getCurrentPlayer() {
        return players[turnCursor];
    }

    public void runRound() {
        CluePlayer currPlayer = getCurrentPlayer();
        ClueGuess guess = currPlayer.makeGuess();

        // run through players to 
        int revealCardCursor = turnCursor;
        ClueCard newInfo = null; // null before any information is revealed
        while (newInfo==null) {
            revealCardCursor = incrementCursor(revealCardCursor); // increment FIRST to not count search player guessing
            if (revealCardCursor==turnCursor) { // if gone around the entire table 
                break;
            }
            newInfo = players[revealCardCursor].revealInformation(guess); // keep searching for new info
            // add revealed information (if it exists)
            revealedInfo.add(new ClueInfo(currPlayer, guess, players[revealCardCursor], newInfo));
        }
    }

    public ClueInfo[] revealedInfo() {
        return revealedInfo.toArray(new ClueInfo[0]);
    }

    public void play() {
        // TODO add ending game functionality to players
        runRound();
    }

    public static void main(String[] args) {
        ClueGame game = new ClueGame(4);
        System.out.println("Players");
        for (CluePlayer p: game.getPlayers()) {
            System.out.println(p + " " + p.getHand());
        }
        game.runRound();
        //System.out.println(game.getRevealedInfo()[0].getPlayer() + " " + game.getRevealedInfo()[0].getCard());
        for (int i=0; i<game.revealedInfo().length; ++i) {
            System.out.println(game.revealedInfo()[i]);
        }
    }
}


