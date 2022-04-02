import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Accueil extends JPanel{

    private Fenetre fenetre;

    private JButton[] games;

    public Accueil(Fenetre f) {
        this.fenetre = f;

        // try {
        //     HashMap<Integer, Integer> gamesList = fenetre.getGames();

        //     games = new JButton[gamesList.size()];
        //     int i = 0;
        //     for(Entry<Integer, Integer> entry : gamesList.entrySet()) {
        //         games[i] = new JButton("Partie " + entry.getKey() + " : " + entry.getValue() + " joueur(s)");

        //         i++;
        //     }
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }   
    
}
