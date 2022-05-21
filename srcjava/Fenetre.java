package srcjava;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.awt.Component;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;

//Classe principale de l'interface graphique
public class Fenetre extends JFrame {
    private InetAddress ip;
    private int port;

    private ClientTCP client;

    private JPanel mainPanel;
    private CardLayout cardLayout = new CardLayout();

    private Accueil acc;

    private Start startScreen;
    private EcranAttente attente;

    private MenuPartie jeu;

    //Constructueur qui permet la création du JFrame et des scene associe
    public Fenetre(InetAddress ip, int port) {
        super();

        this.ip = ip;
        this.port = port;
        //c.setFenetre(this);
        //this.client = c;
        this.setSize(800, 520);
        this.setTitle("Ghost Lab");
        this.setVisible(true);
        // this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setAlwaysOnTop(false);
        this.setMinimumSize(new Dimension(500,300));
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel(cardLayout);


        //acc = new Accueil(this);
        startScreen = new Start(this);
        attente = new EcranAttente(this);

        mainPanel.add("start", startScreen);
        mainPanel.add("attente",attente);
        this.add(mainPanel);

        setScene("start");
    }

    //Fonction qui creer un MenuPartie avec les tailles donne par le message WELCO et affiche la scene
    public void initJeu(int w, int h){
        jeu = new MenuPartie(this,w,h);
        mainPanel.add("jeu",jeu);
        setScene("jeu");
        jeu.setFocusable(true);
        jeu.requestFocus();
        jeu.requestFocusInWindow();
        jeu.setId(acc.id.getText());
    }

    //Fonction pour demarrer la connexion en creant le client TCP et afficher l'accueil
    public void start() {
        try {
            client = new ClientTCP(ip, port, this);
            new Thread(client).start();
            acc = new Accueil(this);
            mainPanel.add("accueil", acc);
            this.setScene("accueil");
        } catch (IOException e) {
            System.out.println("Error create client !");
            System.exit(1);
        }
    }

    //Fonction pour dessiner le joueur a la bonne position
    public void setPosJoueur(int x,int y){
        jeu.setJoueur(x,y);
    }

    //fonction pour afficher la scene desiree
    public void setScene(String scene) {
        cardLayout.show(mainPanel, scene);
        mainPanel.updateUI();
    }

    //getter qui retourne le clientTCP
    public ClientTCP getClient(){
        return client;
    }

    //getter qui retourne l'accueil
    public Accueil getAccueil(){
        return this.acc;
    }

    //fonction pour enlever les parties presentes dans la liste contenue dans accueil
    public void reset_games(){
        Runnable run=new Runnable(){
            public void run(){
                acc.games.clear();
            }
        };
        SwingUtilities.invokeLater(run);
    }

    //fonction pour ajouter une partie dans la liste des parties de accueil
    public void add_game(int ngame, int nplayer){
        Runnable run=new Runnable(){
            public void run(){
                acc.games.addElement("Partie " + ngame + " : " + nplayer + " joueur(s)");
            }
        };
        try {
            SwingUtilities.invokeAndWait(run);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //Fonction pour changer le score d'un joueur dans MenuPartie
    public void set_player_score(String id, int p){
        Runnable run=new Runnable(){
            public void run(){
                jeu.setScore(id, p);
            }
        };
        SwingUtilities.invokeLater(run);
    }

    //Fonction pour changer le score du joueur dans MenuPartie
    public void set_my_score(int p){
        Runnable run=new Runnable(){
            public void run(){
                jeu.setMyScore(p);
            }
        };
        SwingUtilities.invokeLater(run);
    }

    //fonction pour enlever les joueurs presents dans la liste contenue dans MenuPartie
    public void reset_players(){
        Runnable run=new Runnable(){
            public void run(){
                jeu.players.clear();
            }
        };
        SwingUtilities.invokeLater(run);
    }

    //fonction pour ajouter un joeuur dans la liste des joueur de MenuPartie
    public void add_player(String id, int score){
        Runnable run=new Runnable(){
            public void run(){
                jeu.players.addElement(id + " points: " + score);
            }
        };
        try {
            SwingUtilities.invokeAndWait(run);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //fonction pour ajouter un message dans le chat de MenuPartie
    public void add_message(String id, String msg){
        jeu.historic.setText(jeu.historic.getText().substring(0,jeu.historic.getText().length()-7)+"<br>"+id+": "+msg+"</html>");
    }

    //fonction pour ajouter un message personnel dans le chat de MenuPartie
    public void add_messageP(String id, String msg){
        jeu.historic.setText(jeu.historic.getText().substring(0,jeu.historic.getText().length()-7)+"<br><font color='red'>"+id+"(to you)"+msg+"</font></html>");
    }

    //fonction pour dessiner un fantome dans MenuPartie
    public void drawGhost(int x, int y) {
        jeu.addPosToDraw(x, y, "ghost");
    }

    //Fonction pour dessiner les joueurs dans MenuPartie
    public void drawPlayers(int x, int y) {
        jeu.addPosToDraw(x, y, "player");
    }

    //Fonction pour ajouter un piege dans le plateau de MenuPartie
    public void addTrap(int x, int y) {
        jeu.addTrap(x, y);
    }

    //Fonction pour stopper le clientUDP
    public void stopUdp() {
        client.stopUDP();
    }

    //Fonction pour demandé l'envoie du message IQUIT
    public void sendQuit() {
        try {
            client.reqQuit();
            jeu.stop();
        } catch (IOException e) {
            System.out.println("Erreur send end !");
        }
    }

    //Fonction pour choisir le message affiche sur la scene setEnd
    public void setEnd(String end) {
        mainPanel.add("end",new EndScreen(this, end));
        setScene("end");
    }

    //fonction pour mettre l'id du joueur dans MenuPartie
    public void setJeuId(String id){
        jeu.setId(id);
    } 

}