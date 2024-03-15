import java.io.*;
import java.net.*;
import java.util.*;

public class Casino {
    private static final int PORT = 9802;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        //creating tables
        Table table1 = new Table(9803);
        Table table2 = new Table(9804);
        Table table3 = new Table(9805);

        List<Table> tables = new ArrayList<>();
        tables.add(table1);
        tables.add(table2);
        tables.add(table3);

        table1.start();
        table2.start();
        table3.start();

        //login
        while(true){
            Socket player = null;
            try{
                player = serverSocket.accept();
                StartGameHandler startGameHandler = new StartGameHandler(tables, player);
                startGameHandler.start();
            }
            catch (Exception e){
                player.close();
                e.printStackTrace();
            }
        }
    }
}