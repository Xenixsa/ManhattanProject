package com;

public class Neutron extends Particle {

    private double dx;
    private double dy;
    private boolean onBoard;

    public Neutron(double x, double y, double dx, double dy){
        this.x = x;
        this.y = y;
        this.dy = dy;
        this.dx = dx;
        this.onBoard = true;
    }
    public void move(int width, int height){
        if (x<0 || x>=width || y<0 || y>=height){
            onBoard = false;
        }

        x += dx;
        y += dy;
    }
    public int getPixelX(){
        return (int)x;
    }
    public int getPixelY(){
        return (int)y;
    }
    public boolean isOnBoard(){
        return onBoard;
    }
    public void deactivate(){
        onBoard = false;
    }

}
