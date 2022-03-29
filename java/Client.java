package java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    
    private Socket socket;
    private boolean running = true;

    private int count = 0;

    public Client(InetAddress addr, int port) {
        try {
            socket = new Socket(addr, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        System.out.println("Client Running !");

        InputStream is = socket.getInputStream();
        
        byte[] data = new byte[256];

        boolean pre = false;
        boolean prepre = false;

        byte[] requete = new byte[256];
        int x = 0;

        while(running) {
            int re = is.read(data, 0, 256);
            for(int i = 0; i < re; i++) {
                if(data[i] == '*') {
                    if(prepre) {
                        traitement(requete, x);
                        x = 0;
                        prepre = false;
                        pre = false;
                    } else if(pre) prepre = true; 
                    else pre = true;
                } else {
                    pre = false;
                    prepre = false;
                    requete[x] = data[i];
                    x++;
                }
            }
        }

    }

    public void print_byte(byte[] req, int len) {
        for(int i = 0; i < len; i++) {
            System.out.print(req[i]);
        }
        System.out.println();
    }

    public void traitement(byte[] req, int len) {
        byte[] debut = new byte[5];
        for(int i = 0; i < 5; i++) {
            debut[i] = req[i];
        }
        String deb = new String(debut);
        switch(deb) {
            case "GAMES" : 
                if(len == 7) reqGamesN(req, len);
                else if(len == 9) reqGamesMS(req, len);
                else {
                    print_byte(req, len);
                    //TODO: error
                }
                break;
        }
    }

    public void reqGamesN(byte[] req, int len) {
        count = req[6];
        System.out.println("Il y a " + req[6] + " parties !");

    }

    public void reqGamesMS(byte[] req, int len) {
        System.out.println("Partie " + req[6] + " : " + req[8] + " joueur(s)");
        count--;
        if(count == 0) {
            //Envoyer un requête
        }
    }

    public static void main(String[] args) {
        if(args.length != 0) {
            System.out.println("IP + PORT STP !!!!");
            System.exit(0);
        }
        String addr = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Client c = new Client(InetAddress.getByName(addr), port);
            c.run();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}