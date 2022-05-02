package srcjava;


import javax.swing.*;

public class EcranAttente extends JPanel{
    private Fenetre fe;

    public EcranAttente(Fenetre fenetre){
        this.fe = fenetre;


        this.add(new JLabel("Waiting for other players"));
        this.setVisible(true);
    }
}
