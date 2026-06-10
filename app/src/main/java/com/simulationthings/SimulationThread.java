package com.simulationthings;

public class SimulationThread extends Thread {

    private final SimulationEngine engine; // silnik symulacji - liczy fizykę co klatkę
    private final Runnable onRepaint; // pole do odświeżania ekranu - przekazywany  zewnątrz
    private volatile boolean running = true; // flaga kontrolująca pętlę. Słowo volatile gwarantuje widoczność zmian zmiennej pomiędzy wątkami.

    // Stała długość kroku fizyki (1/60 s), niezależna od liczby klatek
    private static final double FIXED_TIME_STEP = 1.0 / 60.;


    public SimulationThread(SimulationEngine engine, Runnable onRepaint) { // konstruktor przyjmuje silnik i funkcję oświeżającą

        this.engine = engine; // zapisujemy silnik
        this.onRepaint = onRepaint; // zapisujemy funkcję odświeżającą
    }

    @Override // nadpisujemy metodę run() z klasy Thread
    public void run() { // run() odpala się w osobnym wątku po wywołaniu start()


        long previousTime = System.nanoTime(); // czas z poprzedniego powrotu pętli
        double accumulator = 0.0; // nadwyżka czasu czekająca na zasymulowanie


        // dopóki nie zamknięto okna - nie kończymy jej po zakończeniu symulacji,
        // żeby dało się jeszcze przewijać i oglądać klatki
        while (running) {


            long currentTime = System.nanoTime();

            double deltaTime = (currentTime - previousTime) / 1_000_000_000.0; // w sekundach
            previousTime = currentTime;


            if (deltaTime > 0.25) deltaTime = 0.25; // ograniczenie skoku po dłuższej przerwie (np. breakpoint)

            if (engine.isFinished()) {
                accumulator = 0.0; // fizyka stoi - nie kumulujemy czasu (inaczej skok po wznowieniu)
            } else {
                accumulator += deltaTime;

                while (accumulator >= FIXED_TIME_STEP) {

                    engine.update(FIXED_TIME_STEP);
                    accumulator -= FIXED_TIME_STEP;
                }
            }

            onRepaint.run(); // wywołuje odświeżenie ekranu - pokazuje nową klatkę użytkownikowi

            try {
            Thread.sleep(1); // czeka 1ms co daje 60 FPS

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
