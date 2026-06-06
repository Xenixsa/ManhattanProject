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
        //Dimension mainMenuDimension = new Dimension(800, 600);
        //mainMenuFrame.setSize(mainMenuDimension);
        mainMenuFrame.setTitle("Manhattan");
        mainMenuFrame.setResizable(true); // pozwala na zmianę rozmiaru, aby system mógł poprawnie zmaksymalizować okno
        //mainMenuFrame.setLocationRelativeTo(null); // wyśrodkowanie na ekranie - tylko raz przy starcie
        //mainMenuFrame.setResizable(false);
        mainMenuFrame.add(container); // do okna trafia kontener, nie bezpośrednio panel
        mainMenuFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        //mainMenuFrame.pack(); // rozszerza okno do rozmiaru PaintingPanel
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

<<<<<<< Updated upstream
        SimulationEngine engine          = new SimulationEngine(1920, 1080, grid); // silnik fizyki
=======
        // Read settings
        boolean showFragments = true;
        boolean wallCollisions = true;
        String resolution = "1920x1080";
        if (settingsManagerRef != null) {
            showFragments = settingsManagerRef.getStringSetting("fragments", "true").equals("true");
            wallCollisions = settingsManagerRef.getStringSetting("wallCollisions", "true").equals("true");
            resolution = settingsManagerRef.getStringSetting("resolution", "1920x1080");
        }

        // apply window size from resolution setting (does not change engine internal resolution)
        try {
            String[] parts = resolution.split("x");
            int winW = Integer.parseInt(parts[0]);
            int winH = Integer.parseInt(parts[1]);
            mainMenuFrame.setExtendedState(JFrame.NORMAL);
            mainMenuFrame.setSize(winW, winH);
            mainMenuFrame.setLocationRelativeTo(null);
        } catch (Exception ignored) {
        }

        SimulationEngine engine          = new SimulationEngine(1920, 1080, grid, showFragments, wallCollisions); // silnik fizyki
>>>>>>> Stashed changes
        SimulationPanel  simulationPanel  = new SimulationPanel(1920, 1080);        // ekran symulacji
        simulationPanel.setEngine(engine); // przekazanie silnika do panelu przez setter
        Renderer         renderer         = new Renderer(1920, 1080);               // zamienia grid[] na obrazek

<<<<<<< Updated upstream
=======

        JLabel fpsLabel = new JLabel("FPS: --");
        fpsLabel.setForeground(Color.WHITE);


        // tablice jednoelementowe, bo lambda może modyfikować tylko zmienne "efektywnie final"
        // (zwykłego int-a wewnątrz lambdy nie można zmieniać - kompilator krzyczy
        long[] lastTime = { System.nanoTime() };
        int[] frameCount = { 0 };
        final JPanel[] overlayPanel = new JPanel[1];


>>>>>>> Stashed changes
        // Lambda wywoływana co klatkę przez SimulationThread:
        // renderuje stan silnika -> przekazuje obrazek do panelu -> odświeża ekran
        SimulationThread[] simulationThreadRef = new SimulationThread[1];
        simulationThreadRef[0] = new SimulationThread(
                engine,
                () -> {
<<<<<<< Updated upstream
                    BufferedImage frame = renderer.render(grid, engine.getParticles()); // renderuje nową klatkę
                    simulationPanel.setImage(frame); // przekazuje obrazek do panelu
                    simulationPanel.repaint(); // mówi Swingowi, żeby odświeżył ekran
                }
        );

        container.add(simulationPanel, "SIMULATION"); // rejestrujemy ekran symulacji jako kartę
        cardLayout.show(container, "SIMULATION");     // przełączamy na ekran symulacji
