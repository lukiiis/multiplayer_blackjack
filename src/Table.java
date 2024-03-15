import database.AccountDAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Table extends Thread{
    private final int PORT;
    private final int MAINSERVERPORT = 9802;
    private static int player_count = 0;
    private static Deck deck = new Deck();
    private ServerSocket serverSocket = null;

    public Table(int PORT) {
        this.PORT = PORT;
        try{
            this.serverSocket = new ServerSocket(PORT);
            deck.createDeck();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        List<ClientHandler> clientsList = new ArrayList<>();
        List<Socket> players = new ArrayList<>();

        while(true)
        {
            deck.resetDeck();
            try{
                String decision0;
                String decision1;
                    if(players.size() < 2){
                        Socket player = null;
                        player = serverSocket.accept();
                        players.add(player);

                        System.out.println("Player joined: " + player);
                        player_count++;
                    }

                    if (players.size() == 2) {
                        for (var gracz : players) {
                            DataInputStream dis = new DataInputStream(gracz.getInputStream());
                            DataOutputStream dos = new DataOutputStream(gracz.getOutputStream());

                            System.out.println("Creating player thread: " + gracz);

                            ClientHandler client = new ClientHandler(gracz, dis, dos, players, deck);
                            clientsList.add(client);

                            client.start();
                        }
                        //waiting for handler threads to end
                        try {
                            for (var client : clientsList) {
                                client.join();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // counting points and sending results
                        if (clientsList.stream().allMatch(ClientHandler::isEndRound)) {
                            AccountDAO dao = new AccountDAO();
                            System.out.println("Counting points...");
                            int winnerPoints = 0;
                            for (var c : clientsList) {
                                if (c.getPoints() <= 21 && c.getPoints() > winnerPoints) {
                                    winnerPoints = c.getPoints();
                                }
                            }
                            if (winnerPoints == 0) {
                                clientsList.get(0).getDos().writeUTF("Draw, every player has got too many points");
                                clientsList.get(1).getDos().writeUTF("Draw, every player has got too many points");
                            } else if (clientsList.get(0).getPoints() == clientsList.get(1).getPoints()) {
                                clientsList.get(0).getDos().writeUTF("Draw, every player has got the same amount of points: " + winnerPoints);
                                clientsList.get(1).getDos().writeUTF("Draw, every player has got the same amount of points: " + winnerPoints);
                            } else if (winnerPoints > 0 && clientsList.get(0).getPoints() != clientsList.get(1).getPoints()) {
                                ClientHandler winner = null;
                                ClientHandler loser = null;
                                for (var x : clientsList) {
                                    if (x.getPoints() == winnerPoints) winner = x;
                                    else if (x.getPoints() != winnerPoints) loser = x;
                                }
                                winner.getDos().writeUTF("Congratulations, you won with the following number of points: " + winnerPoints);
                                loser.getDos().writeUTF("You lost, your opponent won with the following number of points: " + winnerPoints);
                                dao.alterMoney(winner.getUsername(), loser.getBet());
                                dao.alterMoney(loser.getUsername(), -(loser.getBet()));
                            }
                            System.out.println(winnerPoints);
                        }
                        // getting choice of players if they play again
                        decision0 = clientsList.get(0).getDis().readUTF();

                        decision1 = clientsList.get(1).getDis().readUTF();

                        if ((decision0.equals("0") && decision1.equals("0")) ||
                                (decision0.equals("1") && decision1.equals("0")) ||
                                (decision0.equals("0") && decision1.equals("1"))) {
                            if(decision0.equals("0") && decision1.equals("0")){
                                System.out.println("Game over, clearing the table...");
                            }
                            else if(decision0.equals("1") && decision1.equals("0")){
                                clientsList.get(0).getDos().writeUTF("Opponent left, leaving the table...");
                            }
                            else if(decision0.equals("0") && decision1.equals("1")){
                                clientsList.get(1).getDos().writeUTF("Opponent left, leaving the table...");
                            }
                            players.get(0).close();
                            players.get(1).close();
                            players.clear();
                            clientsList.clear();
//                            break;
                        }
                        else if(decision0.equals("1") && decision1.equals("1")){
                            clientsList.get(0).getDos().writeUTF("Starting the next round...");
                            clientsList.get(1).getDos().writeUTF("Starting the next round...");
                            clientsList.clear();
                        }
                    }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
