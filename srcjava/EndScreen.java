package srcjava;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.*;;

public class EndScreen extends JPanel {
    public EndScreen(Fenetre fe, String te) {
        this.setLayout(new GridLayout(2,1));
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        JLabel text = new JLabel(te);
        text.setFont(new Font("Verdana", Font.PLAIN,  30));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        top.add(text, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10,10,10,10);
        BetterButton back = new BetterButton("Back to start screen");
        bottom.add(back, c);
        back.addActionListener((e) -> {
            fe.setScene("start");
        });

        this.add(top);
        this.add(bottom);
    }
}
