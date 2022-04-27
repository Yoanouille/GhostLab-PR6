package srcjava;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


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

    public JLabel info = new JLabel("Nothing for now");

    private JButton size = new JButton("Size of lab");

    private JButton unregister = new JButton ("Unregister");

    private JButton refresh = new JButton ("refresh");

    public Boolean reg = false;

    public class JTextFieldLimit extends PlainDocument {
        private int limit;
        // optional uppercase conversion
        private boolean toUppercase = false;
        
        JTextFieldLimit(int limit) {
         super();
         this.limit = limit;
         }
         
        JTextFieldLimit(int limit, boolean upper) {
         super();
         this.limit = limit;
         toUppercase = upper;
         }
       
        public void insertString
          (int offset, String  str, AttributeSet attr)
            throws BadLocationException {
         if (str == null) return;
      
         if ((getLength() + str.length()) <= limit) {
           if (toUppercase) str = str.toUpperCase();
           super.insertString(offset, str, attr);
           }
         }
      }
      

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

        unregister.addActionListener((ActionEvent e) -> {
            unreg();
        });

        refresh.addActionListener((ActionEvent e) -> {
            refresh();
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

        
        id.setDocument(new JTextFieldLimit(8));

        rightPane.add(id);
        rightPane.add(create_game);
        rightPane.add(start);
        rightPane.add(register);
        rightPane.add(detail);
        rightPane.add(size);
        rightPane.add(unregister);
        rightPane.add(info);
        rightPane.add(refresh);

        this.add(rightPane);

        this.setVisible(true);
        this.updateUI();

    }   

    public void create_game() {
        //definir le port
        if(!reg){
            try {
                int n = 8 - id.getText().length();
                char[] spaces = new char[n];
                Arrays.fill(spaces, ' ');
                fenetre.getClient().reqNewPL(id.getText()+(new String(spaces)));
                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            info.setText("You are registered in a game");
        }
    }

    public void register_to() {
        //definir le port
        if(!reg){
            String g = games_list.getSelectedValue();
            String s[] = g.split(":");

            s[0] = s[0].replaceAll("[^0-9]", "");
            int m = Integer.parseInt(s[0]);

            try {
                int n = 8 - id.getText().length();
                char[] spaces = new char[n];
                Arrays.fill(spaces, ' ');
                fenetre.getClient().reqRegis(id.getText()+(new String(spaces)),m);
                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            info.setText("You are registered in a game");
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

    public void unreg(){
        if(reg){
            try {
                fenetre.getClient().reqUnReg();
                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            info.setText("You are not registered to any game");
        }
    }

    public void refresh(){
        try {
            fenetre.getClient().reqGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.info.setText("Nothing for now");
    }
    
}
