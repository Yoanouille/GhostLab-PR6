package srcjava;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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

    public int getPort() {
        return socket.getLocalPort();
    }
    
    @Override
    public void run() {
        while(isRunning) {
            System.out.println("UDP");
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                parseMess(packet);

            } catch (IOException e) {
                System.out.println("End UDP");
            }
            if(socket.isClosed()) break;
        }
        System.out.println("FIN UDP");
    }

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

    public void resMessp(byte[] res, int len) {
        String id = new String(res, 6, 8);
        String mess = new String(res, 15, len);
        mess = mess.substring(0, mess.length() - 3);

        System.out.println(id + " vous a envoyé " + mess);
        //TODO: afficher sur l'interface
    }

    public void resTrap(byte[] res, int len) {
        if(len != 21) {
            System.out.println("Error len rcv TRAP!");
            return;
        }
        int x = Integer.parseInt(new String(res, 6, 3));
        int y = Integer.parseInt(new String(res, 10, 3));

        fe.addTrap(x, y);
    }

}