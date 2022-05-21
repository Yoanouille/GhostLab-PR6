package srcjava;


import javax.swing.*;
import java.awt.*;;

//Classe qui represente un ecran d'accueil
public class EcranAttente extends JPanel{
    private Fenetre fe;

    public EcranAttente(Fenetre fenetre){
        this.fe = fenetre;
        this.setLayout(new BorderLayout());

        JLabel label = new JLabel("Waiting for other players");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.PLAIN,  20));

        this.add(label, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
