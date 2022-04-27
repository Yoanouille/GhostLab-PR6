package srcjava;

import javax.swing.*;
import java.awt.CardLayout;
import java.lang.reflect.InvocationTargetException;

public class Fenetre extends JFrame {
    private ClientTCP client;

    private JPanel mainPanel;
    private CardLayout cardLayout = new CardLayout();

    private Accueil acc;

    public Fenetre(ClientTCP c) {
        super();
        c.fe = this;
        this.client = c;
        this.setSize(800, 500);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel(cardLayout);
        acc = new Accueil(this);

        mainPanel.add("accueil", acc);
        this.add(mainPanel);

        setScene("accueil");
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

}