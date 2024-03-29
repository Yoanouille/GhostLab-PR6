package srcjava;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientMulti implements Runnable {
    private MulticastSocket socket;
    private boolean isRunning = true;
    private Fenetre fe;

    public ClientMulti(String ip, int port, Fenetre fe) throws IOException {
        this.fe = fe;
        socket = new MulticastSocket(port);
        System.out.println(port);
        System.out.println(ip);
        String newIp = "";
        for(int i = 0; i < ip.length(); i++) {
            if(ip.charAt(i) != '#') newIp += ip.substring(i, i+1);
            else break;
        }
        System.out.println(newIp);
        //on abonne la socket à l'adresse de multi-diffusion
        socket.joinGroup(InetAddress.getByName(newIp));
    }

    //Fonction du thread qui recoit les messages udp multi-diffuse
    @Override
    public void run() {
        while(isRunning) {
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                parseMess(packet);

            } catch (IOException e) {
                System.out.println("Erreur Multi");
                break;
            }
        }     
        System.out.println("FIN MULTI");   
    }

    //parseur des messages UDP recut
    public void parseMess(DatagramPacket packet) {
        byte[] res = packet.getData();
        int len = packet.getLength();
        String debut = new String(res, 0, 5);
        String mess = new String(res, 0, len);
        if(!mess.endsWith("+++")) {
            System.out.println("Error +++ packet !");
            return;
        }
        switch(debut) {
            case "GHOST" :
                resGhost(res, len);
                break;
            case "SCORE" :
                resScore(res, len);
                break;
            case "MESSA" :
                resMessa(res, len);
                break;
            case "ENDGA" :
                resEndGame(res, len);
                break;
        }
    }

    //fonction qui parse le message Ghost et actualise l'interface
    public void resGhost(byte[] res, int len) {
        if(len != 16) {
            System.out.println("Error len recv multi GHOST");
            return;
        }
        int x = Integer.parseInt(new String(res, 6, 3));
        int y = Integer.parseInt(new String(res, 10, 3));

        fe.drawGhost(x, y);
    }

    //fonction qui parse le message Score et actualise l'interface
    public void resScore(byte[] res, int len) {
        if(len != 30) {
            System.out.println("Error len recv multi SCORE");
            return;
        }
        String id = new String(res, 6, 8);
        int p = Integer.parseInt(new String(res, 15, 4));
        int x = Integer.parseInt(new String(res, 20, 3));
        int y = Integer.parseInt(new String(res, 24, 3));

        fe.set_player_score(id,p);
    }

    //fonction qui parse le message MESSA et actualise l'interface
    public void resMessa(byte[] res, int len) {
        String id = new String(res, 6, 8);
        String mess = new String(res, 15, len - 15);
        mess = mess.substring(0, mess.length() - 3);
        System.out.println(id +": "+mess);

        fe.add_message(id, mess);
    }

    //fonction qui parse le message ENDGA et actualise l'interface
    public void resEndGame(byte[] res, int len) {
        if(len != 22) {
            System.out.println("Error len recv multi ENDGA");
            return;
        }

        String id = new String(res, 6, 8);
        int p = Integer.parseInt(new String(res, 15, 4));

        System.out.println("Le gagnant est " + id + " avec " + p + " points");

        isRunning = false;
        fe.setEnd("Le gagnant est " + id + " avec " + p + " points");
        fe.sendQuit();
        fe.stopUdp();
    }

    //focntion pour arreter la boucle while du run
    public void stop(){
        this.isRunning = false;
    }

}
