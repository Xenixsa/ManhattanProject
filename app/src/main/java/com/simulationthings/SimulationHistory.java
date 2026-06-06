package com.simulationthings;

// Caretaker we wzorcu Pamiątka - przechwuje cykliczny bufor snapshotów symulacji
// Nie zagląda do środka Momento, tylko je przechowuje i wydaje
// Używa CycleBuffer zamiast ArrayList dla O(1) wydajności przy dodawaniu
public class SimulationHistory {

    private final CycleBuffer<SimulationMemento> history;
    private static final int MAX_HISTORY = 300; // 5 sekund przy 60 FPS

    public SimulationHistory() {
        this.history = new CycleBuffer<>(MAX_HISTORY);
    }

    // Dodaje nowy snapshot - cycle buffer automatycznie nadpisuje najstarszy wpis gdy jest pełny
    // O(1) operacja - bez drażliwych przesunięć tablicy
    public void save(SimulationMemento memento) {
        history.add(memento);
    }

    // Zwraca i usuwa ostatni snapshot - cofa o jeden zapisany krok
    // Zwraca null, jeśli historia jest pusta
    public SimulationMemento undo() {
        return history.removeLast();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    // Resetuje historię - usuwa wszystkie snapshoty
    public void clear() {
        history.clear();
    }

    // Getter do monitorowania rozmiaru historii
    public int size() {
        return history.size();
    }
}
