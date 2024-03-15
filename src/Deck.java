import java.util.ArrayList;

public class Deck {
    public Deck() {
        Cards = new ArrayList<Card>();
    }

    public ArrayList<Card> Cards;
    public void resetDeck() {
        Cards.clear();
        createDeck();
    }
    public void createDeck() {
        for (int j = 0; j < 4; j++) {
            for (int i = 2; i < 11; i++) {
                String color = "";
                switch (j) {
                    case 0:
                        color = "spade";
                        break;
                    case 1:
                        color = "diamond";
                        break;
                    case 2:
                        color = "club";
                        break;
                    case 3:
                        color = "heart";
                        break;
                }
                Cards.add(new Card(i, color));
                if (i == 2 || i == 3 || i == 4 || i == 5) {
                    switch (i) {
                        case 2 -> Cards.add(new Card(i, color, "JACK"));
                        case 3 -> Cards.add(new Card(i, color, "QUEEN"));
                        case 4 -> Cards.add(new Card(i, color, "KING"));
                        case 5 -> Cards.add(new Card(11, color, "ACE"));
                        default -> {
                            return;
                        }
                    }
                }
            }
        }
    }
    public Card DrawCard() {
        if (Cards.size() == 0)
            resetDeck();

        int random = (int) (Math.random() * Cards.toArray().length);
        var drawn = Cards.remove(random);

        return drawn;
    }
}