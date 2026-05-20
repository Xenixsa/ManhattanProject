package com;

import java.awt.*;

public class Fragments extends Particle {

    private double dx;
    private double dy;
    private final Color color;
    public static final int R = 4; // dwukrotnie większy od neutronu (r=2)

    private int ticksAlive = 0;
    private static final int LIFETIME_TICKS = 5 * 60;

    private static final Color[] COLORS = {
            new Color(255, 80, 80),   // czerwony
            new Color(80, 180, 255),  // niebieski
    };

    private static int colorIndex = 0;

    public Fragments(double x, double y, double dx, double dy) {
        super(x, y);
        this.dx = dx;
        this.dy = dy;
        this.color = COLORS[colorIndex % COLORS.length];
        colorIndex++;
    }

    public void move(int width, int height) {
        ticksAlive++;
        setX(getX() + dx);
        setY(getY() + dy);

        if (getX() < R) { setX(R); dx = -dx; }
        else if (getX() >= width - R) { setX(width - R - 1); dx = -dx; }

        if (getY() < R) { setY(R); dy = -dy; }
        else if (getY() >= height - R) { setY(height - R - 1); dy = -dy; }
    }

    public boolean isAlive(){
        return ticksAlive < LIFETIME_TICKS;
    }

    @Override
    public void drawSelf(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int)getX() - R, (int)getY() - R, R * 2, R * 2);
    }
}
