import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import srcjava.*;

public class Main {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("IP + PORT STP !!!!");
            System.exit(0);
        }
        String addr = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            // Client c = new Client(InetAddress.getByName(addr), port);
            // //Fenetre f = new Fenetre(c);
            // c.resGames();
            // c.reqNewPL("JEANCLAU", 7777);
            // c.reqGame();
            ClientV2 c = new ClientV2(InetAddress.getByName(addr), port);
            new Thread(c).start();
            c.reqNewPL("JEANCLAU", 7777);
            c.reqGame();
            c.reqSize(0);
            c.reqSize(1);
            c.reqRegis("Jules", 7777, 0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
