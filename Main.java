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
            ClientTCP c = new ClientTCP(InetAddress.getByName(addr), port);
            new Thread(c).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
