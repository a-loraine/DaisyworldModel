package main;

import java.util.Arrays;
import java.util.Random;

/**
 * Class to represent the colour alleles of a daisy
 *
 */
public class Colour {
    private final double dominance = 0.5;
    private double[] alleles;
    private double expressedColour;

    public Colour(int ploidy){
        // create new array for colour alleles of the size of the ploidy
        alleles = new double[ploidy];
        for(int i=0; i < alleles.length; i++) {
            alleles[i] = 0.5;
        }
        calculateExpressedColour();
    }
    public Colour(int ploidy, double[] alleles) {
        this.alleles = alleles;
        calculateExpressedColour();
    }
    public void calculateExpressedColour() {
        double total = 0;
        for (double a : alleles) {
            total += a;
        }
        this.expressedColour = total / alleles.length;
    }

    public void mutate() {
        Random r = new Random();
        for (int i=0; i < alleles.length; i++) {
            // if 1 in 1000 random chance is satisfied, mutate
            if((1 + r.nextInt(1000)) < (Constants.MUTATION_RATE *1000)) {
                // randomly plus or minus 0.05 as a mutation
                alleles[i] += 0.05 * Math.pow(-1, r.nextInt(1));
            }
        }
    }

    public double[] getAlleles() {
        return alleles;
    }

    public double getExpressedColour() {
        return expressedColour;
    }

    @Override
    public String toString() {
        return Arrays.toString(alleles);
    }

}