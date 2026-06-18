package com.particles;

import java.awt.*;

public class Fragments extends Particle {

    private double dx;
    private double dy;
    private final Color color;
    public static final int R = 4; // dwukrotnie większy od neutronu (r=2)

    private double ageSeconds = 0.0;
    private static final double LIFETIME_SECONDS = 5.0;

    private static final Color[] COLORS = {
            new Color(255, 80, 80),   // czerwony
            new Color(80, 180, 255),  // niebieski
    };

    private final String name; // nazwa izotopu zależna od koloru
    private static int colorIndex = 0;

    public Fragments(double x, double y, double dx, double dy) {
        super(x, y);
        this.dx = dx;
        this.dy = dy;
        int index = colorIndex % COLORS.length;
        this.color = COLORS[index];
        this.name = index == 0 ? "Bar-144" : "Krypton-89"; // czerwony = Bar, niebieski = Krypton
        colorIndex++;
    }

    public void update(int width, int height, double deltaTime) {
        ageSeconds += deltaTime; // starszejemy się o realny czas, nie o "jedną klatkę"
        setX(getX() + dx * deltaTime);
        setY(getY() + dy * deltaTime);

        if (getX() < R) { setX(R); dx = -dx; }
        else if (getX() >= width - R) { setX(width - R - 1); dx = -dx; }

        if (getY() < R) { setY(R); dy = -dy; }
        else if (getY() >= height - R) { setY(height - R - 1); dy = -dy; }
    }

    public boolean isAlive() {
        return ageSeconds < LIFETIME_SECONDS;
    }

    // Prywatny konstruktor kopiujący - używamy wyłącznie przez copy().
    // NIE rusza statycznego colorIndex, więc kopia dostaje dokładnie ten sam kolor.
    private Fragments(Fragments original) {
        super(original.getX(), original.getY());
        this.dx = original.dx;
        this.dy = original.dy;
        this.color = original.color;
        this.name = original.name;
        this.ageSeconds = original.ageSeconds; // inaczej kopia żyłaby od nowa 5 sekund
    }

    @Override
    public Fragments copy() {
        return new Fragments(this);
    }

    @Override
    public void drawSelf(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int)getX() - R, (int)getY() - R, R * 2, R * 2);
    }

    @Override
    public String getInfo() {

        double speed = Math.hypot(dx, dy); // długość wektora prędkości, jak szybko leci cząstka
        double secondsLeft = LIFETIME_SECONDS - ageSeconds;
        return String.format("%s\nPrędkość: %.2f px/s\nPozostało: %.1f s",
        name, speed, secondsLeft);
    }
}
