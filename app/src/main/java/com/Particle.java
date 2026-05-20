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
