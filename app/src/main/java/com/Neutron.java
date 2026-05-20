package com;

import java.awt.*;

public class Neutron extends Particle {

    private double dx;
    private double dy;
    private boolean onBoard;

    public int r = 2; // promień kulki w pikselach

    public Neutron(double x, double y, double dx, double dy){
        super(x, y);
        this.dy = dy;
        this.dx = dx;
        this.onBoard = true;
    }
    public void move(int width, int height){
        setX(getX() + dx);
        setY(getY() + dy);

        int r = 2;
        // odbicie od lewej i prawej ściany
        if (getX() < r) { // jeśli wyleciał za ścianę
            setX(r); // cofa do krawędzi
            dx = -dx; // odwraca kierunek poziomy
        } else if (getX() >= width - r) { // wyleciał za prawą ścianę
            setX(width - r - 1); // cofnij go do krawędzi
            dx = -dx; // odwróć kierunek poziomy
        }

        // odbicie od górnej i dolnej ściany
        if (getY() < r) {
            setY(r);
            dy = -dy;
        } else if (getY() >= height - r) {
            setY(height - r - 1);
            dy = -dy;
        }

    }

    @Override
    public void drawSelf(Graphics2D g2d){
        g2d.fillOval(this.getPixelX() - r, this.getPixelY() - r, r * 2, r * 2); // rysuje kulkę
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

    //

}
