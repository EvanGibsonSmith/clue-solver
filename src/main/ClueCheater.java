package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClueCheater {
    
    public static void main(String[] args) {
        // TODO rebuilding repeatedly instead of using grow is inefficient, but should work fine
        Scanner scnr = new Scanner(System.in);
        System.out.println("Please enter the number of players in the game");
        int numPlayers = scnr.nextInt();
        // TODO need to calculate proper hand size for each player, instead of asking?
        System.out.println("Please enter the hand size of player this game");
        int playerHandSize = scnr.nextInt();

        String[] rooms = {"conservatory", "dining", "ballroom", "study", "hall", "lounge", "library", "billiard"};
        String[] people = {"scarlett", "mustard", "white", "green", "peacock", "plum"};
        String[] weapons = {"candlestick", "knife", "lead pipe", "pistol", "rope", "wrench"};
        
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

        // TODO could add custom card strings here to pass
        SolverTree tree = new SolverTree(players[0], players, rooms, people, weapons); 

        // initialize card information TODO need to add central card information here as well as elsewhere to make this work correctly
        System.out.println("Please enter cards in your hand");
        for (int i=0; i<playerHandSize; ++i) {
            String cardStr = scnr.next();
            players[0].getHand().addCard(new ClueCard(cardStr));
        }

        ArrayList<ClueInfo> info = new ArrayList<>();
        while (true) {
            System.out.println("Waiting for next piece of info...");
            System.out.println("Please input guessing player name");
            String guessingPlayerStr = scnr.next();

            System.out.println("Please input guess [person] [room] [weapon]" );
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
            ClueGuess guess = new ClueGuess(guessPerson, guessRoom, guessWeapon);
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
            info.add(new ClueInfo(guessingPlayer, guess, revealingPlayer, new ClueCard(card)));

            // check if answer can be deduced
            tree.build(info.toArray(new ClueInfo[0])); // rebuild tree (quite inefficient but works)
            ClueCard[] answer = tree.getAnswer();
            if (answer==null) {
                System.out.println("No answer found yet!");
            }
            else {
                System.out.println("Answer: " + Arrays.toString(answer));
            }
            System.out.println(tree.getAnswer());
        }
    }
}
