package main;

import java.util.Scanner;
import java.util.Set; 
import java.util.HashSet; 

// TODO add functionality to make final guess (to leave the game or win)
// TODO make the clue player collect the informatoin since we as a cheating player are not omnipotent? 
// Game can also theoretically have this information if we keep track of a player, but does it make more sense to have it in Player?
public class CluePlayer {
    ClueHand hand; 
    int maxHandSize;
    String name;

    public CluePlayer(String name, int handSize) {
        hand = new ClueHand(handSize);
        this.maxHandSize = handSize;
        this.name = name;
    }

    public CluePlayer(int handSize) {
        hand = new ClueHand(handSize);
        this.maxHandSize = handSize;
        name = "Player";
    }

    @Override
    public String toString() {
        return name; // TODO add more player info such as name as it is added
    }

    @Override 
    public boolean equals(Object other) { // TODO another weird equality method for HashSets, could make a wrapper for the solver with this instead of the whole player class
        if (other.getClass()!=this.getClass()) {
            return false;
        }
        return this.name.equals(((CluePlayer) other).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String getName() {
        return name;
    }
    
    public ClueHand getHand() {
        return hand;
    }

    public int getMaxHandSize() {
        return maxHandSize;
    }

    public ClueGuess makeGuess() {
        // NOTE: Could make other player objects to implement guessing in other ways (such as AI)
        Scanner scnr = new Scanner(System.in);

        System.out.println("Please input player to guess");
        String player = scnr.nextLine();
        
        System.out.println("Please input room to guess");
        String room = scnr.nextLine();
        
        System.out.println("Please input weapon to guess");
        String weapon = scnr.nextLine();

        return new ClueGuess(player, room, weapon);
    }

    private Set<ClueCard> getRevealableCardValues(ClueGuess guess) {
        Set<ClueCard> revealableCard = new HashSet<ClueCard>();
        // maybe could use a set for hand for something like this if hands are large?
        for (ClueCard card: hand.getHand()) {
            if (guess.getCardSet().contains(card)) {
                revealableCard.add(card);
            }
        }
        return revealableCard;
    }

    // TODO document
    public ClueCard revealInformation(ClueGuess guess) {
        Set<ClueCard> revealableCards = getRevealableCardValues(guess); // just the "names", like as courtroom, scarlett etc.
        if (revealableCards.isEmpty()) { // if empty, no card can be revealed
            return null;
        }
        System.out.println("Which Card Would you like to Reveal?");
        Scanner scnr = new Scanner(System.in); // ask player which card to reveal
        boolean validCardChoice = false; // represents if player picked a card actually within their hand
        String revealedCardValue = null; // which card the player chooses to reveal
        while (!validCardChoice) {
            revealedCardValue = scnr.nextLine();
            validCardChoice = revealableCards.contains(new ClueCard(revealedCardValue));
        }
        return new ClueCard(revealedCardValue);
    }
}
