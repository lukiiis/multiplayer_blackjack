import database.AccountDAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket client;
    final List<Socket> allPlayers;

    //player info
    private String username;
    private int points;
    private String cards;
    private int bet;
    private List<Card> userCards;
    private Deck deck;
    private boolean endRound = false;
    private final AccountDAO dao = new AccountDAO();


    public ClientHandler() {
        dis = null;
        dos = null;
        client = null;
        allPlayers = null;
    }

    public ClientHandler(Socket client, DataInputStream dis, DataOutputStream dos, List<Socket> allPlayers, Deck deck)
    {
        this.client = client;
        this.dis = dis;
        this.dos = dos;
        this.allPlayers = allPlayers;
        this.points = 0;
        this.cards = "";
        this.userCards = new ArrayList<>();
        this.deck = deck;
    }

    @Override
    public void run(){
        int received;
        String toSend;
        try {

            username = dis.readUTF();
            //bet
            dos.writeUTF("Place bet: ");
            String bet = dis.readUTF();
            while(true){
                int dbMoney = dao.getMoney(username);

                if(Integer.parseInt(bet) > dbMoney){
                    dos.writeUTF("Not enough money, please bet smaller amount: ");
                    bet = dis.readUTF();
                }
                else{
                    setBet(Integer.parseInt(bet));
                    break;
                }
            }
            //drawing starting cards and points
            userCards.add(deck.DrawCard());
            userCards.add(deck.DrawCard());

            countPoints();

            //wysylanie kart na rece i punktow na rece
            dos.writeUTF(cardsString() + "\nYour starting points:" + points);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            try {
                dos.writeUTF("What do you want to do (1 - draw a card, 2 - stop)");
                received = dis.readInt();
                if (received == 1){
                    userCards.add(deck.DrawCard());
                    countPoints();
                    dos.writeUTF(cardsString() + "\nPoints: " + points);
                    if(points > 21){
                        endRound = true;
                        dos.writeUTF("Oops, too much points, wait for the results");
                        break;
                    }
                } else if (received == 2) {
                    endRound = true;
                    dos.writeUTF("You finished playing, wait for the results");
                    dos.writeUTF(" ");
                    break;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String cardsString(){
        String cardsToString = "Your cards: ";
        for (var card: userCards) {
            cardsToString += card.toString() + " ";
        }
        return cardsToString;
    }

    private void countPoints() {
        this.points = 0;
        for (var n : this.userCards) {
            this.points += n.value;
        }
    }


    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public boolean isEndRound() {
        return endRound;
    }

    public void setEndRound(boolean endRound) {
        this.endRound = endRound;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username=username;
    }
}
