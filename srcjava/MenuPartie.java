package srcjava;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

import java.io.IOException;
import java.time.chrono.HijrahEra;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import srcjava.Fenetre;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.awt.*;
import javax.swing.*;

//Classe ou le jeu est afficher et le menu permettant les deplacements et la communication entre les joueurs
public class MenuPartie extends JPanel {

    //liste des deplacements pour l'affichage correct des murs
    private LinkedList<Integer> queueMovelen = new LinkedList<Integer>();
    private LinkedList<Integer> queueMoveDir = new LinkedList<Integer>();
    
    //liste des joueurs dans la partie
    public DefaultListModel<String> players = new DefaultListModel<String>();
    private JList<String> player_list = new JList<String>(players);

    public JLabel historic = new JLabel("<html></html>");
    private JScrollPane historic_ScrollPane = new JScrollPane(historic);

    //liste des objets temporaire à dessiner (fantome et joueur)
    private HashSet<PosOp> setOfDraw = new HashSet<>();
    
    private String my_id;
    
    private Fenetre fenetre;

    private PanneauJeu plateau = new PanneauJeu();

    private boolean isRunning = true;

    //plateau du jeu
    private int [] [] data;

    private int x = -1;
    private int y = -1;


    public MenuPartie(Fenetre fe,int width, int height){
        this.fenetre = fe;
        data = new int[width] [height];

        if(height >= width) {
            double ratio = width * 1.0 / height;
            fe.setSize((int)(fe.getHeight() * ratio * 2), fe.getHeight());
        } else {
            double ratio = width * 1.0 / height;
            fe.setSize(fe.getWidth(), (int)(fe.getWidth() / (ratio * 2)));
        }

        GridLayout gridLayout = new GridLayout(1,2);

        this.setLayout(gridLayout);
       

        JPanel rightPane = new JPanel();
        rightPane.setLayout(new BorderLayout());

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j]=0;
            }
        }

        //DIRECTION------------------------------------------------------------

        JPanel cross = new JPanel();
        cross.setAlignmentY(Component.CENTER_ALIGNMENT);
        cross.setLayout(new BoxLayout(cross,BoxLayout.Y_AXIS));


        JPanel d_cross = new JPanel();
        d_cross.setLayout(new BoxLayout(d_cross,BoxLayout.X_AXIS));

        BetterButton up = new BetterButton("\u2191");
        up.setAlignmentX(Component.CENTER_ALIGNMENT);
        up.setFont(new Font("arial", Font.PLAIN,  20));

        BetterButton down = new BetterButton("\u2193");
        down.setAlignmentX(Component.CENTER_ALIGNMENT);
        down.setFont(new Font("arial", Font.PLAIN,  20));

        BetterButton right = new BetterButton("\u2192");
        right.setFont(new Font("arial", Font.PLAIN,  20));
        BetterButton left = new BetterButton("\u2190");
        left.setFont(new Font("arial", Font.PLAIN,  20));
        JLabel nextMove = new JLabel(" ");
        nextMove.setFont(new Font("arial", Font.PLAIN,  20));
        nextMove.setAlignmentX(Component.CENTER_ALIGNMENT);
       

        BetterButton move = new BetterButton("Move");
        move.setAlignmentX(Component.CENTER_ALIGNMENT);

        up.addActionListener((ActionEvent e) -> {
            nextMove("\u2191",nextMove);
            this.requestFocus();
            this.requestFocusInWindow();
        });
        right.addActionListener((ActionEvent e) -> {
            nextMove("\u2192",nextMove);
            this.requestFocus();
            this.requestFocusInWindow();
        });
        down.addActionListener((ActionEvent e) -> {
            nextMove("\u2193",nextMove);
            this.requestFocus();
            this.requestFocusInWindow();
        });
        left.addActionListener((ActionEvent e) -> {
            nextMove("\u2190",nextMove);
            this.requestFocus();
            this.requestFocusInWindow();
        });

        move.addActionListener((ActionEvent e) -> {
            move(nextMove);
            this.requestFocus();
            this.requestFocusInWindow();
        });

        d_cross.add(left);
        d_cross.add(down);
        d_cross.add(right);


        cross.add(up);
        cross.add(d_cross);
        cross.add(nextMove);
        cross.add(move);
        cross.setBorder(new EmptyBorder(50, 10, 10, 10));
        rightPane.add(cross,BorderLayout.CENTER);
        


        //CHAT-----------------------------------------------------------------

        JPanel chatMenu = new JPanel();
        chatMenu.setLayout(new BoxLayout(chatMenu,BoxLayout.Y_AXIS));

        player_list.setFont(new Font("Arial",Font.BOLD,17));
        historic.setFont(new Font("Arial",Font.BOLD,17));
        JScrollPane list = new JScrollPane(player_list);

        BetterButton actu = new BetterButton("actualiser");
        actu.addActionListener((ActionEvent e) -> {
            System.out.println(my_id);
            refresh();
            this.requestFocus();
            this.requestFocusInWindow();
        });
        actu.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField message = new JTextField();

        message.setDocument(new JTextFieldLimit(200));
        message.setAutoscrolls(true);
        
        
        BetterButton send_to = new BetterButton("Send to :");
        send_to.setAlignmentX(Component.CENTER_ALIGNMENT);
        send_to.addActionListener((ActionEvent e) -> {
            send_to_player(message);
            this.requestFocus();
            this.requestFocusInWindow();
        });

        BetterButton send_all = new BetterButton("Send to all");
        send_all.setAlignmentX(Component.CENTER_ALIGNMENT);
        send_all.addActionListener((ActionEvent e) -> {
            send_to_all(message);
            this.requestFocus();
            this.requestFocusInWindow();
        });

        JPanel buttons_send = new JPanel();
        buttons_send.setLayout(new BoxLayout(buttons_send,BoxLayout.X_AXIS));
        buttons_send.add(send_all);
        buttons_send.add(send_to);
        
        JPanel players_and_chatbox = new JPanel();
        
        players_and_chatbox.setLayout(new GridLayout(1,0));
        players_and_chatbox.add(list);
    

        players_and_chatbox.add(historic_ScrollPane);
        
        chatMenu.add(actu);

        chatMenu.add(players_and_chatbox);
        chatMenu.add(message);
        chatMenu.add(buttons_send);


        

        rightPane.add(chatMenu,BorderLayout.NORTH);

        BetterButton Quit = new BetterButton("Quit");
        Quit.addActionListener((ActionEvent e) -> {
            try {
                fenetre.getClient().reqQuit();
                fenetre.setEnd("Vous avez quitté !");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            this.requestFocus();
            this.requestFocusInWindow();
        });
        Quit.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPane.add(Quit,BorderLayout.SOUTH);

        
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocus();
                requestFocusInWindow();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
        });
        

        this.add(plateau);
        this.add(rightPane);

        this.setVisible(true);
        refresh();
        this.updateUI();
        new Thread (() -> {
            while(isRunning){
                SwingUtilities.invokeLater(() -> {
                    plateau.repaint();
                    Toolkit.getDefaultToolkit().sync();
                });
                try {
                    Thread.sleep(1000/30);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                

            }
        }).start();;

        setFocusable(true);
        this.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println(e.getKeyCode());
                switch(e.getKeyCode()) {
                    case 38:
                        nextMove("\u2191",nextMove);
                        break;
                    case 39:
                        nextMove("\u2192",nextMove);
                        break;
                    case 40:
                        nextMove("\u2193",nextMove);
                        break;

                    case 37:
                        nextMove("\u2190",nextMove);
                        break;

                    case 10:
                        move(nextMove);
                        break;
                }

                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                
            }

            @Override
            public void keyTyped(KeyEvent e) {
                
            }
            
        });
    }

    //fonction qui permet l'acutalisation du score d'un joueur
    public void setScore(String id, int score){
       for(int i = 0; i < players.size();i++){
           if(id.equals(players.getElementAt(i).substring(0, 8))){
               players.setElementAt(id + " points: " + score, i);
           }
       }
    }

    //fonction qui stop l'acutalisation du Thread qui dessine le jeu
    public void stop() {
        isRunning = false;
    }

    //fonction qui ajoute un element temporaire a dessiner(fantome ou joueur)
    public void addPosToDraw(int x, int y, String type) {
        synchronized(setOfDraw) {
            if(type.equals("ghost")) {
                PosOp p = new PosOp(x, y, Color.GREEN);
                setOfDraw.add(p);
            } else if(type.equals("player")) {
                PosOp p = new PosOp(x, y, Color.RED);
                setOfDraw.add(p);
            }
        }
    }

    //fonction qui ajoute un piege aux coordonees idiquee
    public void addTrap(int x, int y) {
        synchronized(data) {
            data[y][x] = 3;
        }
    }

    //fonction qui demande l'envoie du message SEND? 
    private void send_to_player(JTextField text){
        if(player_list.getSelectedValue() != null){
            String g = player_list.getSelectedValue();
            String id = g.substring(0,8);
            try {
                fenetre.getClient().reqSend(id,text.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //fonction qui demande l'envoie du message MALL?
    private void send_to_all(JTextField text){
        try {
            fenetre.getClient().reqMall(text.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //focntion qui demande l'envoie du message GLIS?
    private void refresh(){
        try {
            fenetre.getClient().reqGlis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //fonction qui permet d'afficher les fleches et de repeter plusiseurs foix la meme
    private void nextMove(String s,JLabel label){
        if(label.getText().contains(s)){
            label.setText(label.getText() + s);
        }else{
            label.setText(s);
        }
        
    }
    
    //fonction qui demande l'envoie du message de type **MOV, 
    // determine quel type de **MOV envoyer et sauvegarde la direction 
    private void move(JLabel nextMove){
        int dir;
        switch (nextMove.getText().charAt(0)) {
            case '\u2191':
                dir = 0;
                break;
            case '\u2193':
                dir = 1;
                break;
            case '\u2190':
                dir = 2;
                break;
            case '\u2192':
                dir = 3;
                break;
        
            default:
                dir = -1;
                break;
        }
        if(dir != -1){
            try {
                queueMovelen.push(nextMove.getText().length());
                queueMoveDir.push(dir);
                fenetre.getClient().reqMov(dir,nextMove.getText().length());  
                nextMove.setText(nextMove.getText().substring(0,1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
    }


    //fonction qui permet de deplacer le joueur a la bonne case dans le plateau
    // et change les cases parcourues en case libre et s'il y a eu collision ajoute un mur dans le plateau
    public void setJoueur(int x, int y){
        if(this.x == -1 && this.y == -1){
            data[y][x] = 1;
        }else {
            int len = Math.abs(this.x - x + this.y - y);
            int dx = 0;
            int dy = 0; 
            int dir = queueMoveDir.pop();
            switch(dir) {
                case 0 : 
                    dx = -1;
                    dy = 0;
                    break;
                case 1 : 
                    dx = 1;
                    dy = 0;
                    break;
                case 2 :
                    dx = 0;
                    dy = -1;
                    break;
                case 3 :
                    dx = 0;
                    dy = 1;
                    break;
            }
            
            synchronized(data) {
                for(int i = 0; i < len; i++){
                    if(data[this.y + (i*dy)][this.x + (i*dx)]== 0) data[this.y+(i*dy)][this.x+(i*dx)] = 1;
                }
                if (len < queueMovelen.pop()){
                    if(this.x+(len+1)*dx >= 0 && this.x+(len+1)*dx < data[y].length && this.y+(len+1)*dy >= 0 && this.y+(len+1)*dy < data.length)
                    data[this.y+(len+1)*dy][this.x+(len+1)*dx]=2;
                }
            }
        }
        this.x = x;
        this.y = y;
        //plateau.repaint();
    }

    //fonction pour changer l'id
    public void setId(String id){
        this.my_id = id;
    }

    //fonction pour changer le score
    public void setMyScore(int score){
        setScore(my_id,score);
    }
    

    //classe interne pour dessiner le plateau de jeu
    private class PanneauJeu extends JPanel {
    
        
        public PanneauJeu() {
            super();
        }

        //Fonction qui peint le plateau en fonction des cases du plateau et des elements
        // temporaires dans setOfDraw (elle est appelee dans le thread d'actualisation de l'interface)
        @Override
        public void paintComponent(final Graphics g) {

            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();

            //dessin du plateau
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {

                    int currentCase = data[i][j];
                    Color c;
                    switch (currentCase) {
                        case 0:
                            c = Color.GRAY;
                            break;
                    
                        case 1:
                            c = Color.WHITE;
                            break;
                        case 2:
                            c = Color.BLACK;
                            break;
                   
                        default:
                            c = Color.ORANGE;
                            break;
                    }

                    if(currentCase != 3) {
                        if (i == y && j == x){
                            g.setColor(Color.BLUE);
                            g.fillRect(y * width / data.length + 1, x * height / data[y].length + 1, width / data.length -2, height / data[y].length -2);
                        }else {
                            g.setColor(c);
                            g.fillRect(i * width / data.length + 1, j * height / data[i].length + 1, width / data.length -2, height / data[i].length -2);
                        }
                    }else{ 
                        if(currentCase == 3){
                            if (i == y && j == x){
                                g.setColor(Color.BLUE);
                                g.fillRect(y * width / data.length + 1, x * height / data[y].length + 1, width / data.length -2, height / data[y].length -2);
                                g.setColor(new Color(0,0,0,150));
                                int sclW = (width)/ data.length;
                                int sclH = (height) / data[i].length;
                                g.fillRect(i * sclW + sclW / 4, j * sclH + sclH / 4, sclW / 2, sclH / 2);
                            } else {
                                g.setColor(Color.white);
                                g.fillRect(i * width / data.length + 1, j * height / data[i].length + 1, width / data.length -2, height / data[i].length -2);
                                g.setColor(Color.BLACK);
                                int sclW = (width)/ data.length;
                                int sclH = (height) / data[i].length;
                                g.fillRect(i * sclW + sclW / 4, j * sclH + sclH / 4, sclW / 2, sclH / 2);
                            }
      
                        }
                    }
                } 
            }

            //dessin des elements temporaire
            synchronized(setOfDraw) {
                LinkedList<PosOp> elt_rm = new LinkedList<>();
                for(PosOp p : setOfDraw) {
                    Color c = p.getC();
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), p.getOpacity()));
                    g.fillRect(p.getY() * width / data.length + 3, p.getX() *  height / data[p.getY()].length + 3 , width / data.length - 6, height / data[p.getY()].length - 6);
                    if(p.reduceOp()) elt_rm.add(p);
                    if(data[p.getY()][p.getX()] == 3 ){
                        g.setColor(new Color(0,0,0,150));
                        int sclW = (width)/ data.length;
                        int sclH = (height) / data[p.getY()].length;
                        g.fillRect(p.getY() * sclW + sclW / 4, p.getX()* sclH + sclH / 4, sclW / 2, sclH / 2);  
                    }

                }
                for(PosOp elt : elt_rm) {
                    setOfDraw.remove(elt);
                }
            }
        }

    }

    //classe interne pour limiter le nombre caracteres maximum du chat et empecher les + et les * dans le message
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
      
         if ((getLength() + str.length()) <= limit && !str.contains("*") && !str.contains("+")) {
           if (toUppercase) str = str.toUpperCase();
                super.insertString(offset, str, attr);
           }
        }
    }
}
