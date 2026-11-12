package gunging.ootilities.gunging_ootilities_plugin.misc;

/**
 * Holds two integer numbers
 */
public class IntVector2 {

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    int x, y;

    public IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double magnitude() { return Math.sqrt(x * x + y * y); }
}
