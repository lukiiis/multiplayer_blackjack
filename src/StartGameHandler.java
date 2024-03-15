import database.AccountDAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class StartGameHandler extends Thread{
    private List<Table> tablesList;
    private Socket player;
    private static final AccountDAO dao = new AccountDAO();

    public StartGameHandler(List<Table> tablesList, Socket player){
        this.tablesList = tablesList;
        this.player = player;
    }

    @Override
    public void run() {
        try{
            DataInputStream dis = new DataInputStream(player.getInputStream());
            DataOutputStream dos = new DataOutputStream(player.getOutputStream());
            //login
            dos.writeUTF("Login: ");
            String username = dis.readUTF();

            //password
            if(dao.accountExists(username)){
                dos.writeUTF("Password: ");
                String password = dis.readUTF();
                while(true){

                    if(dao.getPassword(username).equals(password)) {
                        break;
                    }
                    else{
                        dos.writeUTF("Wrong password, enter your password again: ");
                        password = dis.readUTF();
                    }
                }
            }
            else{
                dos.writeUTF("Creating an account, create password: ");
                String password = dis.readUTF();
                dao.addToDB(username, password, 1000);
            }


            String money = Integer.toString(dao.getMoney(username));
            String rank = "Your balance: "+money+"\n"+"Top 5 players:" + "\n";
            for (var x : dao.getTopRanking(username)) {
                rank += x + "\n";
            }

            dos.writeUTF(rank);

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}
