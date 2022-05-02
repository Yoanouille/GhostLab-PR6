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



public class MenuPartie extends JPanel{

    
    private Fenetre fenetre;

    private int [] [] data = new int [20] [20];



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



    public MenuPartie(Fenetre fe){
        GridLayout gridLayout = new GridLayout(1,2);

        this.setLayout(gridLayout);
       

        JPanel rightPane = new JPanel();
        rightPane.setLayout(new BorderLayout());

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j]=(j+i)%2;
            }
        }

        data[5][2] = 3;

        PanneauJeu plateau = new PanneauJeu(data);

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
    }

    private void nextMove(String s,JLabel label){
        if(label.getText().contains(s)){
            label.setText(label.getText() + s);
        }else{
            label.setText(s);
        }
        
    }


    private class PanneauJeu extends JPanel {

        int[][] cases;

        public PanneauJeu(final int[][] cases) {

            this.cases = cases;

        }

        @Override
        public void paintComponent(final Graphics g) {

            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();

            for (int i = 0; i < cases.length; i++) {
                for (int j = 0; j < cases[i].length; j++) {

                    int currentCase = cases[i][j];
                    Color c;
                    switch (currentCase) {
                        case 0:
                            c = Color.WHITE;
                            break;
                    
                        case 1:
                            c = Color.BLACK;
                            break;
                    
                        default:
                            c = Color.BLUE;
                            break;
                    }
                    g.setColor(c);
                    g.fillRect(i * width / cases.length, j * height / cases[i].length, width / cases.length,
                            height / cases[i].length);

                }
            }

        }

    }
    
}
