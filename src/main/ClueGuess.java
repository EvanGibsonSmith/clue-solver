package main;

import java.util.Set;
import java.util.HashSet;

public class ClueGuess {
    ClueCard person;
    ClueCard room;
    ClueCard weapon;
    Set<ClueCard> cardSet = new HashSet<>(); 

    public ClueGuess(String person, String room, String weapon) {
        this.person = new ClueCard(person); // has default allowable people rooms and weapons
        this.room = new ClueCard(room);
        this.weapon = new ClueCard(weapon);
        populateCardSet();
    }

    public ClueGuess(String person, String room, String weapon, String[] people, String[] rooms, String[] weapons) {
        this.person = new ClueCard(person, people, rooms, weapons);
        this.room = new ClueCard(room, people, rooms, weapons);
        this.weapon = new ClueCard(weapon, people, rooms, weapons);
        populateCardSet();
    }

    public ClueGuess(ClueCard person, ClueCard room, ClueCard weapon) {
        if (!person.getType().equals("person") | !room.getType().equals("room") | !weapon.getType().equals("weapon")) {
            throw new RuntimeException("Types of inputted cards must match person, room, weapon.");
        }
        this.person = person;
        this.room = room;
        this.weapon = weapon;
        populateCardSet();
    }

    @Override 
    public String toString() {
        return person.toString() + ", " + room.toString() + ", " + weapon.toString();
    }

    public ClueCard[] cardArray() {
        return new ClueCard[] {person, room, weapon};
    }

    @Override 
    public boolean equals(Object other) {
        if (other.getClass()!=this.getClass()) {
            return false;
        }
        return ((ClueGuess) other).cardSet.equals(this.cardSet);
    }

    @Override 
    public int hashCode() {
        return this.cardSet.hashCode();
    }

    private void populateCardSet() {
        cardSet.add(this.person);
        cardSet.add(this.room);
        cardSet.add(this.weapon);
    }

    public Set<ClueCard> getCardSet() {
        return cardSet;
    }
}
