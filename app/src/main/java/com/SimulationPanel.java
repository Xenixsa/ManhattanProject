package com;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class SimulationPanel extends JPanel { // dziedziczymy po JPanel - panel Swinga na którym rysujemy

    private BufferedImage image; // aktualny obrzek z Renderera - null dopuki symulacja nie startuje

    private SimulationEngine engine; // referencja do silnika, aby panel miał bezpośredni wgląd w stan gry

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
    }


}
