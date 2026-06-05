package com.simulationthings;

import com.particles.Fragments;
import com.particles.Neutron;
import com.particles.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationEngine {

    private boolean started = false; // czy użytkownik już strzelił

    private boolean isAiming = false; // flaga informująca czy gracz aktualnie naciąga celownik
    private int aimStartX, aimStartY; // współrzędne punktu kliknięcia myszy
    private int aimCurrentX, aimCurrentY; // współrzędne aktualnej pozycji myszy podczas przeciągania

    private int neutronLimit = 10000;
    private int width;
    private int height;
    private int[] grid;
    private final boolean showFragments;

    private int framesSinceLastSave = 0; // licznik klatek od ostatniego zapisu historii
    private Random random = new Random();

    private List<Particle> particles = new ArrayList<>();
    private List<Particle> pendingNeutrons = new ArrayList<>();

    private final SimulationHistory history = new SimulationHistory(); // Caretaker - przechowuje zdjęcia
    private volatile boolean paused = false; // volatile - czyta wątek symulacji, ustawia wątek UI
    private boolean gridShared = false; // true = aktualny grid trzyma już jakąś pamiątkę

    public SimulationEngine(int width, int height, int[] grid, boolean showFragments){
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.particles = new ArrayList<>();
        this.showFragments = showFragments;
    }

    public void fireNeutron(int startX, int startY, int releaseX, int releaseY){
        double dx = startX - releaseX; //obliczamy wektory kierunku naciagniecia
        double dy = startY - releaseY;
        double speedMultiplier = 0.05;
        dx =dx * speedMultiplier;
        dy =dy * speedMultiplier;
        addNeutron(startX, startY, dx, dy);
    }

    public void addNeutron(double x, double y, double dx, double dy){
        started = true;
        particles.add(new Neutron(x, y, dx, dy));
    }

    private void spawnNeutrons(int x, int y) {
        // liczymy tylko neutrony, bo fragmenty nie napędzają reakcji i nie powinny blokować limitu
        long neutronCount = particles.stream()
                .filter(p -> p instanceof Neutron)
                .count();

        if (neutronCount < neutronLimit) {
            for (int i = 0; i < 3; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double speed = 2.0;
                pendingNeutrons.add(new Neutron(x, y,
                        Math.cos(angle) * speed, Math.sin(angle) * speed));
            }
        }
    }

    public List<Particle> getParticles() {
        // Zwracamy kopię pod lockiem - inaczej wątek renderujący i wątek UI
        // mogą jednocześnie czytać i modyfikować tę samą listę
        synchronized (particles) {
            return new ArrayList<>(particles);
        }
    }

    // Szuka cząstki w okolicy podanych współrzędnych (w przestrzeni 1920x1080).
    // Zwraca null, jeśli żadna cząstka nie jest wystarczająco blisko.
    public Particle getParticleAt(double x, double y) {
        double threshold = 20.0; // promień wykrywania w pikselach
        Particle closest = null;
        double closestDist =  Double.MAX_VALUE;
        synchronized (particles) { // synchronized, bo lista jest współdzielona między wątkami
            for (Particle p : particles) {
                double dist = Math.hypot(p.getX() - x, p.getY() - y); // hypot to pierwiastek z (a^2 + b^2) - odległość między dwoma punktami
                if (dist < threshold && dist < closestDist) {
                    closest = p;
                    closestDist = dist;
                }
            }
        }
        return closest;
    }


    public void update() {

        if (paused) return; // przy pauzie nie liczymy fizyki i nie zapisujemy klatek

        framesSinceLastSave++;
        if (framesSinceLastSave >= 3) {
            history.save(save());
            framesSinceLastSave = 0;
        }
        //history.save(save()); // zapamiętujemy stan SPRZED tego kroku, żeby móc się cofnąć

        pendingNeutrons.clear();
        synchronized (particles){
        for (Particle p : particles) {
            if (p instanceof Neutron n) {
                if (!n.isOnBoard()) continue;
                n.move(width, height);
                if (!n.isOnBoard()) continue;

                int index = n.getPixelY() * width + n.getPixelX();
                if (grid[index] == 1) {
                    ensureGridWritable(); // klonuj grid przed zmianą, żeby nie zepsuć zapisanych
                    int blastRadius = 3;
                    for (int by = -blastRadius; by <= blastRadius; by++) {
                        for (int bx = -blastRadius; bx <= blastRadius; bx++) {
                            int cx = n.getPixelX() + bx;
                            int cy = n.getPixelY() + by;
                            if (cx >= 0 && cx < width && cy >= 0 && cy < height) {
                                int ci = cy * width + cx;
                                if (grid[ci] == 1) grid[ci] = 2;
                            }
                        }
                    }
                    n.deactivate();
                    spawnNeutrons(n.getPixelX(), n.getPixelY());
                    if (showFragments) spawnFragments(n.getPixelX(), n.getPixelY());
                }

            } else if (p instanceof Fragments f) {
                f.update(width, height); // odbija się od ścian, brak innych interakcji
            }
        }

        particles.removeIf(p -> (p instanceof Neutron n && !n.isOnBoard()) || (p instanceof Fragments f && !f.isAlive()));
        particles.addAll(pendingNeutrons);
    }}

    private void spawnFragments(int x, int y) {
        // dwa fragmenty w przeciwnych kierunkach
        double angle = random.nextDouble() * 2 * Math.PI;
        double speed = 1.2;

        double dx1 = Math.cos(angle) * speed;
        double dy1 = Math.sin(angle) * speed;
        double dx2 = -dx1; // dokładnie przeciwny kierunek
        double dy2 = -dy1;

        pendingNeutrons.add(new Fragments(x, y, dx1, dy1));
        pendingNeutrons.add(new Fragments(x, y, dx2, dy2));
    }

    public boolean isFinished() {
        for (int i = 0; i < grid.length; i++) {
            if (grid[i] == 1) return false; // zostały jeszcze nierozszczepione atomy
        }
        return true; // wszystkie żółte zniszczone
    }


    // Metoda pozwalająca z zewnątrz zaktualizować cały stan celowania w silniku za jednym razem
    public void setAimState(boolean isAiming, int startX, int startY, int currentX, int currentY) {
        this.isAiming = isAiming;
        this.aimStartX = startX;
        this.aimStartY = startY;
        this.aimCurrentX = currentX;
        this.aimCurrentY = currentY;
    }

    // Gettery, dzięki którym SimulationPanel będzie mógł pobrać dane do narysowania strzałki
    public boolean isAiming() { return isAiming; }
    public int getAimStartX() { return aimStartX; }
    public int getAimStartY() { return aimStartY; }
    public int getAimCurrentX() { return aimCurrentX; }
    public int getAimCurrentY() { return aimCurrentY; }

    // Tworzy zdjęcie aktualnego stanu (rola Originatora). Od tej chwili grid jest tylko-do-odczytu,
    // bo właśnie oddaliśmy jego referencję pamiątce.
    public SimulationMemento save() {
        gridShared = true;
        return new SimulationMemento(grid, particles, started);
    }

    // Przywraca stan ze zdjęcia. Bierzemy referencję planszy z pamiątki
    // i znów oznaczamy ją jako współdzieloną.
    public void restore(SimulationMemento memento) {
        // Lock na particles - ten sam co w getParticles(),
        // więc restore i render nigdy nie wykonają się równocześnie
        synchronized (particles) { // to oznacza "tylko jeden wątek na raz może wejść do bloku trzymającego lock na tym obiekcie"
            grid = memento.getGrid();
            gridShared = true;
            particles.clear();
            particles.addAll(memento.getParticles()); // getParticles() zwraca świeże kopie
        }
        started = memento.isStarted();
    }

    // Jeśli aktualny grid jest współdzielony z pamiątką, klonujemy go PRZED modyfikacją.
    // Bez tego nadpisalibyśmy dane w zapisanych zdjęciach.
    private void ensureGridWritable() {
        if (gridShared) {
            grid = grid.clone();
            gridShared = false;
        }
    }

    // Getter potrzebny dlatego, że silnik może podmienić referencję grid pod spodem (copy-on-write).
    // Renderer musi zawsze pobierać aktualną tablicę przez silnik, nie trzymać starej referencji.
    public int[] getGrid() { return grid; }

    public void togglePause() { paused = !paused; }
    public boolean isPaused() { return paused; }

    // Cofa symulację o jedną zapisaną klatkę. Wywołuj tylko przy pauzie -
    // wtedy wątek symulacji nic nie robi i nie ma wyścigu o wspólne dane.
    public void rewind() {
        SimulationMemento previous = history.undo();
        if (previous != null) restore(previous);
    }
}
