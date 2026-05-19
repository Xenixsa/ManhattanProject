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
    private List<Neutron> neutrons;
    private Random random = new Random();

    private List<Atom> atoms;
    private List<Neutron> pendingNeutrons = new ArrayList<>();  //lista pomocnicza, żeby nie psuć głownej pętli

    public SimulationEngine(int width, int height, int[] grid){
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.neutrons = new ArrayList<>();
    }

    public void fireNeutron(int startX, int startY, int releaseX, int releaseY){
        double dx = startX - releaseX; //obliczamy wektory kierunku naciagniecia
        double dy = startY - releaseY;
        double speedMultiplier = 0.05;
        dx =dx * speedMultiplier;
        dy =dy * speedMultiplier;
        addNeutron(startX, startY, dx, dy);
    }

    public void addNeutron(double x, double y,double dx, double dy){
        started = true; // zaznaczamy, że symulacja została zaczęta
        neutrons.add(new Neutron(x,y,dx,dy));
    }

    private void spawnNeutrons(int x,int y){
        int amount = 3;
        if (neutrons.size() + pendingNeutrons.size() < neutronLimit){ //limit neutronow
        for (int i=0; i<amount; i++){
            double angle = random.nextDouble() * 2 *Math.PI;
            double speed = 2.0;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) *speed;

            pendingNeutrons.add(new Neutron(x ,y,dx,dy));}
        }
    }

    private void checkCollisions(){

    }



    public void update() {
        // Tutaj logika fizyki, robi Wojtek.
        pendingNeutrons.clear();

        for (Neutron n: neutrons){
            if (n.isOnBoard() == false){
                continue;
            }
            n.move(width,height);
            if (n.isOnBoard() == false){
                continue;
            }
            int index = n.getPixelY() * width + n.getPixelX();
            if (grid[index] == 1)  {//trafil w atom uran
                //metoda, dzieki ktorej nie osiagamy limitu neutronow za szybko
                int blastRaduis = 3;
                for (int by = -blastRaduis; by <= blastRaduis; by++){
                    for (int bx = -blastRaduis; bx<=blastRaduis; bx++){
                        int clearX = n.getPixelX() + bx;
                        int clearY = n.getPixelY() +by;

                        //brak mozliwosci wyjscia poza tablice przy krawedziach
                        if (clearX >= 0 && clearX < width && clearY >= 0 && clearY < height){
                            int clearIndex = clearY * width + clearX;
                            if (grid[clearIndex]==1){
                                grid[clearIndex]=2;
                            }
                        }
                    }
                }
                n.deactivate(); //stary neutron znika i pojawiaja sie nowe
                spawnNeutrons(n.getPixelX(), n.getPixelY());
            }
        }
        neutrons.removeIf(n -> !n.isOnBoard());  //usuwamy martwe neutrony z pamieci, zeby nie blokowac limitu
        neutrons.addAll(pendingNeutrons);
    }



    public boolean isFinished() {
        if (!started) return false; // symulacja nie zakończona, jeśli jeszcze nie zaczęta
        for (Neutron n : neutrons) {
            if (n.isOnBoard()) {
                return false;
            }
        }
        return true; // na razie nigdy nie kończy
    }

    public List<Neutron> getNeutrons() {
        return neutrons;
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
}
