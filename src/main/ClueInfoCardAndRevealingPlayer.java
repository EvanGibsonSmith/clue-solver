package main;

import java.util.Set;
import java.util.HashSet;

// TODO again refactoring between these classes could definitely be done
public class ClueInfoCardAndRevealingPlayer {
    ClueCard card;
    boolean hasCard;
    CluePlayer revealingPlayer;
    
    public ClueInfoCardAndRevealingPlayer(CluePlayer revealingPlayer, ClueCard card, boolean hasCard) {
        this.card = card;
        this.hasCard = hasCard;
        this.revealingPlayer = revealingPlayer;
    }

    public ClueInfoCardAndRevealingPlayer(ClueInfo info) {
        this.card = info.card();
        this.hasCard = info.hasCard();
        this.revealingPlayer = info.revealingPlayer();
    }

    public static Set<ClueInfoCardAndRevealingPlayer> buildSet(Set<ClueInfo> infoSet) {
        Set<ClueInfoCardAndRevealingPlayer> out = new HashSet<>();
        for (ClueInfo info: infoSet) {
            out.add(new ClueInfoCardAndRevealingPlayer(info));
        }
        return out;
    }

    public ClueCard getCard() {
        return card;
    }

    public boolean hasCard() {
        return hasCard;
    }

    /*
     * Used in contradiction type 2 of SolverTree prune for 
     */
    public void flipHasCard() {
        hasCard = !hasCard;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass()!=this.getClass()) {
            return false;
        }
        boolean cardEquality = ((ClueInfoCardAndRevealingPlayer) obj).card.equals(this.card);
        boolean hasCardEquality = ((ClueInfoCardAndRevealingPlayer) obj).hasCard == this.hasCard;
        boolean revealingPlayerEquality = ((ClueInfoCardAndRevealingPlayer) obj).revealingPlayer.equals(this.revealingPlayer);
        return cardEquality && hasCardEquality && revealingPlayerEquality;
    }

    @Override
    public int hashCode() {
        return card.hashCode() + Boolean.hashCode(hasCard) + revealingPlayer.hashCode();
    }
}
