import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Client {
    
    private Socket socket;

    public Client(InetAddress addr, int port) {
        try {
            socket = new Socket(addr, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int myRecv(byte[] data, int offset, int len) throws IOException {
        int re = 0;
        do {
            re = socket.getInputStream().read(data, re + offset, len - re);
            if(re == -1) break;
        } while(re < len);
        return re;
    }

    public void fill(byte[] data, int begin, String s) {
        for(int i = begin; i < data.length && i < begin + s.length(); i++) {
            data[i] = (byte) s.charAt(i - begin);
        }
    }

    public boolean verifyBegin(byte[] data, int offset, String s) {
        return new String(data, offset, 5).equals(s);
    }

    public void print_byte(byte[] req, int len) {
        for(int i = 0; i < len; i++) {
            System.out.print(req[i]);
        }
        System.out.println();
    }

    public HashMap<Integer,Integer> resGames() throws IOException {
        //GAMES m
        int len = 10;
        byte[] data = new byte[len];
        int re = myRecv(data, 0,len);
        if(re != len) {
            System.out.println("Error first msg games");
            System.exit(1);
        }
        
        if(!verifyBegin(data, 0,"GAMES")) {
            System.out.println("Wrong msg GAMES ! resGames");
            System.exit(1);
        }
        int m = data[6];
        System.out.println("Il y a " + m + " parties !");

        len = 12 * m;
        data = new byte[len];
        re = myRecv(data, 0,len);
        if(re != len) {
            System.out.println("Error msg OGAMES");
            System.exit(1);
        }

        HashMap<Integer, Integer> res = new HashMap<>();

        for(int i = 0; i < m; i++) {
            if(!verifyBegin(data, 12 * i,"OGAME")) {
                System.out.println("Wrong msg OGAME " + i + " ! resGames");
                System.exit(1);
            }
            System.out.println("Partie " + data[12 * i + 6] + " : " + data[12 * i + 8] + " joueur(s)");
            res.put((int)data[12 * i + 6], (int)data[12 * i + 8]);
        }
        return res;
    }

    public boolean reqNewPL(String id, int port) throws IOException {
        byte[] data = new byte[5 + 1 + 8 + 1 + 4 + 3];
        fill(data, 0, "NEWPL");
        fill(data, 5 + 1, id);
        fill(data, 5 + 1 + 8, " " + Integer.toString(port));
        fill(data, 5 + 1 + 8 + 1 + 4, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        return resReg();
    }

    public boolean resReg() throws IOException {
        byte[] data = new byte[10];
        int len = 5;
        int re = myRecv(data, 0,len);
        if(re != len) {
            System.out.println("Error read resReg !");
            System.exit(1);
        }
        if(verifyBegin(data, 0, "REGOK")) {
            len = 10;
            myRecv(data, 5, len - 5);
            System.out.println("Vous êtes dans la partie " + data[6]);
            return true;
        } else if(verifyBegin(data, 0, "REGNO")) {
            len = 8;
            myRecv(data, 5, len - 5);
            return false;
        } else {
            System.out.println("Error resReg !"); 
            System.exit(1);  
        }
        return false;
    }

    public boolean reqRegis(String id, int port, int game) throws IOException {
        byte[] data = new byte[5 + 1 + 8 + 1 + 4 + 1 + 1 + 3];
        fill(data, 0, "REGIS ");
        fill(data, 5 + 1, id);
        fill(data, 5 + 1 + 8, " " + Integer.toString(port) + " ");
        data[5 + 1 + 8 + 1 + 4 + 1] = (byte) game;
        fill(data, 5 + 1 + 8 + 1 + 4 + 1 + 1, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        return resReg();
    }

    public void reqStart() throws IOException {
        byte[] data = new byte[5 + 3];
        fill(data, 0, "START***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public boolean reqUnReg() throws IOException {
        byte[] data = new byte[5 + 3];
        fill(data, 0, "UNREG***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        return resUnReg();
    }


    public boolean resUnReg() throws IOException {
        byte[] data = new byte[10];
        int len = 5;
        int re = myRecv(data, 0,len);
        if(re != len) {
            System.out.println("Error read resUnReg !");
            System.exit(1);
        }
        if(verifyBegin(data, 0, "UNROK")) {
            len = 10;
            myRecv(data, 5, len - 5);
            System.out.println("Vous vous êtes bien désinscrit de la partie " + data[6]);
            return true;
        } else if(verifyBegin(data, 0, "DUNNO")) {
            len = 8;
            myRecv(data, 5, len - 5);
            System.out.println("Peut pas se désinscrire !");
            return false;
        } else {
            System.out.println("Error resUnReg !"); 
            System.exit(1);  
        }
        return false;
    }

    public int[] reqSize(int m) throws IOException {
        byte[] data = new byte[10];
        fill(data, 0, "SIZE? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        return resSize();
    }

    public int[] resSize() throws IOException {
        byte[] data = new byte[10];
        int len = 5;
        int re = myRecv(data, 0,len);
        if(re != len) {
            System.out.println("Error read resSize !");
            System.exit(1);
        }
        if(verifyBegin(data, 0, "SIZE!")) {
            len = 16;
            myRecv(data, 5, len - 5);
            int[] res = new int[2];

            //TODO: va Falloir les convertir !!!! (en little endian ou big endian)
            res[0] = ((data[8] & 0xff) << 8) | (data[9] & 0xff);
            res[1] = ((data[11] & 0xff) << 8) | (data[12] & 0xff);
            System.out.println("Taille labyrinthe  partie " + data[6] + " : " + res[0] + "x" + res[1]);
            return res;
        } else if(verifyBegin(data, 0, "DUNNO")) {
            len = 8;
            myRecv(data, 5, len - 5);
            System.out.println("Pas de partie correspondante");
            return null;
        } else {
            System.out.println("Error resSize !"); 
            System.exit(1);  
        }
        return null;
    }

    public String[] reqList(int m) throws IOException {
        byte[] data = new byte[10];
        fill(data, 0, "LIST? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        return resList();
    }

    public String[] resList() throws IOException {
        int len = 10;
        byte[] data = new byte[len];
        int re = myRecv(data, 0, 5);
        if(re != 5) {
            System.out.println("Error resList first recv");
            System.exit(1);
        }
        if(verifyBegin(data, 0, "DUNNO")) {
            len = 8;
            myRecv(data, 5, 8 - 5);
            System.out.println("Pas de partie correspondante");
        } else if(!verifyBegin(data, 0, "LIST!")) {
            System.out.println("Wrong msg ! resList");
            System.exit(1);
        }

        myRecv(data, 5, len - 5);
        int m = (int) data[6];
        System.out.println("Pour la partie " + m + ", il y a :" );
        int s = (int) data[8];

        len = s * 17;
        data = new byte[len];
        
        String[] res = new String[s];
        for(int i = 0; i < s; i++) {
            if(!verifyBegin(data, 17 * i,"PLAYR")) {
                System.out.println("Wrong msg PLAYR " + i + " ! resList");
                System.exit(1);
            }
            String joueur = new String(data, 12 * i + 6, 8);
            System.out.println("\tJoueur " + i + " : " + joueur);
            res[i] = joueur;
        }
        return res;
    }

    public HashMap<Integer, Integer> reqGame() throws IOException {
        byte[] data = "GAME?***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        return resGames();
    }

    public void sendStart() throws IOException {
        byte[] data = "START***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();

        //TODO: Recevoir le Welcome !
    }


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