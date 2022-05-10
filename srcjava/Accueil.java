package srcjava;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.JViewport;



public class Accueil extends JPanel{

    private GridLayout gridLayout = new GridLayout(1,2);

    private Fenetre fenetre;

    private JPanel rightPane = new JPanel();

    private JScrollPane rightPane_ScrollPane = new JScrollPane(rightPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    public DefaultListModel<String> games = new DefaultListModel<String>();

    private JList<String> games_list = new JList<String>(games);

    private JTextField id = new JTextField();

    private BetterButton create_game = new BetterButton("Create a new Game");
    

    private BetterButton register = new BetterButton("Register");

    private BetterButton start = new BetterButton("Start");

    private BetterButton detail = new BetterButton("Details");

    public JLabel info = new JLabel("Nothing for now");

    private BetterButton size = new BetterButton("Size of lab");

    private BetterButton unregister = new BetterButton ("Unregister");

    private BetterButton refresh = new BetterButton ("refresh");

    public Boolean reg = false;

    private class JTextFieldLimit extends PlainDocument {
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
      
         if ((getLength() + str.length()) <= limit && isAlphaNum(str)) {
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
        start.addActionListener((ActionEvent e) -> {
            start();
        });

        games_list.setVisibleRowCount(10);
        games_list.setLayoutOrientation(JList.VERTICAL);
        games_list.setFont(new Font("Verdana", Font.PLAIN,  20));
        JScrollPane list = new JScrollPane(games_list);

        this.setLayout(gridLayout);
        this.add(list);

        GridLayout g = new GridLayout(0,1);
        g.setVgap(30);
        

        id.setFont(new Font("Verdana", Font.PLAIN,  20));
        id.setHorizontalAlignment(SwingConstants.CENTER);
        id.setDocument(new JTextFieldLimit(8));

        rightPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.ipady = 40;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10,10,10,10);
        rightPane.add(id, c);

        c.gridy = 1;
        rightPane.add(create_game,c);

        c.gridy = 2;
        rightPane.add(start,c);

        c.gridy = 3;
        rightPane.add(register,c);

        c.gridy = 4;
        rightPane.add(detail,c);

        c.gridy = 5;
        rightPane.add(size, c);

        c.gridy = 6;
        rightPane.add(unregister,c);

        c.gridy = 7;
        info.setFont(new Font("Verdana", Font.PLAIN,  15));
        rightPane.add(info,c);

        c.gridy = 8;
        rightPane.add(refresh, c);

        rightPane.setBackground(Color.decode("#758078"));
        
        

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

    public void start(){
        if(reg){
            try{
                fenetre.getClient().reqStart();
                fenetre.setScene("attente");
            } catch (IOException e){
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

    public static boolean isAlphaNum(String str) {
        str = str.toLowerCase();
        for (int i = 0; i < str.length(); ++i) {
            char alpha = str.charAt(i);
            if (!((alpha >= 'a' && alpha <= 'z') || (alpha >= '0' && alpha <= '9'))) {
                return false;
            }
        }
        return true;
    }
    
}
