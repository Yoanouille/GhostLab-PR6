package srcjava;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTCP implements Runnable {

    private Socket socket;
    private boolean running = false;
    private final static int buff_size = 256;
    private Fenetre fe;

    private ClientUDP cUdp;
    private ClientMulti cMulti;

    //permet de savoir si on a bien recut le bon nombre de message
    private int count_ogame = 0;
    private int count_list = 0;
    private int count_glis = 0;

    public ClientTCP(InetAddress addr, int port, Fenetre fe) throws IOException {
        socket = new Socket(addr, port);
        this.fe = fe;
        cUdp = new ClientUDP(fe);
        new Thread(cUdp).start();
    }
    
    //simple setter
    public void setFenetre(Fenetre fe) {
        this.fe = fe;
    }

    //fonction pour stoper clientUDP
    public void stopUDP() {
        cUdp.stop();
    }

    //fonction qui recoit les messages tcp et les parse pour ensuite les traiter
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
                 //   System.out.print(buf[i] + " ");
                    req[len] = buf[i];
                    len++;
                    if(buf[i] == '*') {
                        if(prepre) {
                          //  System.out.println();
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
                System.out.println("FIN TCP");
                return;
            }
        }
    }

    //fonction qui permet de remplir un tableau de byte avec une string en partant de begin
    public void fill(byte[] data, int begin, String s) {
        for(int i = begin; i < data.length && i < begin + s.length(); i++) {
            data[i] = (byte) s.charAt(i - begin);
        }
    }

    //affiche un byte
    public void print_byte(byte[] req, int len) {
        for(int i = 0; i < len; i++) {
            System.out.print(req[i]);
        }
        System.out.println();
    }

    //fonction d'erreur
    public void errorOgame() {
        if(count_ogame != 0) {
            System.out.println("Error didn't receive the rigth number of OGAME");
        }
    }

    //fonction d'erreur
    public void errorPlayr() {
        if(count_list != 0) {
            System.out.println("Error didn't receive the right number of PLAYR");
        }
    }

    //fonction d'erreur
    public void errorGlis() {
        if(count_glis != 0) {
            System.out.println("Error didn't receive the right number of GPLYR");
        }
    }

    //fonction qui une fois le messages TCP recut en entier va permettre d'appeler la bonne fonction de reception
    public void parseReq(byte[] res, int len) {
        String begin = new String(res, 0, 5);
        System.out.println(new String(res,0,len));
        switch(begin) {
            case "GAMES" :
                errorOgame();
                errorPlayr();
                errorGlis();
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
                errorGlis();
                resOgame(res, len);
                break;
            
            case "REGOK" :
                errorPlayr();
                errorGlis();
                errorOgame();
                // if(!send.equals("NEWPL") && !send.equals("REGIS")) {
                //     System.out.println("Error recv REGOK but not send NEWPL or REGIS");
                //     System.exit(1);
                // }
                resRegOK(res, len);
                break;

            case "REGNO" :
                errorPlayr();
                errorGlis();
                errorOgame();
                // if(!send.equals("NEWPL") && !send.equals("REGIS")) {
                //     System.out.println("Error recv REGOK but not send NEWPL or REGIS");
                //     System.exit(1);
                // }
                resRegNO(res, len);
                break;
            
            case "UNROK" :
                errorPlayr();
                errorGlis();
                errorOgame();
                resUnRegOK(res, len);
                break;
            
            case "DUNNO" :
                errorGlis();
                errorPlayr();
                errorOgame();
                resDUNNO(res, len);
                break;
            
            case "SIZE!" :
                errorPlayr();
                errorGlis();
                errorOgame();
                resSize(res, len);
                break;

            case "LIST!" :
                errorGlis();
                errorPlayr();
                errorOgame();
                resList(res, len);
                break;

            case "PLAYR" :
                errorGlis();
                errorOgame();
                resPlayr(res, len);
                break;

            case "WELCO" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resWelco(res, len);
                break;
            
            case "POSIT" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resPosit(res, len);
                break;

            case "MOVE!" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resMove(res, len);
                break;
            
            case "MOVEF" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resMoveF(res, len);
                break;
                    
            case "GOBYE" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resGoodBye(res, len);
                break;

            case "GLIS!" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resGlis(res, len);
                break;
            
            case "GPLYR" :
                errorOgame();
                errorPlayr();
                resGplyr(res, len);
                break;                    

            case "MALL!" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resMall(res, len);
                break;

            case "SEND!" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resSend(res, len);
                break;

            case "NSEND" :
                errorOgame();
                errorGlis();
                errorPlayr();
                resNSend(res, len);
                break;
            
            default :
                System.out.println("REPONSE INCONNUE");
                //System.exit(1);
        }       
    }

    //Fonction qui envoie le message NEWPL
    public void reqNewPL(String id) throws IOException {
        //send = "NEWPL";
        byte[] data = ("NEWPL "+id+" "+Integer.toString(cUdp.getPort())+"***").getBytes();
        //System.out.println(new String(data, 0, data.length));
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message REGIS
    public void reqRegis(String id, int game) throws IOException {
        //send = "REGIS";
        byte[] data = new byte[5 + 1 + 8 + 1 + 4 + 1 + 1 + 3];
        fill(data, 0, "REGIS ");
        fill(data, 5 + 1, id);
        fill(data, 5 + 1 + 8, " " + Integer.toString(cUdp.getPort()) + " ");
        data[5 + 1 + 8 + 1 + 4 + 1] = (byte) game;
        fill(data, 5 + 1 + 8 + 1 + 4 + 1 + 1, "***");
        System.out.println("Send REGIS");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message START
    public void reqStart() throws IOException {
        //send = "START";
        byte[] data = new byte[5 + 3];
        fill(data, 0, "START***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message UNREG
    public void reqUnReg() throws IOException {
        //send = "UNREG";
        byte[] data = new byte[5 + 3];
        fill(data, 0, "UNREG***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message SIZE?
    public void reqSize(int m) throws IOException {
        //send = "SIZE?";
        byte[] data = new byte[10];
        fill(data, 0, "SIZE? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message LIST?
    public void reqList(int m) throws IOException {
        //send = "LIST?";
        byte[] data = new byte[10];
        fill(data, 0, "LIST? ");
        data[6] = (byte) m;
        fill(data, 7, "***");
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message GAME?
    public void reqGame() throws IOException {
        //send = "GAME?";
        byte[] data = "GAME?***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }


    //Fonction qui envoie le message **MOV
    //DIR 0 -> UP | 1 -> DOWN | 2 -> LEFT | 3 -> RIGHT
    public void reqMov(int dir, int len) throws IOException {
        String mess = "";
        switch(dir) {
            case 0: mess = "UPMOV ";break;
            case 1: mess = "DOMOV ";break;
            case 2: mess = "LEMOV ";break;
            case 3: mess = "RIMOV ";break;
            default: break;
        }
        mess += String.format("%03d", len);
        mess += "***";
        byte[] data = mess.getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message IQUIT
    public void reqQuit() throws IOException {
        byte[] data = "IQUIT***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message GLIS?
    public void reqGlis() throws IOException {
        byte[] data = "GLIS?***".getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message MALL?
    public void reqMall(String mess) throws IOException {
        String req = "MALL? " + mess + "***";
        byte[] data = req.getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui envoie le message SEND?
    public void reqSend(String id, String mess) throws IOException {
        String req = "SEND? " + id + " " + mess + "***";
        byte[] data = req.getBytes();
        socket.getOutputStream().write(data);
        socket.getOutputStream().flush();
    }

    //Fonction qui parse le message GAMES et actualise l'interface 
    public void resGame(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv GAMES");
            return;
        }
        count_ogame = res[6];
        fe.reset_games();
        System.out.println("Je vais recevoir " + res[6] + " messages OGAME !");
    }

    //Fonction qui parse le message OGAME et actualise l'interface
    public void resOgame(byte[] res, int len) {
        if(len != 12) {
            System.out.println("Error len recv OGAME");
            return;
        }
        count_ogame--;
        fe.add_game((int) res[6],(int) (res[8]));
        System.out.println("Partie " + res[6] + " : " + res[8] + " joueur(s)");
        if(count_ogame == 0) {
            System.out.println("J'ai reçu tous les OGAME");
        }
    }

    //Fonction du message REGOK et actualise l'interface
    public void resRegOK(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv REGOK");
            return;
        }
        System.out.println("Vous avez été bien inscrit à la partie " + (res[6] & 0xff));
        fe.getAccueil().info.setText("Vous avez été bien inscrit à la partie " + (res[6] & 0xff));
        fe.getAccueil().reg = true;
    }

    //Fonction du message REGNO et actualise l'interface
    public void resRegNO(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv REGNO");
            return;
        }
        System.out.println("Vous pouvez pas vous y incrire !");
        fe.getAccueil().info.setText("Vous pouvez pas vous y incrire !");
    }


    //Fonction du message UNROK et actualise l'interface
    public void resUnRegOK(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv UNROK");
            return;
        }
        System.out.println("Vous vous êtes bien désinscrit de la partie " + res[6]);
        fe.getAccueil().reg = false;
        fe.getAccueil().info.setText("Vous vous êtes bien désinscrit de la partie " + res[6]);
    }

    //Fonction du message DUNNO et actualise l'interface
    public void resDUNNO(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv DUNNO");
            return;
        }
        System.out.println("IMPOSSIBLE !");
        fe.getAccueil().info.setText("IMPOSSIBLE !");
    }

    //Fonction qui parse le message SIZE! et actualise l'interface
    public void resSize(byte[] res, int len) {
        if(len != 16) {
            System.out.println("Error len recv SIZE");
            return;
        }
        int h = ((res[9] & 0xff) << 8) | (res[8] & 0xff);
        int w = ((res[12] & 0xff) << 8) | (res[11] & 0xff);

        System.out.println("Taille du lab : " + w + "x" + h + " (wxh)");
        fe.getAccueil().info.setText("Taille du lab : " + w + "x" + h + " (w x h)");

    }

    //Fonction qui parse le message LIST! et actualise l'interface
    public void resList(byte[] res, int len) {
        if(len != 12) {
            System.out.println("Error len recv LIST");
            return;
        }
        System.out.println("Il y a " + res[8] + " joueur(s) dans la Partie " + res[6]);
        count_list = res[8];

        fe.getAccueil().info.setText("<html>Il y a " + res[8] + " joueur(s) dans la Partie " + res[6]+":</html>");
    }

    //Fonction qui parse le message PLAYR et actualise l'interface
    public void resPlayr(byte[] res, int len) {
        if(len != 17) {
            System.out.println("Error len recv PLAYR");
            return;
        }
        String nom = new String(res, 6, 8);
        
        String base = fe.getAccueil().info.getText().substring(0,fe.getAccueil().info.getText().length()-7);
        fe.getAccueil().info.setText(base+"<br>"+nom+"</html>");        
        System.out.println("\t" + nom);
        count_list--;
        if(count_list == 0) {
            System.out.println("J'ai reçu tout les PLAYR");
        }
    }

    //Fonction qui parse le message WELCO et actualise l'interface
    public void resWelco(byte[] res, int len) {
        if(len != 39) {
            System.out.println("Error len recv WELCO");
            return;
        }
        
        int num_partie = res[6] & 0xff;
        int h = ((res[9] & 0xff) << 8) | (res[8] & 0xff);
        int w = ((res[12] & 0xff) << 8) | (res[11] & 0xff);
        int nb_fant = res[14] & 0xff;
        String ip = new String(res, 16, 15);
        int port = Integer.parseInt(new String(res, 32, 4));



        if(cMulti == null) {
            try {
                System.out.println("Je lance MULTI");
                cMulti = new ClientMulti(ip, port, fe);
                new Thread(cMulti).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        fe.initJeu(w,h);
    }


    //Fonction qui parse le message POSIT et actualise l'interface
    public void resPosit(byte[] res, int len) {
        if(len != 25) {
            System.out.println("Error len recv POSIT");
            return;
        }

        String id = new String(res, 6, 8);
        int x = Integer.parseInt(new String(res, 15, 3));
        int y = Integer.parseInt(new String(res, 19, 3));

        fe.setPosJoueur(x, y);
    }

    //Fonction qui parse le message MOVE! et actualise l'interface
    public void resMove(byte[] res, int len) {
        if(len != 16) {
            System.out.println("Error len recv MOVE!");
            return;
        }

        int x = Integer.parseInt(new String(res, 6, 3));
        int y = Integer.parseInt(new String(res, 10, 3));

        fe.setPosJoueur(x,y);
    }

    //Fonction qui parse le message MOVEF et actualise l'interface
    public void resMoveF(byte[] res, int len) {
        if(len != 21) {
            System.out.println("Error len recv MOVE!");
            return;
        }

        int x = Integer.parseInt(new String(res, 6, 3));
        int y = Integer.parseInt(new String(res, 10, 3));
        int p = Integer.parseInt(new String(res, 14, 4));

        fe.setPosJoueur(x,y);
    }

    //Fonction du message GOBYE et ferme la connection
    public void resGoodBye(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv GOBYE");
            return;
        }

        System.out.println("Au revoir !");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cMulti.stop();
        cUdp.stop();
    }

    //Fonction qui parse le message GLIS! et actualise l'interface
    public void resGlis(byte[] res, int len) {
        if(len != 10) {
            System.out.println("Error len recv GLIS!");
            return;
        }

        count_glis = res[6];
        System.out.println("Je vais recevoir " + res[6] + " messages GPLYR");
        fe.reset_players();
    }

    //Fonction qui parse le message GPLYR et actualise l'interface
    public void resGplyr(byte[] res, int len) {
        if(len != 30) {
            System.out.println("Error len recv GPLYR");
            return;
        }

        count_glis--;
        String id = new String(res, 6, 8);
        int x = Integer.parseInt(new String(res,15,3));
        int y = Integer.parseInt(new String(res,19,3));
        int p = Integer.parseInt(new String(res,23,4));

        fe.drawPlayers(x, y);
        fe.add_player(id,p);
    }

    //Fonction du message MALL!
    public void resMall(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv MALL!");
            return;
        }
        System.out.println("Message bien envoyé à tous !");
    }

    //Fonction du message SEND!
    public void resSend(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv SEND!");
            return;
        }
        System.out.println("Message bien envoyé !");
    }

    //Fonction du message NSEND
    public void resNSend(byte[] res, int len) {
        if(len != 8) {
            System.out.println("Error len recv NSEND");
            return;
        }
        System.out.println("Message pas bien envoyé !");
    }
}
