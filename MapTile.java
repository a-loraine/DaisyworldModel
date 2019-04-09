package main;
// NOT CURRENTLY USED
public class MapTile {

    private Location location;
    private Daisy daisy;
    private double local_temp;

    public MapTile(Location pos) {
        this.location = pos;
        this.daisy = null;
    }

    public void setDaisy(Daisy daisy) {
        this.daisy = daisy;
    }

    public MapTile(Location pos, Daisy daisy) {
        this.daisy = daisy;
        this.location = pos;
    }

    public Location getLocation() {
        return this.location;
    }
    public boolean isEmpty() {
        return this.daisy == null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(location.toString()).append(", ");
        if (daisy != null) {
            str.append(daisy.toString());
        }
        return str.toString();
    }
}
