package com;

import java.awt.*;

public abstract class Particle {

    private double x;
    private double y;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void drawSelf(Graphics2D g2d);

    // Każda cząstka musi umieć stworzyć swoją niezależną kopię.
    // Dzięki temu Pamiątka kopiuje cząstki polimorficznie - woła p.copy()
    // i nie musi wiedzieć, czy ma do czynienia z neutronem, czy fragmentem
    public abstract Particle copy();

    // Każda cząstka zwraca opis siebie - wyświetlany w dymku po zatrzymaniu symulacji.
    // Abstrakcyjna, bo każdy typ cząstki ma inne dane do pokazania
    public abstract String getInfo();


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
}
