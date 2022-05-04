import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class KeyListenerExample implements KeyListener
{
	@Override
	public void keyTyped(KeyEvent e)
	{
		System.out.println("The key Typed was: " + e.getKeyChar());
	}
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.isActionKey())
			System.exit(0);
		System.out.println("The key Pressed was: " + e.getKeyChar());
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		System.out.println("The key Released was: " + e.getKeyChar());
	}
	public static void main(String[] args)
	{
		//Setting the Frame and Labels
		Frame f = new Frame("Demo");
		f.setLayout(new FlowLayout());
		f.setSize(500, 500);
		Label l = new Label();
		l.setText("This is a demonstration");
		f.add(l);
		f.setVisible(true);
		
		//Creating and adding the key listener
		KeyListenerExample k = new KeyListenerExample();
		f.addKeyListener(k);
	}
}