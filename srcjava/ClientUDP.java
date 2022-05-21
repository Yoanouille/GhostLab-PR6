package srcjava;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

//CLasse qui gere le client UDP
public class ClientUDP implements Runnable {
    private DatagramSocket socket;
    private boolean isRunning = true;
    private Fenetre fe;
    public ClientUDP(Fenetre fe) {
        this.fe = fe;
        int port = 0;
        boolean good = false;
        while(!good) {
            good = true;
            port = (int)(Math.random() * (10000 - 1000)) + 1000;
            try {
                socket = new DatagramSocket(port);
            } catch (SocketException e) {
                good = false;
            }
        }
        System.out.println(port + " " + socket.getLocalPort());
    }

    //getter qui retourne le port
    public int getPort() {
        return socket.getLocalPort();
    }

    //fonction qui stop la connection UDP
    public void stop() {
        socket.close();
        System.out.println("J'essaie de fermer UDP");
        isRunning = false;
    }
    
    //Fonction du thread qui recoit les messages udp
    @Override
    public void run() {
        while(isRunning) {
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                parseMess(packet);

            } catch (IOException e) {
                System.out.println("End UDP");
                break;
            }
            if(socket.isClosed()) break;
        }
        System.out.println("FIN UDP");
    }

    //parseur des messages UDP recut
    public void parseMess(DatagramPacket packet) {
        byte[] res = packet.getData();
        int len = packet.getLength();
        String debut = new String(res, 0, 5);
        String mess = new String(res, 0, len);
        if(!mess.endsWith("+++")) {
            System.out.println("Error +++ UDP");
            return;
        }
        System.out.println(mess);
        switch(debut) {
            case "MESSP" :
                resMessp(res, len);
                break;
            
            case "TRAP!" :
                resTrap(res, len);
                break;
        }
    }

    //Fonction qui parse le message MESSP et actualise l'interface
    public void resMessp(byte[] res, int len) {
        String id = new String(res, 6, 8);
        String mess = new String(res, 15, len-15);
        mess = mess.substring(0, mess.length() - 3);

        System.out.println(id + " vous a envoyé " + mess);
        fe.add_messageP(id, mess);

    }

    //Fonction qui parse le message TRAP! et actualise l'interface
    public void resTrap(byte[] res, int len) {
        if(len != 21) {
            System.out.println("Error len rcv TRAP!");
            return;
        }
        int x = Integer.parseInt(new String(res, 6, 3));
        int y = Integer.parseInt(new String(res, 10, 3));
        int p = Integer.parseInt(new String(res, 14, 4));

        fe.set_my_score(p);
        fe.addTrap(x, y);
    }

}