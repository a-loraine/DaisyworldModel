package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class Simulation {

    private DaisyMap daisyMap;
    private int xSize;
    private int ySize;
    private int simLength;
    private int currentStep;
    private int initialPopulation;

    // default simulation
    public Simulation() {
        // create daisyMap
        daisyMap = new DaisyMap(xSize, ySize, 25);
        daisyMap.addSetupDaisies(Constants.INITIAL_POPULATION);

    }

    // Method to simulate the passing of a timestep
    public void run() {
        double globalAlbedo = daisyMap.getGlobalAlbedo();
        daisyMap.setGlobalAlbedo(daisyMap.calcGlobalAlbedo());
        // calculate global albedo
        daisyMap.setGlobalTemperature(daisyMap.calcAverageTemperature());
        // calculate temperature stdev
        daisyMap.setGlobalTemperatureSd(daisyMap.calcTemperatureSD());
        // output data pre step
        output();
        // Actual Simulation code starts here
        // grow all daisies and collect resources
        daisyMap.growDaisies();
        // perform reproduction
        daisyMap.reproduceDaisies();

        // cull daisies if over capacity
        if (daisyMap.getCarrying_capacity() > daisyMap.getPopulation()) {
            daisyMap.cullDaisies(daisyMap.getPopulation() - daisyMap.getCarrying_capacity());
        }
        currentStep++;
    }
    public void output() {
        double white = 0.0, black =0.0 , grey = 0.0, avgOptTemp = 0.0, avgColour = 0.0;
        for (Map.Entry<Location, Daisy> entry : daisyMap.getAllTiles().entrySet()) {
            if (entry.getValue().getColour().getExpressedColour() > 0.55) {
                white++;
            } else if (entry.getValue().getColour().getExpressedColour() < 0.45) {
                black++;
            } else {
                grey++;
            }
            avgOptTemp += entry.getValue().getOptimum();
            avgColour += entry.getValue().getColour().getExpressedColour();
        }
        avgOptTemp = avgOptTemp /  daisyMap.getAllTiles().size();
        avgColour = avgColour / daisyMap.getAllTiles().size();
        // add these to a file.
        StringBuilder str = new StringBuilder();
        str.append(currentStep).append(daisyMap.getGlobal_temperature()).append(",").append(daisyMap.getGlobalAlbedo());
        str.append(",").append(daisyMap.getGlobal_temperature_sd()).append(",").append(white).append(",").append(black).append(",");
        str.append(grey).append(",").append(avgOptTemp).append(",").append(avgColour);
        // TODO: add this string to a file.
        writeToFile("outputFile.txt", str.toString());
    }
    public void writeToFile(String filePath, String content) {
        try (
            //Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.APPEND);
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(content);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
