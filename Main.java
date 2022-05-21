import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import srcjava.*;

//Classe qui lance le client
public class Main {
    public static void main(String[] args) {
        //verification du nombre d'arguments
        if(args.length != 2) {
            System.out.println("IP + PORT STP !!!!");
            System.exit(0);
        }
        String addr = args[0];
        int port = Integer.parseInt(args[1]);

        //lance l'interface graphique si addr correspond bien a une adresse 
        try {
            new Fenetre(InetAddress.getByName(addr), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
