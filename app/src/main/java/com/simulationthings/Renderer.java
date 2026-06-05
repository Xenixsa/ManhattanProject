package com.simulationthings;

import com.particles.Particle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt; // wewnętrzny bufor obrazka jako int[]
import java.util.List;


public class Renderer {

    private final int width; // szerokość planszy w pikselach
    private final int height; // wysokość planszy w pikselach
    private final BufferedImage image; // obrazek, który wypełniamy kolorami co klatkę
    private final int[] pixels; // bezpośrednia tablica pikseli obrazka, int = jeden piksel


    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // tworzymy obrazek raz i będziemy go nadpisywać, zamiast tworzyć nowy co klatkę
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        // getRuster() pobiera obiekt opisujący jakie dane są ułożone w pamięci
        // getDataBuffer() z tego rustera wyciąga surowy bufor danych, ale zwraca go jako ogólny typ DataBuffer
        // .getData() dostajemy zwykłą tablicę int[]
    }

    public BufferedImage render(int[] grid, List<Particle> particles) {

        for (int i = 0; i < grid.length; i++) { // przechodzimy przez każdy piksel planszy

            if (grid[i] == 1) {
                pixels[i] = 0xFFFF00; // żółty - atom uranu
            } else if (grid[i] == 2) {
                pixels[i] = 0x404040; // ciemnoszary - rozszczepiony atom
            } else {
                pixels[i] = 0x000000; // czarny - puste miejsce
            }
        }

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // wygładzone krawędzie kulki
        g2d.setColor(Color.WHITE);

        for (Particle p: particles){
            p.drawSelf(g2d);
        }
        g2d.dispose(); // zwalniamy zasoby graficzne po skończeniu rysowania

        return image;
   }
}
