package com.simulationthings;

import java.util.ArrayList;
import java.util.List;

// Caretaker we wzorcu Pamiątka. Snapshoty trzyma w buforze cyklicznym -
// stałej tablicy z wędrującym wskaźnikiem, więc zapis jest o(1) i nie przesuwa danych
// Nie zagląda do środka Momento, tylko je przechowuje i wydaje
public class SimulationHistory {


    private static final int CAPACITY = 300; // okno historii (przy 60 krokach/s ~ 5s)

    private final SimulationMemento[] buffer = new SimulationMemento[CAPACITY];
    private int oldestIndex = 0; // wskaźnik na najstarszy snapshot w tablicy
    private int size = 0; // liczba zapisanych snapshotów (0...CAPACITY)
    private int cursor = -1; // który snapshot oglądamy (-1 = bufor pusty)

    // Zapisuje nowy snapsho jako teraźniejszość. Jeśli wcześniej cofaliśmy,
    // odcina nieaktualną przyszłość. Gdy bufor pełny, nadpisuje najstarszy wpis.
    public void save(SimulationMemento memento) {

        size = cursor + 1; // odcięcie klatek, które były "do przodu" od kursora

        int writeIndex = (oldestIndex + size) % CAPACITY;
        buffer[writeIndex] = memento;

        if (size < CAPACITY) {
            size++;
        } else {
            oldestIndex = (oldestIndex + 1) % CAPACITY; // bufor pełny - najstarszy wypada
        }
        cursor = size - 1; // kursor wraca na teraźniejszość
    }

    // Cofa o jeden snaphot - nie usuwa nic, tylko przesuwa kursor.
    // Zwraca snapshot do przywrócenia albo null, gdy jesteśmy już na najstarszym.
    public SimulationMemento rewind() {
        if (cursor <= 0) return null;
        cursor--;
        return frameAt(cursor);
    }


    // Przewija o jeden snapshot do przodu, w stronę teraźniejszości.
    // Zwraca snapshot albo null, gdy jesteśmy już na najnowszym
    public SimulationMemento forward() {
        if (cursor >= size - 1) return null;
        cursor++;
        return frameAt(cursor);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Zmienia numer snapshotu (0 = najstarszy) na fizyczny indeks w tablicy.
    private SimulationMemento frameAt(int logicalIndex) {
        return buffer[(oldestIndex + logicalIndex) % CAPACITY];
    }
}
