package com.simulationthings;

import com.particles.Particle;

import java.util.ArrayList;
import java.util.List;

// Zdjęcie całego stanu symulacji w jednej chwili - rola Memento we wzorcu Pamiątka.
// Po utworzeniu jest niemodyfiowalne (wszystkie pola final, brak setterów).
public class SimulationMemento {

    private final int[] grid; // referencja do planszy (współdzielona przez copy-on-write)
    private final List<Particle> particles; // głęboka kopia wszystkich cząstek z tej klatki
    private final boolean started;

    public SimulationMemento(int[] grid, List<Particle> particles, boolean started) {
        this.grid = grid; // referencja do planszy (współdzielona przez copy-on-write)
        this.particles = deepCopy(particles); // głęboka kopia wszystkich cząstek z tej klatki
        this.started = started;
    }

    // Sedno całego rozwiązania: zero instanceof. Każda cząstka sama wie, jak się skopiować.
    private List<Particle> deepCopy(List<Particle> source) {
        List<Particle> result = new ArrayList<>();
        for (Particle p : source) {
            result.add(p.copy());
        }
        return result;
    }

    public int[] getGrid() { return grid; }

    // Oddajemy świeże kopie, a nie oryginalne obiekty z pamiątki - inaczej żywa symulacja
    // zaczęłaby nimi ruszać i zniszczyłaby nasze zdjęcie.
    public List<Particle> getParticles() {
        return deepCopy(particles);
    }

    public boolean isStarted() { return started; }
}