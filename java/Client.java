package java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;






//Peut être changer comment ça marche !
//D'abord read le GAMES n, puis les n prochains OGAME
//Puis Ensuite Avec l'interface, il envoie une req (parmis celle dispo par l'interface) 
//Puis attend la réponse du serveur qui est adapté à la requête envoyé
//  Chaque reponse reçue (EN TCP) est de taille fixe et puis pas mal de fois où un paquet dit tu vas recevoie n paquet de ce type
//Donc Faire une fonction pour chaque couple req/res avec les sous reponses qui arrivent
//Ces fonctions pourront changer les variables globales + actualiser l'interface

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

    public void fill(byte[] data, int begin, String s) {
        for(int i = begin; i < data.length && i < begin + s.length(); i++) {
            data[i] = (byte) s.charAt(i - begin);
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
        String deb = new String(req, 0, 5);
        switch(deb) {
            case "GAMES" : 
                resGamesN(req, len);
                break;
            case "OGAME" :
                resOGamesMS(req, len);
                break;
            
            case "REGOK" :
                resRegOK(req, len);
                break;
            
            case "REGNO" :
                resRegNo(req, len);
                break;
            // case "UNROK" :
            //     resUnRegOK(req, len);
            //     break;
            
            // case "DUNNO" :
            //     resDunno(req, len);
            //     break;
            
            // case "SIZE!" :
            //     resSize(req, len);
            //     break;
            
            // case "LIST!" :
            
        }
    }

    public void resGamesN(byte[] req, int len) {
        if(len != 7) {
            print_byte(req, len);
            //TODO ERROR;
            return;
        }
        count = req[6];
        System.out.println("Il y a " + req[6] + " parties !");

    }

    public void resOGamesMS(byte[] req, int len) {
        if(len != 9) {
            print_byte(req, len);
            //TODO ERROR;
            return;
        }
        System.out.println("Partie " + req[6] + " : " + req[8] + " joueur(s)");
        count--;
        if(count == 0) {
            //Actualiser l'interface graphique
        }
    }

    public void resRegOK(byte[] req, int len) {
        System.out.println("Vous avez été inscrit dans la partie " + req[6]);
        //TODO Actualiser l'interface
    }

    public void resRegNo(byte[] req, int len) {
        System.out.println("Impossible de s'inscrire dans la parite voulue");
    }

    public void reqNewPL(String id, int port) throws IOException {
        byte[] data = new byte[5 + 1 + 8 + 1 + 4 + 3];
        fill(data, 0, "NEWPL ");
        fill(data, 5 + 1, id);
        fill(data, 5 + 1 + 8, " " + Integer.toString(port));
        fill(data, 5 + 1 + 8 + 1 + 4, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqRegis(String id, int port, int game) throws IOException {
        byte[] data = new byte[5 + 1 + 8 + 1 + 4 + 1 + 1 + 3];
        fill(data, 0, "REGIS ");
        fill(data, 5 + 1, id);
        fill(data, 5 + 1 + 8, " " + Integer.toString(port) + " ");
        data[5 + 1 + 8 + 1 + 4 + 1] = (byte) game;
        fill(data, 5 + 1 + 8 + 1 + 4 + 1 + 1, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqStart() throws IOException {
        byte[] data = new byte[5 + 3];
        fill(data, 0, "START***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqUnReg() throws IOException {
        byte[] data = new byte[5 + 3];
        fill(data, 0, "UNREG***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
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