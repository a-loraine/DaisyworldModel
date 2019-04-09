package main;


import java.util.Arrays;
import java.util.Map;
import java.util.Random;


public class Daisy {

    //private Location position;
    private int dispersal;
    private int progeny;
    private int age;
    private int generation;
    private boolean alive;
    private double mutation_rate;
    // DAE bits
    //private int switchs;
    //private int current;
    // this represents the alleles for colour and the expressed one
    private Colour colour;
    // this represents the alleles for temperature
    private double[] temp_alleles;
    private double local_temp;

    private double resources;

    public int getPloidy() {
        return ploidy;
    }

    public void setPloidy(int ploidy) {
        this.ploidy = ploidy;
    }

    private int ploidy;



    //<editor-fold desc="Getters and Setters">
//    public Location getLocation() {
//        return position;
//    }
//
//    public void setPosition(Location position) {
//        this.position = position;
//    }

    public int getDispersal() {
        return dispersal;
    }

    public void setDispersal(int dispersal) {
        this.dispersal = dispersal;
    }

    public int getProgeny() {
        return progeny;
    }

    public void setProgeny(int progeny) {
        this.progeny = progeny;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getMutation_rate() {
        return mutation_rate;
    }

    public void setMutation_rate(float mutation_rate) {
        this.mutation_rate = mutation_rate;
    }

//    public int getSwitchs() {
//        return switchs;
//    }
//
//    public void setSwitchs(int switchs) {
//        this.switchs = switchs;
//    }
//
//    public int getCurrent() {
//        return current;
//    }
//
//    public void setCurrent(int current) {
//        this.current = current;
//    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public double[] getOpt_temp() {
        return temp_alleles;
    }

    public void setOpt_temp(double[] opt_temp) {
            this.temp_alleles = opt_temp;
    }

    public double getLocal_temp() {
        return local_temp;
    }

    public void setLocal_temp(float local_temp) {
        this.local_temp = local_temp;
    }

    public double getResources() {
        return resources;
    }

    public void setResources(float resources) {
        this.resources = resources;
    }
    //</editor-fold>
    // Constructor to create progeny from parent
    public Daisy(Daisy parent) {
        //this.position = parent.position;
        this.dispersal = parent.dispersal;
        this.progeny = parent.progeny;
        this.age = 0;
        this.ploidy = parent.ploidy;
        this.generation = parent.generation + 1;
        this.alive = parent.alive;
        this.mutation_rate = parent.mutation_rate;
//        this.switchs = 0;
//        this.current = 0;
        this.colour = parent.colour;
        this.temp_alleles = parent.temp_alleles;
        this.local_temp = parent.local_temp;
    }
    // generic constructor
    public Daisy(int dispersal, int progeny, double mut_rate, Colour colour, double[] temp_alleles, double local_temp) {
        this.dispersal = dispersal;
        this.progeny = progeny;
        this.age = 0;
        this.ploidy = 2;
        this.generation = 0;
        this.alive = true;
        this.mutation_rate = mut_rate;
        this.colour = colour;
        this.temp_alleles = temp_alleles;
        this.local_temp = local_temp;
        this.resources = new Random().nextInt(5);
    }

    /**
     * Method to grow the daisy and recalculate its resources
     * @param daisyMap daisyMap of daisies
     */
   public double grow(DaisyMap daisyMap) {
        // increment age
        this.age++;
        if (this.age > Constants.AGE_OF_DEATH) {
            this.alive = false;
            // remove daisy from tile and make sure daisyMap updates
            daisyMap.clearTile(this);
            return -1.0;
        }
        double albedo_temp = Math.pow((Constants.SOLAR_INTENSITY * (radiation_factor(daisyMap)/ daisyMap.getY_size()) * 1-this.colour.getExpressedColour())/(4*Constants.SIGMA_CONSTANT),1/4);
        albedo_temp = albedo_temp + 25 - 234;
        this.local_temp = 0.7 * albedo_temp + 0.3 * local_temp;
        double delta_resources = (5-Math.pow(local_temp - getOptimum(), 2));
        resources = resources + delta_resources;
        return resources;
   }

   public Daisy[] reproduce(DaisyMap m, Location l) {
       Daisy[] children = new Daisy[this.progeny];
       for(int i=0; i < this.progeny; i++) {
           // create daisy and assign its position
           Daisy child = new Daisy(this);
           Location newPos = m.getProgenyMapTile(l, this.dispersal);
           if (newPos != null) {
               // assign daisy to position
               //child.setPosition(newPos);

               // add daisy to map
               m.setDaisy(child, newPos);
           } else {// else break - no more space for children
               break;
           }
           child.resources = this.resources / (this.progeny + 1);
            // for each allele in colour, mutate
           child.mutateColour();
           // for each allele in temperature, mutate
           child.mutateTemp();
           // increase ploidy randomly
           Random r = new Random();
           if((1 + r.nextInt(1000)) < (Constants.PLOIDY_MUTATION_RATE * 1000)) {
               child.increasePloidy();
           }
           children[i]= child;
       }
       return children;
   }

   public void increasePloidy() {
       this.ploidy = this.ploidy * 2;
       double[] temp = temp_alleles;
       // double size of array
       temp_alleles = new double[temp.length * 2];
       // copy array contents into new one twice. [a,b] turns to [a,b,a,b]
       System.arraycopy(temp, 0, temp_alleles, 0, temp.length);
       System.arraycopy(temp, 0, temp_alleles, temp.length, temp.length);
   }

   public void mutateColour() {
       this.colour.mutate();
   }

   public void mutateTemp() {
       Random r = new Random();
       for (int i=0; i < temp_alleles.length; i++) {
           // if 1 in 1000 random chance is satisfied, mutate
           if((1 + r.nextInt(1000)) < (Constants.MUTATION_RATE *1000)) {
               // randomly plus or minus 0.05 as a mutation
               temp_alleles[i] += 0.1 * Math.pow(-1, r.nextInt(1));
           }
       }
   }

   public double getOptimum() {
       double closest = temp_alleles[0];
       double distance = Math.abs(temp_alleles[0]- local_temp);

       for (double a: temp_alleles) {
           // if distance between a and local temp is smaller than closest and local temp
           if ((Math.abs(a - local_temp)) < distance) {
               closest = a;
               distance = Math.abs(a-local_temp);
           }
       }
       return closest;
   }
   public double radiation_factor(DaisyMap m) {
       Location l = null;
       for(Map.Entry<Location, Daisy> entry : m.getAllTiles().entrySet()) {
           if( entry.getValue() == this) {
               l = entry.getKey();
           }
       }
     return (0.8 + (0.4 * l.getY_pos()));
   }

   public boolean canReproduce() {
       return resources  > Constants.RESOURCES_TO_REPRODUCE;
   }

   @Override
   public String toString() {
       StringBuilder str = new StringBuilder();
       str.append(local_temp).append(", ");
       str.append(Arrays.toString(this.temp_alleles)).append(", ").append(this.colour.toString()).append(", ");
       str.append(this.age).append(", ").append(this.ploidy);
       return str.toString();
   }




}
