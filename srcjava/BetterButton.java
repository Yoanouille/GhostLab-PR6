package srcjava;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.plaf.DimensionUIResource;

public class BetterButton extends JButton {
    public BetterButton(String text) {
        super(text);

        this.setSize(100,50);

        this.setFont(new Font("Verdana", Font.PLAIN,  20));
        this.setForeground(Color.white);
        this.setBackground(Color.decode("#003678"));
        //this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(Color.decode("#003678"));
                setBackground(Color.white);
                
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(Color.white);
                setBackground(Color.decode("#003678"));
                
            }
            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
    }

     
}
