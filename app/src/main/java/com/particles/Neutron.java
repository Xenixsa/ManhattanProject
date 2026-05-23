package com.particles;

import java.awt.*;

public class Neutron extends Particle {

    private double dx;
    private double dy;
    private boolean onBoard;

    public int r = 2; // promień kulki w pikselach

    public double getDx() { return dx; }
    public double getDy() { return dy; }


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
        g2d.setColor(Color.WHITE); // neutron zawsze biały, niezależnie od tego, co rysowało przed nim
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

    @Override
    public Neutron copy() {
        Neutron copy = new Neutron(getX(), getY(), dx, dy); // ta sama pozycja i prędkość
        if (!onBoard) copy.deactivate(); // zachowujemy stan aktywności
        return copy;
    }

    @Override
    public String getInfo() {
        // String.format("%.2f") zaokrągla do 2 miejsc po przecinku - ładniejszy wynik niż surowy double
        return String.format("Neutron\nPrędkość: (%.2f, %.2f)\nStan: %s",
                dx, dy, onBoard ? "aktywny" : "niekatywny");
    }

}
