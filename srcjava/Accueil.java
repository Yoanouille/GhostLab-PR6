package srcjava;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.plaf.DimensionUIResource;

import org.w3c.dom.events.Event;


public class Accueil extends JPanel{

    private GridLayout gridLayout = new GridLayout(1,2);

    private Fenetre fenetre;

    private JPanel rightPane = new JPanel();

    public DefaultListModel<String> games = new DefaultListModel<String>();

    private JList<String> games_list = new JList<String>(games);

    private JTextField id = new JTextField();

    private JButton create_game = new JButton("Create a new Game");
    

    private JButton register = new JButton("register");

    private JButton start = new JButton("Start");

    private JButton detail = new JButton("Details");

    public JTextArea info = new JTextArea("Nothing for now");

    private JButton size = new JButton("Size of lab");

    public Accueil(Fenetre f) {
        this.fenetre = f;
        this.setLayout(gridLayout);
       
        create_game.addActionListener((ActionEvent e) -> {
            create_game();
        });

        register.addActionListener((ActionEvent e) -> {
            if(games_list.getSelectedValue() != null){
                register_to();
            }
        });
        detail.addActionListener((ActionEvent e) -> {
            if(games_list.getSelectedValue() != null){
                get_list();
            }
        });
        size.addActionListener((ActionEvent e) -> {
            if(games_list.getSelectedValue() != null){
                get_size();
            }
        });

        games_list.setVisibleRowCount(10);
        games_list.setLayoutOrientation(JList.VERTICAL);
        JScrollPane list = new JScrollPane(games_list);

        this.setLayout(gridLayout);
        this.add(list);

        GridLayout g = new GridLayout(0,1);
        g.setVgap(30);
        rightPane.setLayout(g);
        register.setBorder(BorderFactory.createCompoundBorder());

        rightPane.add(create_game);
        rightPane.add(start);
        rightPane.add(register);
        rightPane.add(detail);
        rightPane.add(size);
        rightPane.add(info);

        this.add(rightPane);

        this.setVisible(true);
        this.updateUI();

    }   

    public void create_game() {
        //definir le port
        try {
            fenetre.getClient().reqNewPL(id.getText(), 6666);
            fenetre.getClient().reqGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register_to() {
        //definir le port
        String g = games_list.getSelectedValue();
        String s[] = g.split(":");
        System.out.println(s[0]);

        s[0] = s[0].replaceAll("[^0-9]", "");
        System.out.println(s[0]);
        int m = Integer.parseInt(s[0]);

        try {
            fenetre.getClient().reqRegis(id.getText(), 6666, m);
            fenetre.getClient().reqGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void get_list(){
        String g = games_list.getSelectedValue();
        String s[] = g.split(":");
        System.out.println(s[0]);

        s[0] = s[0].replaceAll("[^0-9]", "");
        System.out.println(s[0]);
        int m = Integer.parseInt(s[0]);
        try {
            fenetre.getClient().reqList(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void get_size(){
        String g = games_list.getSelectedValue();
        String s[] = g.split(":");
        System.out.println(s[0]);

        s[0] = s[0].replaceAll("[^0-9]", "");
        System.out.println(s[0]);
        int m = Integer.parseInt(s[0]);
        try {
            fenetre.getClient().reqSize(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
