package srcjava;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientV2 implements Runnable {
    private Socket socket;

    private boolean running = false;

    private final static int buff_size = 256;

    public ClientV2(InetAddress addr, int port) throws IOException {
        socket = new Socket(addr, port);
    }

    @Override
    public void run() {
        running = true;

        byte[] buf = new byte[buff_size];
        byte[] req = new byte[buff_size];

        boolean pre = false;
        boolean prepre = false;
        int len = 0;

        while(running) {
            try {
                int re = socket.getInputStream().read(buf, 0, buff_size);
                for(int i = 0; i < re; i++) {
                    req[len] = buf[i];
                    len++;
                    if(buf[i] == '*') {
                        if(prepre) {
                            parseReq(req, len);
                            len = 0;
                            pre = false;
                            prepre = false;
                        }
                        else if (pre) prepre = true;
                        else pre = true; 
                    } else {
                        prepre = false;
                        pre = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fill(byte[] data, int begin, String s) {
        for(int i = begin; i < data.length && i < begin + s.length(); i++) {
            data[i] = (byte) s.charAt(i - begin);
        }
    }

    public void print_byte(byte[] req, int len) {
        for(int i = 0; i < len; i++) {
            System.out.print(req[i]);
        }
        System.out.println();
    }

    public void parseReq(byte[] req, int len) {
        String begin = new String(req, 0, 5);
        switch(begin) {
            case "GAMES" :
                break;
            
            case "OGAME" :
                break;

        }
    }


    public void reqNewPL(String id, int port) throws IOException {
        byte[] data = ("NEWPL "+id+" "+Integer.toString(port)+"***").getBytes();
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

    public void reqSize(int m) throws IOException {
        byte[] data = new byte[10];
        fill(data, 0, "SIZE? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqList(int m) throws IOException {
        byte[] data = new byte[10];
        fill(data, 0, "LIST? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqGame() throws IOException {
        byte[] data = "GAME?***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

}
