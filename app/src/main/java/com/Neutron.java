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
        if (getX()<0 || getX()>=width || getY()<0 || getY()>=height){
            onBoard = false;
        }

        setX(getX() + dx);
        setY(getY() + dy);
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
