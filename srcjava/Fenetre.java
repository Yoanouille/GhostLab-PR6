package srcjava;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Component;

import java.lang.reflect.InvocationTargetException;

public class Fenetre extends JFrame {
    private ClientTCP client;

    private JPanel mainPanel;
    private CardLayout cardLayout = new CardLayout();

    private Accueil acc;

    private EcranAttente attente;

    private MenuPartie jeu;

    public Fenetre(ClientTCP c) {
        super();
        c.setFenetre(this);
        this.client = c;
        this.setSize(800, 520);
        this.setTitle("Ghost Lab");
        this.setVisible(true);
        // this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setAlwaysOnTop(false);
        this.setMinimumSize(new Dimension(500,300));
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel(cardLayout);

        acc = new Accueil(this);

        attente = new EcranAttente(this);

        mainPanel.add("attente",attente);
        mainPanel.add("accueil", acc);
        this.add(mainPanel);

        setScene("accueil");
    }

    public void initJeu(int w, int h){
        jeu = new MenuPartie(this,w,h);
        mainPanel.add("jeu",jeu);
        setScene("jeu");
    }

    public void setPosJoueur(int x,int y){
        jeu.setJoueur(x,y);
    }

    public void setScene(String scene) {
        cardLayout.show(mainPanel, scene);
        mainPanel.updateUI();
    }

    public ClientTCP getClient(){
        return client;
    }

    public Accueil getAccueil(){
        return this.acc;
    }

    public void reset_games(){
        Runnable run=new Runnable(){
            public void run(){
                acc.games.clear();
            }
        };
        SwingUtilities.invokeLater(run);
    }

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

    public void reset_players(){
        Runnable run=new Runnable(){
            public void run(){
                jeu.players.clear();
            }
        };
        SwingUtilities.invokeLater(run);
    }

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

    public void add_message(String id, String msg){
        jeu.historic.setText(jeu.historic.getText().substring(0,jeu.historic.getText().length()-7)+"<br>"+id+": "+msg+"</html>");

    }

    public void add_messageP(String id, String msg){
        jeu.historic.setText(jeu.historic.getText().substring(0,jeu.historic.getText().length()-7)+"<br><font color='red'>"+id+"(to you)"+msg+"</font></html>");
    }

    public void drawGhost(int x, int y) {
        jeu.addPosToDraw(x, y, "ghost");
    }

    public void drawPlayers(int x, int y) {
        jeu.addPosToDraw(x, y, "player");
    }

    public void addTrap(int x, int y) {
        jeu.addTrap(x, y);
    }

}