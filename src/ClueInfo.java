public class ClueInfo {
    CluePlayer guessingPlayer;
    CluePlayer revealingPlayer;
    ClueGuess guess;
    ClueCard card;
    boolean hasCard = true; // if true, info reveals that player HAS this card, if false, reveals player does NOT have this card

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
}
