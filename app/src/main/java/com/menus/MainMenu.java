package com.menus;

import com.Renderer;
import com.demopanel.PaintingPanel;
import com.SimulationEngine;
import com.SimulationPanel;
import com.SimulationThread;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainMenu { // klasa głównego menu aplikacji

    //Jedno główne okno aplikacji - nie tworzymy nowych okien
    // tylko podmieniamy zawartość za pomocą CardLayout
    JFrame mainMenuFrame = new JFrame();
    CardLayout cardLayout = new CardLayout(); // przełącza widoczny panel
    JPanel container = new JPanel(cardLayout); // kontener trzymający wszystkie karty (MENU, DRAWING, SIMULATION)

    public MainMenu() { // konstruktor - buduje i wyświetla menu

        JPanel menuPanel = new JPanel(); // panel ekranu startowego z przyciskami
        JButton startbutton = new JButton("Start"); // otwiera panel rysowania atomów
        JButton settingsbutton = new JButton("Settings"); // TODO: ustawienia symulacji
        JButton exitbutton = new JButton("Exit"); // zamyka aplikację

        menuPanel.setBackground(Color.BLACK);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS)); // przyciski jeden pod drugim

        // CENTER_ALIGNMENT wyśrodkowuje każdy przycisk poziomo w BoxLayout
        startbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitbutton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension spacingBetweenButtons = new Dimension(0, 10); // odstęp 10 px między przyciskami

        // VerticalGlue pcha przyciski do środka okna z góry i z dołu
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(startbutton);
        menuPanel.add(Box.createRigidArea(spacingBetweenButtons));
        menuPanel.add(settingsbutton);
        menuPanel.add(Box.createRigidArea(spacingBetweenButtons));
        menuPanel.add(exitbutton);
        menuPanel.add(Box.createVerticalGlue());

        container.add(menuPanel, "MENU"); // rejestrujemy panel menu jako pierwszą kartę

        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension mainMenuDimension = new Dimension(800, 600);
        mainMenuFrame.setSize(mainMenuDimension);
        mainMenuFrame.setTitle("Manhattan");
        mainMenuFrame.setLocationRelativeTo(null); // wyśrodkowanie na ekranie - tylko raz przy starcie
        mainMenuFrame.setResizable(false);
        mainMenuFrame.add(container); // do okna trafia kontener, nie bezpośrednio panel
        mainMenuFrame.setVisible(true);

        startbutton.addActionListener(e -> openDrawing());
        settingsbutton.addActionListener(e -> System.out.println("Settings - TODO"));
        exitbutton.addActionListener(e -> System.exit(0));
    }

    private void openDrawing() {

        // Tworzymy panel rysowania i dodajemy go jako kartę DRAWING -
        // okno zostaje w tym samym miejscu na ekranie, zmienia się tylko zawartość
        PaintingPanel paintingPanel = new PaintingPanel(); // plansza Szymona do rysowania atomów uranu

        JButton launchButton = new JButton("Odpal symulację!");
        launchButton.addActionListener(e -> startSimulation(paintingPanel)); // przekazujemy planszę dalej

        JPanel drawingPanel = new JPanel(new BorderLayout());
        drawingPanel.add(paintingPanel, BorderLayout.CENTER); // plansza wypełnia środek
        drawingPanel.add(launchButton, BorderLayout.SOUTH);   // przycisk na dole

        container.add(drawingPanel, "DRAWING"); // rejestrujemy jako kartę
        cardLayout.show(container, "DRAWING"); // przełączamy na ekran rysowania
        mainMenuFrame.pack(); // rozszerza okno do rozmiaru PaintingPanel
        mainMenuFrame.setTitle("Narysuj atomy uranu"); // aktualizujemy tytuł okna
    }

    private void startSimulation(PaintingPanel paintingPanel) { // spina razem wszystkie klasy i odpala symulację

        // Budujemy tablicę grid[] na podstawie tego, co użytkownik narysował.
        // Każdy blok 40x40px zaznaczony w PaintingPanel staje się atomem uranu (wartość 1) w grid[]
        int[] grid = new int[1920 * 1080];

        // przepisujemy narysowane atomy z PaintingPanel do grid[]
        for (int row = 0; row < paintingPanel.maxRow; row++) {
            for (int col = 0; col < paintingPanel.maxCol; col++) {
                if (paintingPanel.blocks[col][row].isPainted) {
                    // każdy blok to 40x40 pikseli - wypełniamy wszystkie piksele bloku
                    for (int py = 0; py < paintingPanel.nodeSize; py++) {
                        for (int px = 0; px < paintingPanel.nodeSize; px++) {
                            int x = col * paintingPanel.nodeSize + px;
                            int y = row * paintingPanel.nodeSize + py;
                            grid[y * 1920 + x] = 1; // 1 = atom uranu
                        }
                    }
                }
            }
        }

        SimulationEngine engine          = new SimulationEngine(1920, 1080, grid); // silnik fizyki
        SimulationPanel  simulationPanel  = new SimulationPanel(1920, 1080);        // ekran symulacji
        Renderer         renderer         = new Renderer(1920, 1080);               // zamienia grid[] na obrazek

        // Lambda wywoływana co klatkę przez SimulationThread:
        // renderuje stan silnika -> przekazuje obrazek do panelu -> odświeża ekran
        SimulationThread simulationThread = new SimulationThread(
                engine,
                () -> {
                    BufferedImage frame = renderer.render(grid, engine.getNeutrons()); // renderuje nową klatkę
                    simulationPanel.setImage(frame); // przekazuje obrazek do panelu
                    simulationPanel.repaint(); // mówi Swingowi, żeby odświeżył ekran
                }
        );

        container.add(simulationPanel, "SIMULATION"); // rejestrujemy ekran symulacji jako kartę
        cardLayout.show(container, "SIMULATION");     // przełączamy na ekran symulacji
        mainMenuFrame.setTitle("Symulacja");           // aktualizujemy tytuł okna
        mainMenuFrame.setResizable(true);              // pozwalamy zmieniać rozmiar podczas symulacji

        // Przechwytujemy kliknięcie X - najpierw zatrzymujemy wątek,
        // dopiero potem zamykamy okno (kolejność ma znaczenie)
        mainMenuFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainMenuFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                simulationThread.stopSimulation(); // zatrzymujemy wątek przed zamknięciem
                mainMenuFrame.dispose();
            }
        });

        // zapis współrzędnych kliknięcia
        int[] pressCords = new int[2];

        simulationPanel.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // przeliczamy współrzędne z rozmiaru panelu na rozmiar obrazka (1920x1080)
                pressCords[0] = (int)(e.getX() * 1920.0 / simulationPanel.getWidth());
                pressCords[1] = (int)(e.getY()  * 1080.0 / simulationPanel.getHeight());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // przeliczamy gdzie puściliśmy myszkę
                int releaseX = (int)(e.getX() * 1920.0 / simulationPanel.getWidth());
                int releaseY = (int)(e.getY() * 1080.0 / simulationPanel.getHeight());

                // strzelamy neutronem - kierunek odwrotny do przeciągnięcia jak w Angry Birds
                engine.fireNeutron(pressCords[0], pressCords[1], releaseX, releaseY);
            }
        });

        simulationThread.start(); // startujemy wątek - musi być po show() żeby panel był już widoczny
    }
}