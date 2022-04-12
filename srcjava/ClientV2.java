package srcjava;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SNIHostName;
import javax.swing.*;

public class ClientV2 implements Runnable {
    private Socket socket;

    private boolean running = false;

    private final static int buff_size = 256;

    public Fenetre fe;

    //private String send = "GAME?";
    private int count_ogame = 0;
    private int count_list = 0;

    public ClientV2(InetAddress addr, int port) throws IOException {
        socket = new Socket(addr, port);
        fe = new Fenetre(this);
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

    public void errorOgame() {
        if(count_ogame != 0) {
            System.out.println("Error didn't receive the rigth number of OGAME");
            System.exit(1);
        }
    }

    public void errorPlayr() {
        if(count_list != 0) {
            System.out.println("Error didn't receive the right number of PLAYR");
            System.exit(1);
        }
    }

    public void parseReq(byte[] res, int len) {
        String begin = new String(res, 0, 5);
        System.out.println(new String(res,0,len));
        switch(begin) {
            case "GAMES" :
                errorOgame();
                errorPlayr();
                // if(!send.equals("GAME?")) {
                //     System.out.println("Error recv GAMES but not send GAME?");
                //     System.exit(1);
                // }
                resGame(res, len);
                break;
            
            case "OGAME" :
                // if(!send.equals("GAME?")) {
                //     System.out.println("Error recv OGAME but not send GAME?");
                //     System.exit(1);
                // }
                errorPlayr();
                resOgame(res, len);
                break;
            
            case "REGOK" :
                errorPlayr();
                errorOgame();
                // if(!send.equals("NEWPL") && !send.equals("REGIS")) {
                //     System.out.println("Error recv REGOK but not send NEWPL or REGIS");
                //     System.exit(1);
                // }
                resRegOK(res, len);
                break;

            case "REGNO" :
                errorPlayr();
                errorOgame();
                // if(!send.equals("NEWPL") && !send.equals("REGIS")) {
                //     System.out.println("Error recv REGOK but not send NEWPL or REGIS");
                //     System.exit(1);
                // }
                resRegNO(res, len);
                break;
            
            case "UNROK" :
                errorPlayr();
                errorOgame();
                resUnRegOK(res, len);
                break;
            
            case "DUNNO" :
                errorPlayr();
                errorOgame();
                resDUNNO(res, len);
                break;
            
            case "SIZE!" :
                errorPlayr();
                errorOgame();
                resSize(res, len);
                break;

            case "LIST!" :
                errorPlayr();
                errorOgame();
                resList(res, len);
                break;

            case "PLAYR" :
                errorOgame();
                resPlayr(res, len);
                break;
            
            default :
                System.out.println("REPONSE INCONNUE");
                System.exit(1);
        }       
    }


    public void reqNewPL(String id, int port) throws IOException {
        //send = "NEWPL";
        byte[] data = ("NEWPL "+id+" "+Integer.toString(port)+"***").getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqRegis(String id, int port, int game) throws IOException {
        //send = "REGIS";
        byte[] data = new byte[5 + 1 + 8 + 1 + 4 + 1 + 1 + 3];
        fill(data, 0, "REGIS ");
        fill(data, 5 + 1, id);
        fill(data, 5 + 1 + 8, " " + Integer.toString(port) + " ");
        data[5 + 1 + 8 + 1 + 4 + 1] = (byte) game;
        fill(data, 5 + 1 + 8 + 1 + 4 + 1 + 1, "***");
        System.out.println("Send REGIS");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }


    public void reqStart() throws IOException {
        //send = "START";
        byte[] data = new byte[5 + 3];
        fill(data, 0, "START***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqUnReg() throws IOException {
        //send = "UNREG";
        byte[] data = new byte[5 + 3];
        fill(data, 0, "UNREG***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqSize(int m) throws IOException {
        //send = "SIZE?";
        byte[] data = new byte[10];
        fill(data, 0, "SIZE? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqList(int m) throws IOException {
        //send = "LIST?";
        byte[] data = new byte[10];
        fill(data, 0, "LIST? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void reqGame() throws IOException {
        //send = "GAME?";
        byte[] data = "GAME?***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    public void resGame(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv GAMES");
            System.exit(1);
        }
        count_ogame = res[6];
        Runnable run=new Runnable(){
            public void run(){
                fe.reset_games();
            }
        };
        SwingUtilities.invokeLater(run);
        //ICI Mettre à 0 la liste dans l'interface graphique
        System.out.println("Je vais recevoir " + res[6] + " messages OGAME !");
    }

    public void resOgame(byte[] res, int len) {
        if(len != 12) {
            System.out.println("Error len recv OGAME");
            System.exit(1);
        }
        count_ogame--;
        Runnable run=new Runnable(){
            public void run(){
                    fe.add_game("Partie " + (int) (res[6]) + " : " + res[8] + " joueur(s)");
            }
        };
        try {
            SwingUtilities.invokeAndWait(run);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //ICI ajouter à une liste dans l'interface graphique
        System.out.println("Partie " + res[6] + " : " + res[8] + " joueur(s)");
        if(count_ogame == 0) {
            //send = "";
            //ICI actualiser l'interface graphique
            System.out.println("J'ai reçu tous les OGAME");
            
        }
    }

    public void resRegOK(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv REGOK");
            System.exit(1);
        }
        System.out.println("Vous avez été bien inscrit à la partie " + (res[6] & 0xff));
        //send = "";
        //ICI Actualiser l'interface !
    }

    public void resRegNO(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv REGNO");
            System.exit(1);
        }
        System.out.println("Vous pouvez pas vous y incrire !");
        //send = "";
        //ICI Actualiser l'interface !
    }

    public void resUnRegOK(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv UNROK");
            System.exit(1);
        }
        System.out.println("Vous vous êtes bien désinscrit de la partie " + res[6]);
        //send = "";
        //ICI Actualiser l'interface !
    }

    public void resDUNNO(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv DUNNO");
            System.exit(1);
        }
        System.out.println("IMPOSSIBLE !");
        //send = "";
        //ICI Actualiser l'interface !
    }

    public void resSize(byte[] res, int len) {
        if(len != 16) {
            System.out.println("Error len recv SIZE");
            System.exit(1);
        }
        int h = ((res[9] & 0xff) << 8) | (res[8] & 0xff);
        int w = ((res[12] & 0xff) << 8) | (res[11] & 0xff);

        //send = "";
        System.out.println("Taille du lab : " + w + "x" + h + " (wxh)");
        fe.getAccueil().info.setText("Taille du lab : " + w + "x" + h + " (w x h)");

        //ICI Actualiser l'interface graphique !
    }

    public void resList(byte[] res, int len) {
        if(len != 12) {
            System.out.println("Error len recv LIST");
            System.exit(1);
        }
        System.out.println("Il y a " + res[8] + " joueur(s) dans la Partie " + res[6]);
        count_list = res[8];

        fe.getAccueil().info.setText("Il y a " + res[8] + " joueur(s) dans la Partie " + res[6]);
        //send = "";
        //ICI Actualiser l'interface graphique
    }

    public void resPlayr(byte[] res, int len) {
        if(len != 17) {
            System.out.println("Error len recv PLAYR");
            System.exit(1);
        }
        String nom = new String(res, 6, 8);
        
        String base = fe.getAccueil().info.getText();
        fe.getAccueil().info.setText(base+":\n"+nom);

        //Ajouter le nom dans une liste
        System.out.println("\t" + nom);
        count_list--;
        if(count_list == 0) {
            //ICI Actualiser l'interface graphique
            System.out.println("J'ai reçu tout les PLAYR");
        }
    }


}
