package srcjava;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.*;


//Classe representant la premiere scene affichee
public class Start extends JPanel {
    private Fenetre fe;

    public Start(Fenetre fe) {

        this.fe = fe;

        this.setLayout(new GridLayout(2,1));
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        JLabel text = new JLabel("GHOSTLAB");
        text.setFont(new Font("Verdana", Font.PLAIN,  40));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        top.add(text, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10,10,10,10);

        //Ce bouton lance fe.start() qui active la connexion
        BetterButton start = new BetterButton("Start");
        bottom.add(start, c);
        start.addActionListener((e) -> {
            fe.start();
        });

        this.add(top);
        this.add(bottom);
    }


}
