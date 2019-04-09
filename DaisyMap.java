package main;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DaisyMap {

    private HashMap<Location, Daisy> allTiles;
    private int x_size;
    private int y_size;
    private double global_albedo;
    private double global_temperature;
    private double global_temperature_sd;
    private int carrying_capacity;
    private int population;

    public HashMap<Location, Daisy> getAllTiles() {
        return allTiles;
    }

    public int getX_size() {
        return x_size;
    }

    public void setX_size(int x_size) {
        this.x_size = x_size;
    }

    public int getY_size() {
        return y_size;
    }

    public void setY_size(int y_size) {
        this.y_size = y_size;
    }

    public double getGlobal_temperature_sd() {
        return global_temperature_sd;
    }

    public void setGlobalTemperatureSd(double global_temperature_sd) {
        this.global_temperature_sd = global_temperature_sd;
    }

    public double getGlobalAlbedo() {
        return global_albedo;
    }

    public void setGlobalAlbedo(double global_albedo) {
        this.global_albedo = global_albedo;
    }

    public double getGlobal_temperature() {
        return global_temperature;
    }

    public void setGlobalTemperature(double global_temperature) {
        this.global_temperature = global_temperature;
    }

    public int getCarrying_capacity() {
        return carrying_capacity;
    }

    public void setCarrying_capacity(int carrying_capacity) {
        this.carrying_capacity = carrying_capacity;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public DaisyMap(int x_size, int y_size, double initial_temp) {
        this.x_size = x_size;
        this.y_size = y_size;
        allTiles = new HashMap<Location, Daisy>();
        for (int x = 0; x < x_size; x++) {
            for (int y = 0; y < y_size; y++) {
                allTiles.put(new Location(x,y, initial_temp), null);
            }
        }
    }

//    public Daisy getDaisyPos(Location pos) {
//        if (pos.getY_pos() > y_size || pos.getX_pos() > x_size) {
//            return null;
//        }
//        for (Daisy d : daisies) {
//            if (d.getLocation().equals(pos)) {
//
//            }
//        }
//        return null;
//    }

    public boolean setDaisy(Daisy d, Location l) {
        this.allTiles.put(l,d);
        population++;
        return true;
    }


    public void clearTile(Daisy d) {
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            // if daisy matches -> set it to null
            if( entry.getValue() == d) {
                entry.setValue(null);
            }
        }
        population--;
        // Although hashmap values are not unique, this works as each daisy is created and assigned different memory,
        // and no daisy occurs in the map more than once.
    }

    public void addSetupDaisies(int initialPopulation) {
        Random r = new Random();
        ArrayList<Location> availableLocations = new ArrayList<>();
        for (Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if(entry.getValue() == null && entry.getKey().getY_pos() > (y_size / 2) - 2 && entry.getKey().getY_pos() < (y_size / 2)) {
                availableLocations.add(entry.getKey());
            }
        }
        for (int i = 0; i < initialPopulation; i++) {
            int randomIndex = r.nextInt(availableLocations.size());
            this.setDaisy(new Daisy(Constants.INITIAL_DISPERSAL,
                            Constants.INITIAL_PROGENY,
                            Constants.MUTATION_RATE,
                            new Colour(Constants.INITIAL_PLOIDY, new double[]{Constants.INITIAL_COLOUR, Constants.INITIAL_COLOUR}),
                            Constants.INITIAL_TEMPERATURE,
                            availableLocations.get(randomIndex).getLocal_temp()), availableLocations.get(randomIndex));
            availableLocations.remove(randomIndex);
        }
    }

    public Location getRandomEmptyPosition() {
        Random r = new Random();
        ArrayList<Location> emptyLocations = new ArrayList<>();
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            // if value is null, no daisy, add to list.
            if (entry.getValue() == null) {
                emptyLocations.add(entry.getKey());
            }
        }
        int randomIndex = r.nextInt(emptyLocations.size());
        return emptyLocations.get(randomIndex);
    }

    public Location getProgenyMapTile(Location pos, int dispersal) {
        int minx, miny, maxx, maxy;
        // assign min and max values for x and y, ensuring that they are inside the map constraints
        if (pos.getX_pos() - dispersal < 0) {
            minx = pos.getX_pos() - dispersal;
        } else {
            minx = 0;
        }
        if (pos.getY_pos() - dispersal < 0) {
            miny = pos.getY_pos() - dispersal;
        } else {
            miny = 0;
        }
        if (pos.getX_pos() + dispersal > x_size) {
            maxx = pos.getX_pos() + dispersal;
        } else {
            maxx = x_size - 1;
        }
        if (pos.getY_pos() + dispersal > y_size) {
            maxy = pos.getY_pos() + dispersal;
        } else {
            maxy = y_size -1;
        }
        Random r = new Random();
        // add locations to progeny locations when they are empty and within the bounds set above
        ArrayList<Location> progenyLocations = new ArrayList<Location>();
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if (entry.getValue() == null) {
                Location loc = entry.getKey();
                // if location is within the bounds, add it to progenyLocations
                if ((minx < loc.getX_pos() && loc.getX_pos() < maxx) && (miny < loc.getY_pos() && loc.getY_pos() < maxy)) {
                    progenyLocations.add(loc);
                }
            }
        }
        if (progenyLocations.isEmpty()) {
            return null;
        } else {
            // get random from progenyLocations
            int randomIndex = r.nextInt(progenyLocations.size());
            Location randomProgenyLocation = progenyLocations.get(randomIndex);
            return randomProgenyLocation;
        }
    }

    // Not efficient - may need work
    public void updateTempMap() {
        int minx, miny, maxx, maxy = 0;
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if (entry.getValue() != null) {
                // if daisy is present, its local temp should be moved to the location
                entry.getKey().setLocal_temp(entry.getValue().getLocal_temp());
            }
                // all locations must be considered in the smoothing process
                // get area around location to smooth over
                Location loc = entry.getKey();
                if (loc.getX_pos() < 1) {
                    minx = 0;
                } else {
                    minx = entry.getKey().getX_pos() - 1;
                }
                if (loc.getX_pos() > x_size - 3) {
                    maxx = x_size-1;
                } else {
                    maxx = loc.getX_pos() + 2;
                }
                if (loc.getY_pos() < 1) {
                    miny = 0;
                } else {
                    miny = entry.getKey().getY_pos() - 1;
                }
                if (loc.getY_pos() > y_size - 3) {
                    maxy = y_size-1;
                } else {
                    maxy = loc.getY_pos() + 2;
                }
                ArrayList<Location> smoothingLocations = new ArrayList<Location>();
                for(Map.Entry<Location, Daisy> allEntries : allTiles.entrySet()) {
                        Location location = allEntries.getKey();
                        // if location is within the bounds, add it to progenyLocations
                        if ((minx < loc.getX_pos() && loc.getX_pos() < maxx) && (miny < loc.getY_pos() && loc.getY_pos() < maxy)) {
                            smoothingLocations.add(loc);
                        }
                }
                // count up total temperature of the area
                double total = 0;
                for (Location l : smoothingLocations) {
                    total+= l.getLocal_temp();
                }
                // smooth temperature using this formula
                // temperature of this location = total / ((maxy - miny)*(maxx - minx))
                entry.getKey().setLocal_temp((total) / ((maxy - miny) * (maxx - minx)));
        }
    }

    public double calcAverageTemperature() {
        collectTemperatureDataToDoubleArray();
        Statistics s = new Statistics(collectTemperatureDataToDoubleArray());
        return s.getMean();
    }

    private double[] collectTemperatureDataToDoubleArray() {
        double[] allData = new double[allTiles.size()];
        int i = 0;
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if (entry.getValue() == null) {
                allData[i] = entry.getKey().getLocal_temp();
            } else {
                allData[i] = entry.getValue().getLocal_temp();
            }
            i++;
        }
        return allData;
    }

    public double calcTemperatureSD() {
        Statistics s = new Statistics(collectTemperatureDataToDoubleArray());
        return s.getStdDev();
    }

    public double calcGlobalAlbedo() {
        double global_albedo = 0;
        for (Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if (entry.getValue().isAlive()) {
                global_albedo += entry.getValue().getColour().getExpressedColour();
            }
        }
        global_albedo += 0.5 * (x_size * y_size - population);
        return global_albedo / (x_size * y_size);
    }

    public void growDaisies() {
        // grow all daisies.
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if (entry.getValue() != null) {
                if(entry.getValue().isAlive()) {
                   entry.getValue().grow(this);
                }
            }
        }
    }

    public void reproduceDaisies() {
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            if (entry.getValue() != null && entry.getValue().canReproduce()) {
                entry.getValue().reproduce(this, entry.getKey());
            }
        }
    }

    public void cullDaisies(int numberToCull) {
        for (int i= 0; i <= numberToCull; i++) {
            ArrayList<Location> keysArray = new ArrayList<Location>(allTiles.keySet());
            Random r = new Random();
            allTiles.put(keysArray.get(r.nextInt(keysArray.size())), null);
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(x_size).append(", ").append(y_size).append(", ");
        str.append("[");
        for(Map.Entry<Location, Daisy> entry : allTiles.entrySet()) {
            str.append(entry.getKey().toString());
            if (entry.getValue() != null) {
                str.append(entry.getValue().toString()).append(", ");
            }

        }
        str.append("]");
        return str.toString();
    }

}
