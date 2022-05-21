package srcjava;
import java.awt.Color;

//petite classe pour stocker les valeurs des elements temporaires
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

    //fonction qui diminue l'opacite et indique si elle est negative
    public boolean reduceOp() {
        opacity -= 3;
        return (opacity <= 0); 
    }

    //getter de la couleur
    public Color getC() {
        return c;
    }

    //getter de l'opacite
    public int getOpacity() {
        return opacity;
    }

    //getter de x
    public int getX() {
        return x;
    }

    //getteur de y
    public int getY() {
        return y;
    }
}
