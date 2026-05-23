package com.simulationthings;

import java.util.ArrayList;
import java.util.List;

// Caretaker we wzorcu Pamiątka - przechwuje listę snapshotów symulacji
// Nie zagląda do środka Momento, tylko je przechowuje i wydaje
public class SimulationHistory {

    private final List<SimulationMemento> history = new ArrayList<>();
    private static final int MAX_HISTORY = 300; // 5 sekund przy 60 FPS

    // Dodaje nowy snapshot - jeśli historia za długa, usuwa najstarszy wpis
    public void save(SimulationMemento memento) {

        history.add(memento);
        if  (history.size() > MAX_HISTORY) {
            history.remove(0);
        }
    }

    // Zwraca i usuwa ostatni snapshot - cofa o jeden zapisany krok
    // Zwraca null, jeśli historia jest pusta
    public SimulationMemento undo() {
        if (history.isEmpty()) return null;
        return history.remove(history.size() - 1);
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
