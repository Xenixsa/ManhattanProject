package com.simulationthings;

import com.particles.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class SimulationPanel extends JPanel { // dziedziczymy po JPanel - panel Swinga na którym rysujemy

    private BufferedImage image; // aktualny obrzek z Renderera - null dopuki symulacja nie startuje

    private SimulationEngine engine; // referencja do silnika, aby panel miał bezpośredni wgląd w stan gry

    // Aktualna pozycja myszy we współrzędnych (0-1920, 0-1080).
    // Aktualizowana przez MainMenu co ruch myszy.
    private int hoverX = 0;
    private int hoverY = 0;

    public void setHoverPosition(int X, int Y) {
        hoverX = X;
        hoverY = Y;
    }

    public SimulationPanel(int width, int height) { // konstruktor przyjmujący szerokośc i wysokość panelu

        setPreferredSize(new Dimension(width, height)); // ustawia preferowany rozmiar - Swing użyje go przy pack()
        setBackground(Color.BLACK); // czarne tło - widoczne gdy image jest jescze null
    }

    // Setter pozwalający przypisać obiekt silnika do panelu
    public void setEngine(SimulationEngine engine) {
        this.engine = engine;
    }

    public void setImage(BufferedImage image) { // wywołujemy przez SimulationThread co klatkę z nowym obrazkiem

        this.image = image; // zapisujemy nowy obrazek - przy nastepnym repaint()
    }

    @Override // nadpisujemy metodę paintComponent() z JPanel
    protected void paintComponent(Graphics g) { // Swing wywołuje metodę automatycznie przy każdym repaint()

        super.paintComponent(g); // czyści poprzednią klatkę - bez tego klatki nakładałyby się na siebie

        if (image != null) { // sprawdzamy czy mamy już obrazek - na początku może być null

            g.drawImage(image, 0, 0, getWidth(), getHeight(), null); // rysuje obrazek na całym panelu
        }

        // Rysowanie strzałki
        if (engine != null && engine.isAiming()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // włączenie wygładzenia linii
            g2d.setColor(Color.GREEN); // zielony kolor strzałki
            g2d.setStroke(new BasicStroke(3.0f)); // ustawienie grubości linii na 3 piksele

            // Przeliczanie skali okna względem rozdzielczości obrazu (1920x1080)
            double scaleX = (double) getWidth() / 1920.0;
            double scaleY = (double) getHeight() / 1080.0;

            // Przeliczenie punktów z silnika na piksele na ekranie
            int startScreenX = (int) (engine.getAimStartX() * scaleX);
            int startScreenY = (int) (engine.getAimStartY() * scaleY);
            int currentScreenX = (int) (engine.getAimCurrentX() * scaleX);
            int currentScreenY = (int) (engine.getAimCurrentY() * scaleY);

            // obliczenie dystansu (wektor) przeciągnięcia myszy od punktu startu
            int diffX = currentScreenX - startScreenX;
            int diffY = currentScreenY - startScreenY;

            // rysujemy linię od punktu kliknięcia myszy
            g2d.drawLine(startScreenX, startScreenY, currentScreenX, currentScreenY);
            g2d.fillOval(currentScreenX - 5, currentScreenY - 5, 10, 10); // rysowanie małej kroki na końcu strzałki



        }

        // Rysowanie dymka nad cząstką

        // Dymek pokazujemy, tylko kiedy symulacja jest zapauzowana
        if (engine != null && engine.isPaused()) {
            Particle hovered = engine.getParticleAt(hoverX, hoverY);
            if (hovered != null) {
                drawTooltip((Graphics2D) g, hovered);
            }
        }
    }

    // Rysujemy dymek z informacjami o cząstce w pobliżu kursora myszy.
    private void drawTooltip(Graphics2D g2d, Particle particle) {
        String[] lines = particle.getInfo().split("\n");

        // przeliczamy pozycję myszy z przestrzeni planszy na piksele ekranu
        double scaleX = (double) getWidth() / 1920.0;
        double scaleY = (double) getHeight() / 1080.0;
        int screenX = (int) (hoverX * scaleX);
        int screenY = (int) (hoverY * scaleY);

        int padding = 8; // odstęp tekstu od krawędzi dymka (z każdej strony)
        int lineHeight = 16; // wysokość jednej lini tekstu w pikselach
        int boxWidth = 180; // stała szerokość dymka
        int boxHeight = padding * 2 + lines.length * lineHeight; // wysokość zależy od liczby linii

        // przesuwamy dymek 15px w prawo od kursora, a pionowo centrujemy względem niego
        int tx = screenX + 15;
        int ty = screenY - boxHeight / 2;

        // Klamrowanie - upewniamy się, że dymek nie wyleci poza ekran
        tx = Math.min(tx, getWidth() - boxWidth - 4); // jeśli dymek wychodzi za prawą krawędź, cofamy go tak, żeby jego prawy bok był 4px od krawędzi panelu

        ty = Math.max(ty, 4); // górna krawędz: ty min 4px od góry
        ty = Math.min(ty, getHeight() - boxHeight - 4); // dolna krawędź: dymek nie wychodzi za dół

        // półprzezroczyste czarne tło dymka
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(tx, ty, boxWidth, boxHeight, 8, 8);

        // biały kontur
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.drawRoundRect(tx, ty, boxWidth, boxHeight, 8, 8);

        // tekst linia po lini
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        for (int i = 0; i < lines.length; i++) {
            g2d.drawString(lines[i], tx + padding, ty + padding + (i + 1) * lineHeight);
        }



    }


}
