package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ClueCheater {

    static private boolean checkAnswer(SolverTree tree) {
        ClueCard[] answer = tree.getAnswer();
        if (answer==null) {
            System.out.println("No answer found yet!");
            return false;
        }
        System.out.println("Answer: " + Arrays.toString(answer));
        return true;
    }
    
    public static void main(String[] args) {
        // rebuilding repeatedly instead of using grow is inefficient, but should work fine
        Scanner scnr = new Scanner(System.in);
        System.out.println("Please enter the number of players in the game");
        int numPlayers = scnr.nextInt();
        // the amount of cards in a hand for each player should be defined based on the number of players in the game,
        // but can still ask
        System.out.println("Please enter the hand size of player this game");
        int playerHandSize = scnr.nextInt();

        String[] people = {"scarlett", "mustard", "white", "green", "peacock", "plum"};
        String[] rooms = {"conservatory", "dining", "ballroom", "study", "hall", "lounge", "library", "billiard"};
        String[] weapons = {"candlestick", "knife", "lead pipe", "pistol", "rope", "wrench"};

        // smaller set used for testing
        /*String[] people = {"mustard", "peacock", "plum", "white", "scarlett"};
        String[] rooms = {"hall", "study", "lounge", "ballroom"};
        String[] weapons = {"knife", "lead_pipe", "pistol"}; */
        
        System.out.println("Please input player names (you will be the first player entered)");
        String[] playerNames = new String[numPlayers];
        for (int p=0; p<numPlayers; ++p) {
            System.out.println("Please input player name");
            playerNames[p] = scnr.next();
        }

        CluePlayer[] players = new CluePlayer[numPlayers]; 
        for (int i=0; i<players.length; ++i) {
            players[i] = new CluePlayer(playerNames[i], playerHandSize); 
        }

        // initialize card information
        System.out.println("Please enter cards in your hand");
        for (int i=0; i<playerHandSize; ++i) {
            String cardStr = scnr.next();
            if (cardStr.equals("done")) { // shouldn't be needed, but can use if unequal hand size
                break;
            }
            players[0].getHand().addCard(new ClueCard(cardStr));
        }

        ArrayList<ClueCard> centerCards = new ArrayList<>();
        System.out.println("Please enter shared cards in the center of the board. Enter 'done' when done. ");
        while (true) {
            String centerCardStr = scnr.next();
            if (centerCardStr.equals("done")) {
                break;
            }
            centerCards.add(new ClueCard(centerCardStr));
        }
        
        SolverTree tree = new SolverTree(players[0], players, centerCards.toArray(new ClueCard[0]), people, rooms, weapons); 

        List<ClueInfo> info = new ArrayList<>();
        Set<ClueGuess> finalGuesses = new HashSet<>();
        while (true) {
            System.out.println("Waiting for next piece of info...");
            System.out.println("Please input guessing player name or 'final' if a final guess has been done.");
            String guessingPlayerStr = scnr.next();
            if (guessingPlayerStr.equals("final")) {
                System.out.println("Please input final guess [person] [room] [weapon]");
                String guessPerson = scnr.next();
                String guessRoom = scnr.next();
                String guessWeapon = scnr.next();
                finalGuesses.add(new ClueGuess(guessPerson, guessRoom, guessWeapon));
                tree.build(info.toArray(new ClueInfo[0]), finalGuesses);
                boolean answerFound = checkAnswer(tree);
                if (answerFound) {break;} // done if answer found
            }

            System.out.println("Please input guess [person] [room] [weapon]");
            String guessPerson = scnr.next();
            String guessRoom = scnr.next();
            String guessWeapon = scnr.next();

            System.out.println("Please input true or false for if a card was shown");
            boolean hasCard = scnr.nextBoolean();

            if (hasCard) {
                System.out.println("Please input player that revealed card");
            }
            else {
                System.out.println("Please input player that did not reveal information");
            }
            String revealingPlayerStr = scnr.next();

            // if this player was shown a card, ask which card it was. Otherwise it will be null since they didn't know
            String card = null;
            if (guessingPlayerStr.equals(players[0].getName()) && hasCard) { 
                System.out.println("Please input which card was revealed");
                card = scnr.next();
            }

            // add information to tree
            // set up guess
            ClueGuess guess = new ClueGuess(guessPerson, guessRoom, guessWeapon, people, rooms, weapons);
            // get correct player objects for guessingPlayer and revealingPlayer
            CluePlayer guessingPlayer = null; // will be set if a correct player string was suppliied
            CluePlayer revealingPlayer = null;
            for (CluePlayer player: players) {
                if (player.getName().equals(guessingPlayerStr)) {
                    guessingPlayer = player;
                }
                if (player.getName().equals(revealingPlayerStr)) {
                    revealingPlayer = player;
                }
            }

            ClueCard infoCard;
            if (card==null) {
                infoCard = null;
            }
            else {
                infoCard = new ClueCard(card, people, rooms, weapons);
            }
            info.add(new ClueInfo(guessingPlayer, guess, revealingPlayer, infoCard, hasCard));

            tree.build(info.toArray(new ClueInfo[0]));
            boolean answerFound = checkAnswer(tree);
            if (answerFound) {break;}
        }
        scnr.close();
    }
}
