package srcjava;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.plaf.DimensionUIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;



public class MenuPartie extends JPanel{

    private LinkedList<Integer> queueMovelen = new LinkedList<Integer>();
    private LinkedList<Integer> queueMoveDir = new LinkedList<Integer>();
    private Fenetre fenetre;

    private PanneauJeu plateau = new PanneauJeu();

    private int [] [] data;

    private int x = -1;
    private int y = -1;

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
      
         if ((getLength() + str.length()) <= limit) {
           if (toUppercase) str = str.toUpperCase();
                super.insertString(offset, str, attr);
           }
        }
    }



    public MenuPartie(Fenetre fe,int width, int height){
        data = new int[width] [height];
        GridLayout gridLayout = new GridLayout(1,2);

        this.setLayout(gridLayout);
       

        JPanel rightPane = new JPanel();
        rightPane.setLayout(new BorderLayout());

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j]=0;
            }
        }
        JTextField message = new JTextField();

        message.setDocument(new JTextFieldLimit(200));
        rightPane.add(message,BorderLayout.NORTH);

        JPanel cross = new JPanel();
        cross.setAlignmentY(Component.CENTER_ALIGNMENT);
        cross.setLayout(new BoxLayout(cross,BoxLayout.Y_AXIS));

        JPanel d_cross = new JPanel();
        d_cross.setLayout(new BoxLayout(d_cross,BoxLayout.X_AXIS));

        JButton up = new JButton("\u2191");
        up.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton down = new JButton("\u2193");
        down.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton right = new JButton("\u2192");
        JButton left = new JButton("\u2190");
        
        JLabel nextMove = new JLabel(" ");
        nextMove.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        JButton move = new JButton("Move");
        move.setAlignmentX(Component.CENTER_ALIGNMENT);

        up.addActionListener((ActionEvent e) -> {
            nextMove("\u2191",nextMove);
        });
        right.addActionListener((ActionEvent e) -> {
            nextMove("\u2192",nextMove);
        });
        down.addActionListener((ActionEvent e) -> {
            nextMove("\u2193",nextMove);
        });
        left.addActionListener((ActionEvent e) -> {
            nextMove("\u2190",nextMove);
        });

        move.addActionListener((ActionEvent e) -> {
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
                    fe.getClient().reqMov(dir,nextMove.getText().length());  
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }      
        });

        d_cross.add(left);
        d_cross.add(down);
        d_cross.add(right);

        cross.add(up);
        cross.add(d_cross);
        cross.add(nextMove);
        cross.add(move);



        rightPane.add(cross,BorderLayout.CENTER);

        

        this.add(plateau);
        this.add(rightPane);

        this.setVisible(true);
        this.updateUI();
        // actualisation
        // new Thread (() -> {
        //     while(true){
        //         plateau.repaint();
        //         try {
        //             Thread.sleep(1000/30);
        //         } catch (Exception e) {
        //             e.printStackTrace();
        //         }
                

        //     }
        // }).start();;
    }

    private void nextMove(String s,JLabel label){
        if(label.getText().contains(s)){
            label.setText(label.getText() + s);
        }else{
            label.setText(s);
        }
        
    }
    public void setJoueur(int x, int y){
        if(this.x == -1 && this.y == -1){
            data[x][y] = 1;
        }else {
            int len = Math.abs(this.x - x + this.y - y);
            int dx = 0;
            int dy = 0; 
            int dir = queueMoveDir.pop();
            switch(dir) {
                case 0 : 
                    dx = 0;
                    dy = -1;
                    break;
                case 1 : 
                    dx = 0;
                    dy = 1;
                    break;
                case 2 :
                    dx = -1;
                    dy = 0;
                    break;
                case 3 :
                    dx = 1;
                    dy = 0;
                    break;
            }

            
            for(int i = 0; i < len; i++){
                data[this.x+(i*dx)][this.y+(i*dy)] = 1;
            }
            if (len < queueMovelen.pop()){
                if(this.x+(len+1)*dx >= 0 && this.x+(len+1)*dx < data.length && this.y+(len+1)*dy >= 0 && this.y+(len+1)*dy < data[x].length)
                data[this.x+(len+1)*dx][this.y+(len+1)*dy]=2;
            }
        }
        this.x = x;
        this.y = y;
        plateau.repaint();
     }

    private class PanneauJeu extends JPanel {
        
        public PanneauJeu() {
            super();
        }

        @Override
        public void paintComponent(final Graphics g) {

            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();

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
                    g.setColor(c);
                    g.fillRect(i * width / data.length + 1, j * height / data[i].length + 1, width / data.length -2,
                            height / data[i].length -2);

                } 
            }
            if(x >= 0 && y >= 0){
                g.setColor(Color.BLUE);
                g.fillRect(x * width / data.length + 1, y * height / data[x].length + 1, width / data.length -2,
                height / data[x].length -2);
            }
        }
        


    }
    
}
