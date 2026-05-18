package com;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class SimulationPanel extends JPanel { // dziedziczymy po JPanel - panel Swinga na którym rysujemy

    private BufferedImage image; // aktualny obrzek z Renderera - null dopuki symulacja nie startuje

    public SimulationPanel(int width, int height) { // konstruktor przyjmujący szerokośc i wysokość panelu

        setPreferredSize(new Dimension(width, height)); // ustawia preferowany rozmiar - Swing użyje go przy pack()
        setBackground(Color.BLACK); // czarne tło - widoczne gdy image jest jescze null
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
    }


}
