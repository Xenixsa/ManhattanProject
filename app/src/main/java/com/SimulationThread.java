package com;

public class SimulationThread extends Thread {

    private final SimulationEngine engine; // silnik symulacji - liczy fizykę co klatkę
    private final Runnable onRepaint; // pole do odświeżania ekranu - przekazywany  zewnątrz
    private volatile boolean running = true; // flaga kontrolująca pętlę. Słowo volatile gwarantuje widoczność zmian zmiennej pomiędzy wątkami.

    public SimulationThread(SimulationEngine engine, Runnable onRepaint) { // konstruktor przyjmuje silnik i funkcję oświeżającą

        this.engine = engine; // zapisujemy silnik
        this.onRepaint = onRepaint; // zapisujemy funkcję odświeżającą
    }

    @Override // nadpisujemy metodę run() z klasy Thread
    public void run() { // run() odpala się w osobnym wątku po wywołaniu start()

        while (running && !engine.isFinished()) { // dopóki nie zatrzymano i symulacja się nie kończy

            engine.update(); // przesuwa symulację o jeden krok - przesuwa neutrony, sprawdza kolizje
            onRepaint.run(); // wywołuje odświeżenie ekranu - pokazuje nową klatkę użytkownikowi

            try {

                Thread.sleep(16); // czeka 16ms co daje 60 FPS

            } catch (InterruptedException e) { // jeżeli ktoś przerwał wątek z zewnątrz

                Thread.currentThread().interrupt(); // przywraca flagę przerwania
                break; // wychodzi z pętli
            }
        }
    }

    public void stopSimulation() { // metoda która zatrzymuje symulacje z zewnątrz np. po kliknięciu "X"

        running = false; // running = false powoduje wyjście z pętli przy następnym sprawdzeniu warunku
    }

}
