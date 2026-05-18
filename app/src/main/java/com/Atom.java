package com;

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
}
