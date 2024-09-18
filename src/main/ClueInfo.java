package main;

import java.util.Objects;

public class ClueInfo {
    CluePlayer guessingPlayer;
    CluePlayer revealingPlayer;
    ClueGuess guess;
    ClueCard card;
    boolean hasCard = true; // if true, info reveals that player HAS this card, if false, reveals player does NOT have this card

    // TODO: add checks for what is a valid clue info (in combinations of guessingPlayer, hasCard, etc.) so SolverTree doesn't need to handle it
    // TODO: make a function type() that calculates which of the three cases this piece of info falls under (again, so SolverTree has less to do)
    public ClueInfo(CluePlayer guessingPlayer, ClueGuess guess, CluePlayer revealingPlayer, ClueCard card) {
        this.guessingPlayer = guessingPlayer;
        this.revealingPlayer = revealingPlayer;
        this.guess = guess;
        this.card = card;
    }

    public ClueInfo(CluePlayer guessingPlayer, ClueGuess guess, CluePlayer revealingPlayer, ClueCard card, boolean hasCard) {
        this(guessingPlayer, guess, revealingPlayer, card);
        this.hasCard = hasCard;
    }

    @Override
    public String toString() {
        if (card==null) {
            return "(" + guessingPlayer.toString() + ", " + revealingPlayer.toString() + ", null)";
        }
        else {
            return "(" + guessingPlayer.toString() + ", " + revealingPlayer.toString() + ", " + card.toString() + ", " + hasCard + ")";
        }
    }

    @Override
    // TODO equality doesn't care about guessing player at the moment which is kind of silly, but is needed to fit HashCode to work for solver in HashSet
    public boolean equals(Object obj) {
        if (obj.getClass()!=this.getClass()) {
            return false;
        }
        boolean guessPlayerEquality = ((ClueInfo) obj).guessingPlayer().equals(this.guessingPlayer());
        boolean guessEquality = ((ClueInfo) obj).guess().equals(this.guess());
        boolean revealingPlayerEquality = ((ClueInfo) obj).revealingPlayer().equals(this.revealingPlayer());
        boolean cardEquality = ((ClueInfo) obj).card().equals(this.card());
        boolean hasCardEquality = ((ClueInfo) obj).hasCard()==this.hasCard();
        return guessPlayerEquality && guessEquality && revealingPlayerEquality && cardEquality && hasCardEquality;
    }

    
    @Override
    public int hashCode() {
        return Objects.hash(guessingPlayer(), guess(), revealingPlayer(), card(), hasCard);
    }

    public String toStringLong() {
        if (card==null) {
            return guessingPlayer.toString() + " guessed " + guess.toString() + ". " + 
                   revealingPlayer.toString() + " had nothing to reveal.";
        }
        else {
            return guessingPlayer.toString() + " guessed " + guess.toString() + ". " + 
                   revealingPlayer.toString() + " revealed " + card.toString() + ".";
        }
    }

    /*
     * Creates a copy of ClueInfo, but with a new card for the guess as parameter passed
     * TODO add param
     */
    public ClueInfo copyWithNewCard(ClueCard newCard) {
        return new ClueInfo(guessingPlayer, guess, revealingPlayer, newCard);
    }

    public ClueInfo copyWithNewCard(ClueCard newCard, boolean hasCard) {
        return new ClueInfo(guessingPlayer, guess, revealingPlayer, newCard, hasCard);
    }

    public ClueInfo copyWithNewRevealingPlayer(CluePlayer newRevealingPlayer) {
        return new ClueInfo(guessingPlayer, guess, newRevealingPlayer, card, hasCard);
    }

    public ClueInfo copyWithOppositeHasCard() {
        return new ClueInfo(guessingPlayer, guess, revealingPlayer, card, !hasCard);
    }

    public CluePlayer guessingPlayer() {
        return guessingPlayer;
    }

    public CluePlayer revealingPlayer() {
        return revealingPlayer;
    }

    public ClueGuess guess() {
        return guess;
    }

    public ClueCard card() {
        return card;
    }

    public boolean hasCard() {
        return hasCard;
    }

    public static void main(String[] args) {
        ClueGame game = new ClueGame(3);
        CluePlayer[] players = game.getPlayers();

        ClueInfo info1 = new ClueInfo(players[0], new ClueGuess("peacock", "hall", "rope"), players[1], new ClueCard("hall"));
        ClueInfo info2 = new ClueInfo(players[2], new ClueGuess("peacock", "hall", "rope"), new CluePlayer(players[1].getName(), 3), new ClueCard("hall"));
        
        // NOTE: Equality is weird right now, not taking guessing player into account for the purpose of HashSets
        System.out.println(info1.equals(info2));
    }
}
