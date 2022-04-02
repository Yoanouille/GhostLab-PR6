import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.io.IOException;
import java.util.HashMap;

public class Fenetre extends JFrame {
    private Client client;

    private JPanel mainPanel;
    private CardLayout cardLayout = new CardLayout();

    private Accueil acc;

    public Fenetre(Client c) {
        super();
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

        setScene("accueil");
    }

    public void setScene(String scene) {
        cardLayout.show(mainPanel, scene);
    }

    public HashMap<Integer,Integer> getGames() throws IOException {
        return client.resGames();
    }
}