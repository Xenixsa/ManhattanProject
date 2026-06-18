package com.simulationthings;

import com.particles.Fragments;
import com.particles.Neutron;
import com.particles.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationEngine {

    private boolean started = false;

    private boolean isAiming = false;
    private int aimStartX, aimStartY;
    private int aimCurrentX, aimCurrentY;

    private int neutronLimit = 10000000;
    private int width;
    private int height;
    private int[] grid;
    private final boolean showFragments;

    public void unpause() { paused = false; }

    private final boolean exitOnNeutrons;

    private int framesSinceLastSave = 0;
    private Random random = new Random();

    private List<Particle> particles = new ArrayList<>();
    private List<Particle> pendingNeutrons = new ArrayList<>();

    private final SimulationHistory history = new SimulationHistory();
    private volatile boolean paused = false;
    private boolean gridShared = false;

    private final SimulationStatsLogger statsLogger;
    private final CollisionLogger collisionLogger;
    private double statsAccumulator = 0.0;
    private double simulationTime = 0.0;

    private volatile double currentFps = 0.0;
    public void setCurrentFps(double fps) { this.currentFps = fps; }

    public SimulationEngine(int width, int height, int[] grid, boolean showFragments, boolean exitOnNeutrons) {
        this(width, height, grid, showFragments, exitOnNeutrons, null, null);
    }

    public SimulationEngine(int width, int height, int[] grid, boolean showFragments, boolean exitOnNeutrons, SimulationStatsLogger statsLogger, CollisionLogger collisionLogger) {
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.particles = new ArrayList<>();
        this.showFragments = showFragments;
        this.exitOnNeutrons = exitOnNeutrons;
        this.statsLogger = statsLogger;
        this.collisionLogger = collisionLogger;
    }

    public void fireNeutron(int startX, int startY, int releaseX, int releaseY) {
        double dx = startX - releaseX;
        double dy = startY - releaseY;
        double speedMultiplier = 3.0;
        dx = dx * speedMultiplier;
        dy = dy * speedMultiplier;
        addNeutron(startX, startY, dx, dy);
    }

    public void addNeutron(double x, double y, double dx, double dy) {
        started = true;
        particles.add(new Neutron(x, y, dx, dy, !exitOnNeutrons));
    }

    private void spawnNeutrons(int x, int y) {
        long neutronCount = particles.stream()
                .filter(p -> p instanceof Neutron)
                .count();

        if (neutronCount < neutronLimit) {
            for (int i = 0; i < 3; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double speed = 120.0;
                pendingNeutrons.add(new Neutron(x, y,
                        Math.cos(angle) * speed, Math.sin(angle) * speed, !exitOnNeutrons));
            }
        }
    }

    public List<Particle> getParticles() {
        synchronized (particles) {
            return new ArrayList<>(particles);
        }
    }

    public Particle getParticleAt(double x, double y) {
        double threshold = 20.0;
        Particle closest = null;
        double closestDist = Double.MAX_VALUE;
        synchronized (particles) {
            for (Particle p : particles) {
                double dist = Math.hypot(p.getX() - x, p.getY() - y);
                if (dist < threshold && dist < closestDist) {
                    closest = p;
                    closestDist = dist;
                }
            }
        }
        return closest;
    }

    public void update(double deltaTime) {

        if (paused) return;

        simulationTime += deltaTime;
        statsAccumulator += deltaTime;

        framesSinceLastSave++;
        if (framesSinceLastSave >= 3) {
            history.save(save());
            framesSinceLastSave = 0;
        }

        pendingNeutrons.clear();

        synchronized (particles) {

            for (Particle p : particles) {
                p.update(width, height, deltaTime);
            }

            ensureGridWritable();   //przeniesione tu żeby zmniejszyć liczbę "kopii"

            for (Particle p : particles) {
                if (p instanceof Neutron n && n.isOnBoard()) {

                    int px = n.getPixelX();
                    int py = n.getPixelY();
                    if (px < 0 || px >= width || py < 0 || py >= height) {
                        n.deactivate();
                        continue;
                    }
                    int index = py * width + px;
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

                        if (collisionLogger != null) {
                            collisionLogger.log(simulationTime, n.getPixelX(), n.getPixelY());
                        }

                        n.deactivate();
                        spawnNeutrons(n.getPixelX(), n.getPixelY());
                        if (showFragments) spawnFragments(n.getPixelX(), n.getPixelY());
                    }
                }
            }

            particles.removeIf(p -> (p instanceof Neutron n && !n.isOnBoard()) || (p instanceof Fragments f && !f.isAlive()));
            particles.addAll(pendingNeutrons);

            while (statsAccumulator >= 0.01) {
                statsAccumulator -= 0.01;
                double logTime = simulationTime - statsAccumulator;
                if (statsLogger != null) {
                    logStats(logTime);
                }
            }
        }
    }

    private void logStats(double seconds) {
        int neutrons = 0;
        int fragments = 0;
        for (Particle p : particles) {
            if (p instanceof Neutron n && n.isOnBoard()) neutrons++;
            if (p instanceof Fragments f && f.isAlive()) fragments++;
        }
        int atoms = 0;
        for (int value : grid) {
            if (value == 1) atoms++;
        }
        statsLogger.log(seconds, neutrons, atoms, fragments, currentFps);
    }

    private void spawnFragments(int x, int y) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double speed = 72.0;

        double dx1 = Math.cos(angle) * speed;
        double dy1 = Math.sin(angle) * speed;
        double dx2 = -dx1;
        double dy2 = -dy1;

        pendingNeutrons.add(new Fragments(x, y, dx1, dy1));
        pendingNeutrons.add(new Fragments(x, y, dx2, dy2));
    }

    public boolean isFinished() {

        if (exitOnNeutrons) {
            if (!started) return false;
            synchronized (particles) {
                return particles.stream().noneMatch(p -> p instanceof Neutron n && n.isOnBoard());
            }
        }
        for (int i = 0; i < grid.length; i++) {
            if (grid[i] == 1) return false;
        }
        return true;
    }

    public void setAimState(boolean isAiming, int startX, int startY, int currentX, int currentY) {
        this.isAiming = isAiming;
        this.aimStartX = startX;
        this.aimStartY = startY;
        this.aimCurrentX = currentX;
        this.aimCurrentY = currentY;
    }

    public boolean isAiming() { return isAiming; }
    public int getAimStartX() { return aimStartX; }
    public int getAimStartY() { return aimStartY; }
    public int getAimCurrentX() { return aimCurrentX; }
    public int getAimCurrentY() { return aimCurrentY; }

    public SimulationMemento save() {
        gridShared = true;
        return new SimulationMemento(grid, particles, started);
    }

    public void restore(SimulationMemento memento) {
        synchronized (particles) {
            grid = memento.getGrid();
            gridShared = true;
            particles.clear();
            particles.addAll(memento.getParticles());
        }
        started = memento.isStarted();
    }

    private void ensureGridWritable() {
        if (gridShared) {
            grid = grid.clone();
            gridShared = false;
        }
    }

    public int[] getGrid() { return grid; }

    public void togglePause() { paused = !paused; }
    public boolean isPaused() { return paused; }

    public void rewind() {
        SimulationMemento previous = history.rewind();
        if (previous != null) restore(previous);
    }

    public void forward() {
        SimulationMemento next = history.forward();
        if (next != null) restore(next);
    }
}