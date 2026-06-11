package com.menus;

import com.settings.SettingsManager;
import com.settings.SettingsPanel;
import com.simulationthings.*;
import com.demopanel.PaintingPanel;
import com.simulationthings.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainMenu { // klasa głównego menu aplikacji

    // Jedno główne okno aplikacji - nie tworzymy nowych okien
    // tylko podmieniamy zawartość za pomocą CardLayout
    JFrame mainMenuFrame = new JFrame();
    CardLayout cardLayout = new CardLayout(); // przełącza widoczny panel
    JPanel container = new JPanel(cardLayout); // kontener trzymający wszystkie karty (MENU, DRAWING, SIMULATION)
    JPanel menuPanel = new JPanel(); // panel ekranu startowego z przyciskami

    private SettingsPanel settingsPanelRef = null;
    private SettingsManager settingsManagerRef = null;
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private Rectangle normalBounds = new Rectangle(100, 100, DEFAULT_WIDTH, DEFAULT_HEIGHT);



    public MainMenu() { // konstruktor - buduje i wyświetla menu

        // load settings manager early so resolution/fragments are available
        settingsManagerRef = new SettingsManager();

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
        mainMenuFrame.setTitle("Manhattan");
        mainMenuFrame.setResizable(true); // pozwala na zmianę rozmiaru, aby system mógł poprawnie zmaksymalizować okno
        mainMenuFrame.add(container); // do okna trafia kontener, nie bezpośrednio panel
        restoreWindowState(); // przywracamy pozycję i rozmiar z poprzedniej sesji
        mainMenuFrame.setVisible(true);



        // aktualizujemy normalBounds przy każdym ruchu lub zmianie rozmiaru okna,
        // żeby mieć zawsze aktualne wymiary niezależnie od stanu przy zamknięciu
        mainMenuFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                normalBounds = mainMenuFrame.getBounds();
            }
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                normalBounds = mainMenuFrame.getBounds();
            }
        });


        // zapis pozycji okna tuż przed zakończeniem programu - niezależnie od sposobu wyjścia
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveWindowState));


        startbutton.addActionListener(e -> openDrawing());
        settingsbutton.addActionListener(e -> openSettingsPanel());
        exitbutton.addActionListener(e -> System.exit(0));
    }

    // Zapisuje pozycję, rozmiar i stan ona do ustawień.
    // Przy zmaksymalizowanym oknie pobieramy wymiary przed maksymalizacją (normalBounds),
    // żeby przy przywróceniu nie zapisać rozmiaru pełnego ekranu.
    private void saveWindowState() {
        settingsManagerRef.set("windowX", String.valueOf(normalBounds.x));
        settingsManagerRef.set("windowY", String.valueOf(normalBounds.y));
        settingsManagerRef.set("windowWidth", String.valueOf(normalBounds.width));
        settingsManagerRef.set("windowHeight", String.valueOf(normalBounds.height));
        settingsManagerRef.save();
    }


    // Przywraca pozycję i rozmiar okna z poprzedniej sesji.
    // Przy pierwszym uruchomieniu (brak zapisanych wartości) maksymalizuje okno.
    private void restoreWindowState() {
        int x = settingsManagerRef.getIntSetting("windowX", Integer.MAX_VALUE);
        int y = settingsManagerRef.getIntSetting("windowY", Integer.MAX_VALUE);
        int width = settingsManagerRef.getIntSetting("windowWidth", -1);
        int height = settingsManagerRef.getIntSetting("windowHeight", -1);

        if (width <= 0 || height <= 0) {
            // pierwsze uruchomienie - okno na cały główny ekran
            Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
            normalBounds = screen;
        } else  {
            normalBounds = new Rectangle(x, y, width, height);
        }
        mainMenuFrame.setBounds(normalBounds);
    }



    private Path getScriptBaseDir() {
        try {
            URI location = MainMenu.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path jarPath = Paths.get(location);
            if (Files.isRegularFile(jarPath)) {
                Path parent = jarPath;
                for (int i = 0; i < 4 && parent != null; i++) {
                    parent = parent.getParent();
                }
                if (parent != null) {
                    return parent.toAbsolutePath();
                }
            }
        } catch (Exception ignored) {
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    private void startPythonPlotProcess(Path baseDir, String scriptName) throws IOException {
        Path scriptPath = baseDir.resolve(scriptName);
        String python = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";
        ProcessBuilder pb = new ProcessBuilder(python, scriptPath.toString());
        pb.directory(baseDir.toFile());
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        System.out.println("Launching Python script: " + scriptPath);
        pb.start();
    }

    private void openSettingsPanel(){
        if (settingsManagerRef == null) settingsManagerRef = new SettingsManager();
        if (settingsPanelRef == null) {
            settingsPanelRef = new SettingsPanel(mainMenuFrame, container, cardLayout, settingsManagerRef);
            container.add(settingsPanelRef, "SETTINGS");
        }

        cardLayout.show(container, "SETTINGS");
        settingsPanelRef.revalidate();
        settingsPanelRef.repaint();
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

        // Read settings
        boolean showFragments = settingsManagerRef.getStringSetting("fragments", "true").equals("true");
        boolean exitOnNeutrons = (settingsPanelRef != null) && settingsPanelRef.isExitOnNeutrons();
        System.out.println("exitOnNeutrons = " + exitOnNeutrons);
        System.out.println("settingsPanelRef = " + settingsPanelRef);


        final SimulationStatsLogger[] statsLoggerRef = new SimulationStatsLogger[1];
        final CollisionLogger[] collisionLoggerRef = new CollisionLogger[1];
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            statsLoggerRef[0] = new SimulationStatsLogger("app/simulation_stats/simulation_stats_" + timestamp + ".csv");
            collisionLoggerRef[0] = new CollisionLogger("app/simulation_stats/collision_positions_" + timestamp + ".csv");
            System.out.println("Logging stats to: " + statsLoggerRef[0].getFilePath());
            System.out.println("Logging collisions to: " + collisionLoggerRef[0].getFilePath());
        } catch (Exception e) {
            System.err.println("Nie udało się otworzyć plików statystyk: " + e.getMessage());
        }

        SimulationEngine engine          = new SimulationEngine(1920, 1080, grid, showFragments, exitOnNeutrons, statsLoggerRef[0], collisionLoggerRef[0]); // silnik fizyki
        SimulationMemento initialState = engine.save(); // snapshot stanu przed jakimkolwiek neutronem
        SimulationPanel  simulationPanel  = new SimulationPanel(1920, 1080);        // ekran symulacji
        simulationPanel.setEngine(engine); // przekazanie silnika do panelu przez setter
        Renderer         renderer         = new Renderer(1920, 1080);               // zamienia grid[] na obrazek


        JLabel fpsLabel = new JLabel("FPS: --");
        fpsLabel.setForeground(Color.WHITE);


        // tablice jednoelementowe, bo lambda może modyfikować tylko zmienne "efektywnie final"
        // (zwykłego int-a wewnątrz lambdy nie można zmieniać - kompilator krzyczy
        long[] lastTime = { System.nanoTime() };
        int[] frameCount = { 0 };

        // Lambda wywoływana co klatkę przez SimulationThread:
        // renderuje stan silnika -> przekazuje obrazek do panelu -> odświeża ekran
        Runnable onFinish = () -> {
            new Thread(() -> {
                try {
                    Path baseDir = getScriptBaseDir();
                    System.out.println("Python scripts base dir: " + baseDir);
                    startPythonPlotProcess(baseDir, "plot_simulation_stats.py");
                    startPythonPlotProcess(baseDir, "plot_collision_heatmap_2d.py");
                    startPythonPlotProcess(baseDir, "plot_collision_density_time.py");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("onFinish plotting failed: " + e.getMessage());
                }
            }).start();
        };

        SimulationThread simulationThread = new SimulationThread(
                engine,
                () -> {
                    BufferedImage frame = renderer.render(engine.getGrid(), engine.getParticles()); // renderuje nową klatkę
                    simulationPanel.setImage(frame); // przekazuje obrazek do panelu
                    simulationPanel.repaint(); // mówi Swingowi, żeby odświeżyć ekran

                    // aktualizujemy licznik FPS co 30 klatek, żeby nie migotał
                    frameCount[0]++;
                    if (frameCount[0] >= 30) {
                        long now = System.nanoTime();
                        double fps = 30_000_000_000.0 / (now - lastTime[0]);
                        fpsLabel.setText(String.format("FPS: %.0f", fps));
                        lastTime[0] = now;
                        frameCount[0] = 0;
                        engine.setCurrentFps(fps);
                    }
                },
                onFinish
        );

        // Przyciski w panelu symulacji
        JButton pauseButton = new JButton("Pauza [Spacja]");

        JButton rewindButton = new JButton("Cofnij [<-]");
        rewindButton.setEnabled(false); // aktywny dopiero po wstrzymaniu symulacji

        JButton forwardButton = new JButton("Do przodu [->]");
        forwardButton.setEnabled(false); // aktywny dopiero po wstrzymaniu symulacji

        JButton menuButton = new JButton("Menu [M]");

        JButton resetButton = new JButton("Resetuj [R]");


        pauseButton.addActionListener(e -> {
            engine.togglePause();
            pauseButton.setText(engine.isPaused() ? "Wznów [Spacja]" : "Pauza [Spacja]");
            rewindButton.setEnabled(engine.isPaused());
            forwardButton.setEnabled(engine.isPaused());
            simulationPanel.requestFocusInWindow();
        });

        rewindButton.addActionListener(e -> {
            if (engine.isPaused()) engine.rewind();
            simulationPanel.requestFocusInWindow();
        });

        forwardButton.addActionListener(e -> {
            if (engine.isPaused()) engine.forward();
            simulationPanel.requestFocusInWindow();
        });

        menuButton.addActionListener(e -> {
            simulationThread.stopSimulation(); // zatrzymujemy wątek symulacji
            if (statsLoggerRef[0] != null) statsLoggerRef[0].close();
            if (collisionLoggerRef[0] != null) collisionLoggerRef[0].close();
            mainMenuFrame.setTitle("Manhattan");
            cardLayout.show(container, "MENU"); // przełączamy widok na menu
        });

        resetButton.addActionListener(e -> {
            engine.restore(initialState); // przywracamy stan sprzed pierwszego neutronu
            engine.unpause(); // upewniamy się, że symulacja nie stoi w pauzie
            pauseButton.setText("Pauza [Spacja]");
            rewindButton.setEnabled(false);
            forwardButton.setEnabled(false);
            simulationPanel.requestFocusInWindow();
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        controlPanel.setBackground(Color.DARK_GRAY);
        controlPanel.add(menuButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);
        controlPanel.add(rewindButton);
        controlPanel.add(forwardButton);
        controlPanel.add(fpsLabel);

        JPanel simulationWrapper = new JPanel(new BorderLayout());
        simulationWrapper.add(simulationPanel, BorderLayout.CENTER);
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


        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "forward");
        actionMap.put("forward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.isPaused()) forwardButton.doClick();
            }
        });


        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        actionMap.put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButton.doClick();
            }
        });


        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "menu");
        actionMap.put("menu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuButton.doClick();
            }
        });





        mainMenuFrame.setTitle("Symulacja");           // aktualizujemy tytuł okna
        mainMenuFrame.setResizable(true);              // pozwalamy zmieniać rozmiar podczas symulacji

        // Przechwytujemy kliknięcie X - najpierw zatrzymujemy wątek,
        // dopiero potem zamykamy okno (kolejność ma znaczenie)
        mainMenuFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainMenuFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                simulationThread.stopSimulation(); // zatrzymujemy wątek przed zamknięciem
                if (statsLoggerRef[0] != null) statsLoggerRef[0].close();
                if (collisionLoggerRef[0] != null) collisionLoggerRef[0].close();
                System.exit(0);
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


            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                // przeliczamy pozycję kursora na współrzędne planszy i przekazujemy do panelu
                int X = (int) (e.getX() * 1920.0 / simulationPanel.getWidth());
                int Y = (int) (e.getY() * 1080.0 / simulationPanel.getHeight());
                simulationPanel.setHoverPosition(X, Y);

                // odświeżamy ekran tylko przy pauzie - poza pauzą robi to wątek symulacji
                if (engine.isPaused()) simulationPanel.repaint();
            }
        });

        simulationThread.start(); // startujemy wątek - musi być po show() żeby panel był już widoczny
    }
}