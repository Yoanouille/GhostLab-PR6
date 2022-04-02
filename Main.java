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
            Client c = new Client(InetAddress.getByName(addr), port);
            Fenetre f = new Fenetre(c);
            //c.run();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}