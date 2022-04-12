package srcjava;

import javax.swing.*;
import java.awt.CardLayout;
import java.io.IOException;
import java.util.HashMap;

public class Fenetre extends JFrame {
    private ClientV2 client;

    private JPanel mainPanel;
    private CardLayout cardLayout = new CardLayout();

    private Accueil acc;

    public Fenetre(ClientV2 c) {
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

    public ClientV2 getClient(){
        return client;
    }

    public Accueil getAccueil(){
        return this.acc;
    }

    public void reset_games(){
        acc.games.clear();
    }

    public void add_game(String s){
        acc.games.addElement(s);
    }

}