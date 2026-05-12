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

    public SimulationEngine(int width, int height, int[] grid){
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.neutrons = new ArrayList<>();
    }
    public void addNeutron(double x, double y,double dx, double dy){
        neutrons.add(new Neutron(x,y,dx,dy));
    }

    private void spawnNeutrons(int x,int y){
        //... do dokonczenia
    }

    public void update() {
        // Tutaj logika fizyki, robi Wojtek.
        for (Neutron n: neutrons){
            if (n.isOnBoard() == false){
                continue;
            }
            n.move(width,height);
            if (n.isOnBoard() == false){
                continue;
            }
            int index = n.getPixelY() * width * n.getPixelX();
            if (grid[index] == 1)  {                             //trafil w atom uran
                grid[index] = 2;
                spawnNeutrons(n.getPixelX(), n.getPixelY());
            }
        }
    }



    public boolean isFinished() {
        for (Neutron n : neutrons) {
            if (n.isOnBoard()) {
                return false;
            }
        }
        return true; // na razie nigdy nie kończy
    }
}
