package com;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;


public class Renderer {

    private final int width; // szerokość planszy w pikselach
    private final int height; // wysokość planszy w pikselach
    private final BufferedImage image; // obrazek, który wypełniamy kolorami co klatkę

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // tworzymy obrazek raz i będziemy go nadpisywać zamiast tworzyć nowy co klatkę
    }



    public BufferedImage render(int[] grid, List<Neutron> neutrons) {

        for (int i = 0; i < grid.length; i++) { // przechodzimy przez każdy piksel planszy

            int color;

            if (grid[i] == 1) {
                color = 0xFFFF00; // żółty - atom uranu
            } else if (grid[i] == 2) {
                color = 0xFF6600; // pomarańczowy - rozszczepiony atom
            } else {
                color = 0x000000; // czarny - puste miejsce
            }

            image.setRGB(i % width, i / width, color);
        }

        for (Neutron n : neutrons)
            if (n.isOnBoard()) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dx = -2; dx <= 2; dx++) {
                        int nx = n.getPixelX() + dx;
                        int ny = n.getPixelY() + dy;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            image.setRGB(nx, ny, 0xFFFFFF); // biały - neutron

                        }
                    }
                }

            }

        return image;
    }
}
