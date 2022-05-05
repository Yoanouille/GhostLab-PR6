package srcjava;
import java.awt.Color;

public class PosOp {
    private int x;
    private int y;
    private Color c;   
    private int opacity;

    public PosOp(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        this.c = c;
        opacity = 255;
    }

    public boolean reduceOp() {
        opacity -= 3;
        return (opacity <= 0); 
    }

    public Color getC() {
        return c;
    }

    public int getOpacity() {
        return opacity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
