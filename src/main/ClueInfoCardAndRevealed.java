package main;

import java.util.Set;
import java.util.HashSet;

// TODO can delete this class I believe
public class ClueInfoCardAndRevealed {
    ClueCard card;
    boolean hasCard;
    
    public ClueInfoCardAndRevealed(ClueCard card, boolean hasCard) {
        this.card = card;
        this.hasCard = hasCard;
    }

    public ClueInfoCardAndRevealed(ClueInfo info) {
        this.card = info.card();
        this.hasCard = info.hasCard();
    }

    public static Set<ClueInfoCardAndRevealed> buildSet(Set<ClueInfo> infoSet) {
        Set<ClueInfoCardAndRevealed> out = new HashSet<>();
        for (ClueInfo info: infoSet) {
            out.add(new ClueInfoCardAndRevealed(info));
        }
        return out;
    }

    public ClueCard getCard() {
        return card;
    }

    public boolean hasCard() {
        return hasCard;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass()!=this.getClass()) {
            return false;
        }
        boolean cardEquality = ((ClueInfoCardAndRevealed) obj).card.equals(this.card);
        boolean hasCardEquality = ((ClueInfoCardAndRevealed) obj).hasCard == this.hasCard;

        return cardEquality && hasCardEquality;
    }

    @Override
    public int hashCode() {
        return card.hashCode() + Boolean.hashCode(hasCard);
    }
}
