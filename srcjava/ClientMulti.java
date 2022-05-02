package srcjava;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class ClientMulti implements Runnable {
    private MulticastSocket socket;
    private boolean isRunning = true;
    private Fenetre fe;

    public ClientMulti(String ip, int port, Fenetre fe) throws IOException {
        this.fe = fe;
        socket = new MulticastSocket(port);
        socket.joinGroup(InetAddress.getByName(ip));
    }

    @Override
    public void run() {
        while(isRunning) {
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
    }

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
                break;
        }
    }

    public void resGhost(byte[] res, int len) {
        if(len != 16) {
            System.out.println("Error len recv multi GHOST");
            System.exit(1);
        }
        int x = Integer.parseInt(new String(res, 6, 3));
        int y = Integer.parseInt(new String(res, 10, 3));

        //TODO: Mettre à jour l'interface
    }

    public void resScore(byte[] res, int len) {
        if(len != 30) {
            System.out.println("Error len recv multi SCORE");
            System.exit(1);
        }
        String id = new String(res, 6, 8);
        int p = Integer.parseInt(new String(res, 15, 4));
        int x = Integer.parseInt(new String(res, 20, 3));
        int y = Integer.parseInt(new String(res, 24, 3));

        //TODO: Mettre à jour l'interface
    }

    public void resMessa(byte[] res, int len) {
        String id = new String(res, 6, 8);
        String mess = new String(res, 15, len - 15);
        mess = mess.substring(0, mess.length() - 3);

        //TODO: Mettre à jour l'interface
    }

    public void resEndGame(byte[] res, int len) {
        if(len != 22) {
            System.out.println("Error len recv multi ENDGA");
            System.exit(0);
        }

        String id = new String(res, 6, 8);
        int p = Integer.parseInt(new String(res, 15, 4));

        System.out.println("Le gagnant est " + id + " avec " + p + " points");

        isRunning = false;
        //TODO Afficher le gagnant sur l'IG

    }

}
