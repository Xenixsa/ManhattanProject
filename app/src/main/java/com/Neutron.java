package com;

public class Neutron extends Particle {

    private double dx;
    private double dy;
    private boolean onBoard;

    public Neutron(double x, double y, double dx, double dy){
        super(x, y);
        this.dy = dy;
        this.dx = dx;
        this.onBoard = true;
    }
    public void move(int width, int height){
        setX(getX() + dx);
        setY(getY() + dy);

        // odbicie od lewej i prawej ściany
        if (getX() < 0) {
            setX(0);
            dx = -dx;
        } else if (getX() >= width) {
            setX(width - 1);
            dx = -dx;
        }

        // odbicie od górnej i dolnej ściany
        if (getY() < 0) {
            setY(0);
            dy = -dy;
        } else if (getY() >= height) {
            setY(height - 1);
            dy = -dy;
        }

    }

    public int getPixelX(){
        return (int)getX();
    }
    public int getPixelY(){
        return (int)getY();
    }
    public boolean isOnBoard(){
        return onBoard;
    }
    public void deactivate(){
        onBoard = false;
    }

}
