package com.particles;

import java.awt.*;

public class Atom extends Particle {

    private boolean split;

    public Atom(double x, double y) {
        super(x, y);
    }

    public boolean isSplit() {
        return split;
    }

    public void split() {
        this.split = true;
    }


    @Override
    public void update(int width, int height, double deltaTime) {
        // atomy się nie ruszają - są nieruchomym celem
    }

    @Override
    public Atom copy() {
        Atom copy = new Atom(getX(), getY());
        if (isSplit()) copy.split(); // zachowujemy stan rozszczepienia
        return copy;
    }


    public void drawSelf(Graphics2D g2d){
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)getX() - 4,(int)getY() - 4,8,8);
    }

    @Override
    public String getInfo() {
        return String.format("Atom\nPozycja (%.0f, %.0f)\nStan: %s",
                getX(), getY(),
                isSplit() ? "rozszczepiony" : "nienaruszony");
    }
}
