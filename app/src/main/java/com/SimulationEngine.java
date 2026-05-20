package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationEngine {

    private boolean started = false; // czy użytkownik już strzelił

    private boolean isAiming = false; // flaga informująca czy gracz aktualnie naciąga celownik
    private int aimStartX, aimStartY; // współrzędne punktu kliknięcia myszy
    private int aimCurrentX, aimCurrentY; // współrzędne aktualnej pozycji myszy podczas przeciągania

    private int neutronLimit = 10000;
    private double neutronCollisionRadius = 2.0;
    private int width;
    private int height;
    private int[] grid;
//    private List<Neutron> neutrons;
    private Random random = new Random();

    private List<Particle> particles = new ArrayList<>();
    private List<Particle> pendingNeutrons = new ArrayList<>();

    public SimulationEngine(int width, int height, int[] grid){
        this.width = width;
        this.height = height;
        this.grid = grid;
//        this.neutrons = new ArrayList<>();
        this.particles = new ArrayList<>();
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
        particles.add(new Neutron(x, y, dx, dy)); // ← particles zamiast neutrons
    }

    private void spawnNeutrons(int x, int y) {
        if (particles.size() + pendingNeutrons.size() < neutronLimit) {
            for (int i = 0; i < 3; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double speed = 2.0;
                pendingNeutrons.add(new Neutron(x, y,
                        Math.cos(angle) * speed, Math.sin(angle) * speed));
            }
        }
    }

    private void checkCollisions(){

    }

    public List<Particle> getParticles(){return particles;}


    public void update() {
        pendingNeutrons.clear();

        for (Particle p : particles) {
            if (p instanceof Neutron n) {
                if (!n.isOnBoard()) continue;
                n.move(width, height);
                if (!n.isOnBoard()) continue;

                int index = n.getPixelY() * width + n.getPixelX();
                if (grid[index] == 1) {
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
                    spawnFragments(n.getPixelX(), n.getPixelY());
                }

            } else if (p instanceof Fragments f) {
                f.move(width, height); // odbija się od ścian, brak innych interakcji
            }
        }

        particles.removeIf(p -> (p instanceof Neutron n && !n.isOnBoard()) || (p instanceof Fragments f && !f.isAlive()));
        particles.addAll(pendingNeutrons);
    }

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
//    public boolean isFinished() {
//        if (!started){
//            return false;
//        }
//        for (Particle p: particles){
//            if (p instanceof Neutron n && n.isOnBoard()){
//                return false;
//            }
//        }
//        return true;
////        if (!started) return false; // symulacja nie zakończona, jeśli jeszcze nie zaczęta
////        for (Neutron n : neutrons) {
////            if (n.isOnBoard()) {
////                return false;
////            }
////        }
////        return true; // na razie nigdy nie kończy
//    }

//    public List<Neutron> getNeutrons() {
//        return neutrons;
//    }

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
}
