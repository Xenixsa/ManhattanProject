package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationEngine {

    private int width;
    private int height;
    private int[] grid;
    private List<Neutron> neutrons;
    private Random random = new Random();

    private List<Atom> atoms;
    private List<Neutron> pendingNeutrons = new ArrayList<>();  //lista pomocnicza, żeby nie psuć głownej pętli

    public SimulationEngine(int width, int height, int[] grid){
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.neutrons = new ArrayList<>();
    }

    public void fireNeutron(int startX, int startY, int releaseX, int releaseY){
        double dx = startX - releaseX; //obliczamy wektory kierunku naciagniecia
        double dy = startY - releaseY;
        double speedMultiplier = 0.05;
        dx =dx * speedMultiplier;
        dy =dy * speedMultiplier;
        addNeutron(startX, startY, dx, dy);
    }

    public void addNeutron(double x, double y,double dx, double dy){
        neutrons.add(new Neutron(x,y,dx,dy));
    }

    private void spawnNeutrons(int x,int y){
        int amount = 2 + random.nextInt(2); //losuje 2 albo 3
        if (neutrons.size() + pendingNeutrons.size() < 10000){ //limit neutronow
        for (int i=0; i<amount; i++){
            double angle = random.nextDouble() * 2 *Math.PI;
            double speed = 2.0;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) *speed;

            pendingNeutrons.add(new Neutron(x,y,dx,dy));}
        }
    }

    public void update() {
        // Tutaj logika fizyki, robi Wojtek.
        pendingNeutrons.clear();

        for (Neutron n: neutrons){
            if (n.isOnBoard() == false){
                continue;
            }
            n.move(width,height);
            if (n.isOnBoard() == false){
                continue;
            }
            int index = n.getPixelY() * width + n.getPixelX();
            if (grid[index] == 1)  {                             //trafil w atom uran
                grid[index] = 2;
                n.deactivate();
                spawnNeutrons(n.getPixelX(), n.getPixelY());
            }
        }
        neutrons.addAll(pendingNeutrons);
    }



    public boolean isFinished() {
        for (Neutron n : neutrons) {
            if (n.isOnBoard()) {
                return false;
            }
        }
        return true; // na razie nigdy nie kończy
    }

    public List<Neutron> getNeutrons() {
        return neutrons;
    }
}
