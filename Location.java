package main;

/**
 * A position Class to denote a position in the map of daisyworld
 * Contains X and Y co-ordinates
 */
public class Location {

    private int x_pos;

    public int getY_pos() {
        return y_pos;
    }

    public void setY_pos(int y_pos) {
        this.y_pos = y_pos;
    }

    private int y_pos;

    public int getX_pos() {
        return x_pos;
    }

    public void setX_pos(int x_pos) {
        this.x_pos = x_pos;
    }

    private double local_temp;

    public double getLocal_temp() {
        return local_temp;
    }

    public void setLocal_temp(double local_temp) {
        this.local_temp = local_temp;
    }

    public Location(int x, int y, double local_temp) {
        this.x_pos = x;
        this.y_pos = y;
        this.local_temp = local_temp;
    }

    public boolean equals(Location pos) {
        return (pos.getX_pos() == this.x_pos && pos.getY_pos() == this.y_pos);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("X: " + x_pos).append(", ").append("Y: " + y_pos).append(", ");
        str.append("TEMP: " + local_temp).append(", ");
        return str.toString();
    }
}
