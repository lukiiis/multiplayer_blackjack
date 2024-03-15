import java.io.*;
import java.net.*;
import java.util.Scanner;

public class User {
    private static final int MAINSERVER = 9802;

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Welcome to casino.");
            String tosendBeforeWhile;
            String whileReceived;

            InetAddress serverAddress = InetAddress.getByName("localhost");

            Scanner scn = new Scanner(System.in);

            Socket socketMain = new Socket(serverAddress, MAINSERVER);
            DataInputStream disMain = new DataInputStream(socketMain.getInputStream());
            DataOutputStream dosMain = new DataOutputStream(socketMain.getOutputStream());

            //login
            System.out.println(disMain.readUTF());
            tosendBeforeWhile = scn.nextLine();
            String login = tosendBeforeWhile;
            dosMain.writeUTF(tosendBeforeWhile);

            //password
            System.out.println(disMain.readUTF());
            tosendBeforeWhile = scn.nextLine();
            dosMain.writeUTF(tosendBeforeWhile);


            String beforeBadPassword = disMain.readUTF();

            while(true){
                System.out.println(beforeBadPassword);
                if(beforeBadPassword.equals("Wrong password, enter your password again: ")){
                    tosendBeforeWhile = scn.nextLine();
                    dosMain.writeUTF(tosendBeforeWhile);

                    beforeBadPassword = disMain.readUTF();
                }
                else {
                    break;
                }
            }

            Socket socketGame = null;
            while (true) {
                try {
                    System.out.println("Choose the table from 1 to 3: ");
                    int TablePort = 0;
                    while (true) {
                        String table = reader.readLine();
                        if (table.equals("1")) {
                            TablePort = 9803;
                            break;
                        } else if (table.equals("2")) {
                            TablePort = 9804;
                            break;
                        } else if (table.equals("3")) {
                            TablePort = 9805;
                            break;
                        } else {
                            System.out.println("Choose the table from 1 to 3: ");
                        }
                    }

                    try {
                        int timeoutMs = 3000;
                        socketGame = new Socket();
                        socketGame.connect(new InetSocketAddress(serverAddress, TablePort), timeoutMs);
                        break;
                    } catch (SocketTimeoutException e) {
                        System.out.println("Table is full, choose another one.");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            DataInputStream disGame = new DataInputStream(socketGame.getInputStream());
            DataOutputStream dosGame = new DataOutputStream(socketGame.getOutputStream());

            while(true) {
                //sending login to the table
                dosGame.writeUTF(login);
                //bet
                System.out.println("Waiting for the game...");
                System.out.println(disGame.readUTF());
                tosendBeforeWhile = reader.readLine();
                dosGame.writeUTF(tosendBeforeWhile);

                String beforeBadBet = disGame.readUTF();

                while (true) {
                    System.out.println(beforeBadBet);
                    if (beforeBadBet.equals("Not enough money, please bet smaller amount: ")) {
                        tosendBeforeWhile = reader.readLine();
                        dosGame.writeUTF(tosendBeforeWhile);

                        beforeBadBet = disGame.readUTF();
                    } else {
                        break;
                    }
                }
                while (true) {
                    //game
                    whileReceived = disGame.readUTF();
                    System.out.println(whileReceived);

                    if (whileReceived.equals("You finished playing, wait for the results") ||
                            whileReceived.equals("Oops, too much points, wait for the results") ||
                            whileReceived.equals(" ")) {
                        System.out.println(disGame.readUTF());
                        break;
                    }
                    int toSend = scn.nextInt();
                    dosGame.writeInt(toSend);
                    //cards and points
                    System.out.println(disGame.readUTF());
                }

                System.out.println("Do you want to play more?" + "0 - stop playing, 1 - play again");
                String decision;
                decision = reader.readLine();
                dosGame.writeUTF(decision);

                if(decision.equals("0")){
                    break;
                }
                else if(decision.equals("1")){
                    String decisionReceived = disGame.readUTF();
                    if(decisionReceived.equals("Opponent left, leaving the table...")){
                        System.out.println(decisionReceived);
                        break;
                    }
                    else if(decisionReceived.equals("Starting the next round...")){
                        System.out.println(decisionReceived);
                    }
                }

            }
            System.out.println("Thank you for the game, see you again!");

            scn.close();
            disGame.close();
            dosGame.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}