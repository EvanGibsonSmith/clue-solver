package main;



public class ClueHand {
    ClueCard[] clueHand;
    int handIndex = 0;
    int handSize;

    public ClueHand(int handSize) {
        clueHand = new ClueCard[handSize];
        this.handSize = handSize;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i=0; i<handIndex; ++i) { // TODO fix printing null cards
            out += clueHand[i].toString() + ", ";
        }
        return out.substring(0, out.length()-2);
    }

    public int size() {
        return handIndex;
    }


    public int maxSize() {
        return handSize;
    }

    public int addCard(ClueCard newCard) {
        if (handIndex<clueHand.length) {
            clueHand[handIndex] = newCard;
            ++handIndex;
            return 0;
        }
        return -1;
    }

    public ClueCard[] getCards() {
        return clueHand;
    }
}