=======
                    BufferedImage frame = renderer.render(engine.getGrid(), engine.getParticles()); // renderuje nową klatkę
                    SwingUtilities.invokeLater(() -> {
                        simulationPanel.setImage(frame); // przekazuje obrazek do panelu
                        simulationPanel.repaint(); // mówi Swingowi, żeby odświeżył ekran
                        overlayPanel[0].setVisible(engine.isFinished());
                    });

                    // aktualizujemy licznik FPS co 30 klatek, żeby nie migotał
                    frameCount[0]++;
                    if (frameCount[0] >= 30) {
                        long now = System.nanoTime();
                        // 30 klatek * 1_000_000_000 ns/s podzielone przez czas jaki minął
                        double fps = 30_000_000_000.0 / (now - lastTime[0]);
                        fpsLabel.setText(String.format("FPS: %.0f", fps));
                        lastTime[0] = now;
                        frameCount[0] = 0;
                    }
                }
        );

        // Przyciski sterowania
        JButton pauseButton = new JButton("Pauza [Spacja]");
        JButton rewindButton = new JButton("Cofnij [<-]");
        rewindButton.setEnabled(false); // aktywny dopiero po wstrzymaniu symulacji

        pauseButton.addActionListener(e -> {
            engine.togglePause();
            pauseButton.setText(engine.isPaused() ? "Wznów [Spacja]" : "Pauza [Spacja]");
            rewindButton.setEnabled(engine.isPaused());
        });

        rewindButton.addActionListener(e -> {
            if (engine.isPaused()) engine.rewind();
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        controlPanel.setBackground(Color.DARK_GRAY);
        controlPanel.add(pauseButton);
        controlPanel.add(rewindButton);
        controlPanel.add(fpsLabel);

        overlayPanel[0] = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (engine.isFinished()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(new Color(0, 0, 0, 150));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            }
        };
        overlayPanel[0].setOpaque(false);
        overlayPanel[0].setLayout(new GridBagLayout());
        overlayPanel[0].setVisible(false);
        overlayPanel[0].setAlignmentX(0.5f);
        overlayPanel[0].setAlignmentY(0.5f);
        overlayPanel[0].setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        overlayPanel[0].setMinimumSize(new Dimension(0, 0));

        JButton backToMenuButton = new JButton("Powrót do menu");
        backToMenuButton.setFont(backToMenuButton.getFont().deriveFont(Font.BOLD, 16f));
        backToMenuButton.setPreferredSize(new Dimension(220, 50));
        backToMenuButton.addActionListener(e -> {
            if (simulationThreadRef[0] != null) simulationThreadRef[0].stopSimulation();
            engine.resetHistory(); // resetujemy historię po zakończeniu symulacji
            cardLayout.show(container, "MENU");
            mainMenuFrame.setTitle("Manhattan");
        });

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setOpaque(false);
        buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.Y_AXIS));
        JLabel finishedLabel = new JLabel("Symulacja zakończona");
        finishedLabel.setForeground(Color.WHITE);
        finishedLabel.setFont(finishedLabel.getFont().deriveFont(Font.BOLD, 24f));
        finishedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonWrapper.add(finishedLabel);
        buttonWrapper.add(Box.createRigidArea(new Dimension(0, 16)));
        buttonWrapper.add(backToMenuButton);

        overlayPanel[0].add(buttonWrapper);

        JPanel simulationContainer = new JPanel();
        simulationContainer.setLayout(new OverlayLayout(simulationContainer));
        simulationPanel.setAlignmentX(0.5f);
        simulationPanel.setAlignmentY(0.5f);
        simulationContainer.add(overlayPanel[0]);
        simulationContainer.add(simulationPanel);

        JPanel simulationWrapper = new JPanel(new BorderLayout());
        simulationWrapper.add(simulationContainer, BorderLayout.CENTER);
        simulationWrapper.add(controlPanel, BorderLayout.SOUTH);

        container.add(simulationWrapper, "SIMULATION");
        cardLayout.show(container, "SIMULATION");

        // Key bindings - spacja i strzałka w lewo wołają dokładnie te same akcje co przyciski.
        // doClick() symuluje kliknięcie, więc logika jest tylko w jednym miejscu (addActionListener wyżej).
        InputMap inputMap = simulationPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = simulationPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "togglePause");
        actionMap.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseButton.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "rewind");
        actionMap.put("rewind", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.isPaused()) rewindButton.doClick();
            }
        });





>>>>>>> Stashed changes
        mainMenuFrame.setTitle("Symulacja");           // aktualizujemy tytuł okna
        mainMenuFrame.setResizable(true);              // pozwalamy zmieniać rozmiar podczas symulacji

        // Przechwytujemy kliknięcie X - najpierw zatrzymujemy wątek,
        // dopiero potem zamykamy okno (kolejność ma znaczenie)
        mainMenuFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainMenuFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (simulationThreadRef[0] != null) simulationThreadRef[0].stopSimulation(); // zatrzymujemy wątek przed zamknięciem
                engine.resetHistory(); // resetujemy historię przed zamknięciem
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

                // włączamy celowanie w silniku i ustawiamy początek strzałki (na pozycji kliknięcia)
                int logX = pressCords[0];
                int logY = pressCords[1];
                engine.setAimState(true, logX, logY, logX, logY);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // włączamy rysowanie celownika w silniku, bo gracz puścił myszkę
                engine.setAimState(false,0, 0, 0, 0);
                // przeliczamy gdzie puściliśmy myszkę
                int releaseX = (int)(e.getX() * 1920.0 / simulationPanel.getWidth());
                int releaseY = (int)(e.getY() * 1080.0 / simulationPanel.getHeight());

                // strzelamy neutronem - kierunek odwrotny do przeciągnięcia jak w Angry Birds
                engine.fireNeutron(pressCords[0], pressCords[1], releaseX, releaseY);
            }
        });

        simulationPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) { // reagowanie na ruch myszy z wciśniętym przyciskiem (przeciąganie celownika)
                // aktualizujemy pozycję, tylko jeśli w silniku trwa celowanie
                if (engine.isAiming()) {
                    int logCurrentX = (int)(e.getX() * 1920.0 / simulationPanel.getWidth());
                    int logCurrentY = (int)(e.getY() * 1080.0 / simulationPanel.getHeight());

                    // wysyłamy nowe współrzędne przez setter w silniku i odświeżamy ekran
                    engine.setAimState(true, engine.getAimStartX(), engine.getAimStartY(), logCurrentX, logCurrentY);
                    simulationPanel.repaint();
                }
            }
        });

        simulationThreadRef[0].start(); // startujemy wątek - musi być po show() żeby panel był już widoczny
    }
}