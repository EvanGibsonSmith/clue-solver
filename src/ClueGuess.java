import java.util.Set;
import java.util.HashSet;

// TODO document
public class ClueGuess {
    ClueCard player;
    ClueCard room;
    ClueCard weapon;
    Set<ClueCard> cardSet = new HashSet<>();

    public ClueGuess(String player, String room, String weapon) {
        this.player = new ClueCard(player);
        this.room = new ClueCard(room);
        this.weapon = new ClueCard(weapon);
        populateCardSet();
    }

    public ClueGuess(ClueCard player, ClueCard room, ClueCard weapon) {
        if (player.getType()!="player" | room.getType()!="roon" | weapon.getType()!="weapon") {
            throw new RuntimeException("Types of inputted cards much match player, room, weapon.");
        }
        this.player = player;
        this.room = room;
        this.weapon = weapon;
        populateCardSet();
    }

    @Override 
    public String toString() {
        return player.toString() + ", " + room.toString() + ", " + weapon.toString();
    }

    private void populateCardSet() {
        cardSet.add(this.player);
        cardSet.add(this.room);
        cardSet.add(this.weapon);
    }

    public Set<ClueCard> getCardSet() {
        return cardSet;
    }
}
