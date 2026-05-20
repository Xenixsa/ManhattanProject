package com;

import java.awt.*;

public class Atom extends Particle{

    private boolean split;

    public Atom(double x, double y) {
        super(x, y);
    }

    public boolean isSplit() {
        return split;
    }

    public void split() {

    }

    public void drawSelf(Graphics2D g2d){
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)getX() - 4,(int)getY() - 4,8,8);
    }
}
