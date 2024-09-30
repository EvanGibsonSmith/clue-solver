package main;


import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.Collections;
import java.util.Stack;

// NOTE: at the moment incomplete because it is not needed for the clue solver
public class ClueDeck {
    Stack<ClueCard> cards = new Stack<>();
    int totalSize;
    Random random;
    Set<ClueCard> envelope = new HashSet<>(); // set of size 3, representing the correct answer

    public ClueDeck() {
        this.random = new Random();

        // add all cards to array 
        String[] rooms = {"conservatory", "dining", "ballroom", "study", "hall", "lounge", "library", "billiard"};
        String[] people = {"scarlett", "mustard", "white", "green", "peacock", "plum"};
        String[] weapons = {"candlestick", "knife", "lead pipe", "pistol", "rope", "wrench"};
        this.totalSize = rooms.length + people.length + weapons.length;

        // select cards to go into envelope (correct room, person, and weapon)
        String correctPerson = rooms[random.nextInt(people.length)]; 
        envelope.add(new ClueCard(correctPerson));

        String correctRoom = rooms[random.nextInt(rooms.length)]; 
        envelope.add(new ClueCard(correctRoom));

        String correctWeapon = rooms[random.nextInt(weapons.length)]; 
        envelope.add(new ClueCard(correctWeapon));
        
        // populate people
        for (String person: people) {
            if (person!=correctPerson) {
                cards.add(new ClueCard(person));
            }
        }

        // populate rooms
        for (String room: rooms) {
            if (room!=correctRoom) {
                cards.add(new ClueCard(room));
            }
        }

        // populate weapons
        for (String weapon: weapons) {
            if (weapon!=correctWeapon) {
                cards.add(new ClueCard(weapon));
            }
        }

        // shuffle cards
        Collections.shuffle(cards); 

        dealCards();
    }

    // TODO complete, deals cards to players and to center of board.
    private void dealCards() {

    }

    // TODO document, draws a random card
    public ClueCard drawCard() { // draws a random card
        return cards.pop();
    }

}
