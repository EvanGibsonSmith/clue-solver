package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;

public class ClueCard {
    Map<String, String> VALUES_TO_TYPE;

    static final String[] defaultRooms = {"conservatory", "dining", "ballroom", "study", "hall", "lounge", "library", "billiard"};
    static final String[] defaultPeople = {"scarlett", "mustard", "white", "green", "peacock", "plum"};
    static final String[] defaultWeapons = {"candlestick", "knife", "lead pipe", "pistol", "rope", "wrench"};

    private void setupValuesToType(String[] people, String[] rooms, String[] weapons) {
        Map<String, String> tempSet = new HashMap<>();
        for (String person: people) {
            tempSet.put(person, "person");
        }
        for (String room: rooms) {
            tempSet.put(room, "room");
        }
        for (String weapon: weapons) {
            tempSet.put(weapon, "weapon");
        }

        VALUES_TO_TYPE = Collections.unmodifiableMap(tempSet);
    }

    String type;
    String value;

    public ClueCard(String value) {
        this(value, defaultPeople, defaultRooms, defaultWeapons);
    }


    // TODO if this gets too clunky an object of valid types people, rooms weapons could be made.
    public ClueCard(String value, String[] people, String[] rooms, String[] weapons) {
        setupValuesToType(people, rooms, weapons);
        if (!VALUES_TO_TYPE.keySet().contains(value)) {
            throw new RuntimeException("Value of card " + value + " was not in set of valid cards");
        }
        this.value = value;
        this.type = VALUES_TO_TYPE.get(value);
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return type.hashCode() + value.hashCode() + VALUES_TO_TYPE.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ClueCard)) {
            return false;
        }
        return ((this.value.equals(((ClueCard) other).value)) && (this.type.equals(((ClueCard) other).type)));
    }

    public static void main(String[] args) {
        // test equals, TODO can make more rigorous later
        ClueCard card1 = new ClueCard("scarlett");
        ClueCard card2 = new ClueCard("scarlett");
        ClueCard card3 = new ClueCard("hall");
        ClueCard card4 = new ClueCard("pistol");
        System.out.println(card1.equals(card2));
        System.out.println(card1.hashCode());
        System.out.println(card2.hashCode());

        HashSet<ClueCard> set = new HashSet<>();
        set.add(card1);
        set.add(card3);
        set.add(card4);
        set.add(card2);
        System.out.println(set.size());

        System.out.println(set.contains(card2));

    }
}
