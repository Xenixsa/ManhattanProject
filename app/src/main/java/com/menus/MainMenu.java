package com.menus;

import com.demopanel.PaintingPanel;

// Importy klas Symulacji

import com.SimulationEngine;
import com.SimulationPanel;
import com.SimulationThread;

import javax.swing.*;
import java.awt.*;

public class MainMenu { // klasa głównego menu aplikacji

    JFrame mainMenuFrame = new JFrame(); // okno głównego menu

    public MainMenu() { // konstruktor - buduje i wyświetla menu


        JPanel menuPanel = new JPanel(); // panel na którym leżą przyciski

        JButton startbutton = new JButton("Start"); // przycisk otwierający panel rysowania
        JButton settingsbutton = new JButton("Settings"); // przycisk ustawień - to zrobienia w przyszłości
        JButton exitbutton = new JButton("Exit"); // przycisk zamykający aplikację

        menuPanel.setBackground(Color.BLACK); // czarne tło panelu
        menuPanel.setLayout(new BoxLayout(menuPanel,BoxLayout.Y_AXIS)); // przyciski ułożone pionowo jeden pod drugim

        startbutton.setAlignmentX(Component.CENTER_ALIGNMENT); // wyśrodkowanie przycisku Start w poziomie
        settingsbutton.setAlignmentX(Component.CENTER_ALIGNMENT); // wyśrodkowanie przycisku Settings w poziomie
        exitbutton.setAlignmentX(Component.CENTER_ALIGNMENT); // wyśrodkowanie przycisku Exit w poziomie

        Dimension spacingBetweenButtons = new Dimension(0,10); // odstęp 10 px między przyciskami

        menuPanel.add(Box.createVerticalGlue()); // elastyczna przestrzeń - pcha przyciski do środka okna
        menuPanel.add(startbutton); // dodaje przycisk Start
        menuPanel.add(Box.createRigidArea(spacingBetweenButtons)); // dodaje odstęp
        menuPanel.add(settingsbutton); // dodaje przycisk Settings
        menuPanel.add(Box.createRigidArea(spacingBetweenButtons)); // dodaje odstęp
        menuPanel.add(exitbutton); // dodaje przycisk Exit
        menuPanel.add(Box.createVerticalGlue()); // elastyczna przestrzeń - pcha przyciski do środka okna

        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zamknięcie okna kończy aplikację
        Dimension mainMenuDimension = new Dimension(800,600); // rozmiar okna menu
        mainMenuFrame.setSize(mainMenuDimension); // ustawia rozmiar okna
        mainMenuFrame.setTitle("Manhattan"); // tytuł okna
        mainMenuFrame.setLocationRelativeTo(null); // wyśrodkowanie okna na ekranie
        mainMenuFrame.setResizable(false); // zablokowanie możliwości zmiany rozmiaru okna

        mainMenuFrame.add(menuPanel); // dodaj panel z przyciskami do okna


        mainMenuFrame.setVisible(true); // pokaż okno

        startbutton.addActionListener(e -> openDrawing()); // kliknięcie Start otwiera panel rysowania
        settingsbutton.addActionListener(e -> System.out.println("Settings - TODO")); // ustawienia do zaimplementowania później
        exitbutton.addActionListener(e -> System.exit(0)); // kliknięcie Exit zamyka aplikację
        }


    private void openDrawing() { // otwiera okno rysowania atomów po kliknięciu Start

        JFrame drawingWindow = new JFrame("Narysuj atomy uranu"); // okno z planszą do rysowania
        PaintingPanel paintingPanel = new PaintingPanel(); // plansza Szymona do rysowania atomów

        JButton launchButton = new JButton("Odpal symulację!"); // przycisk startujący symulację
        launchButton.addActionListener(e -> {

            drawingWindow.dispose(); // zamuka okno rysowania
            mainMenuFrame.dispose(); // zamyka menu główne
            startSimulation(); // odpala symulację
        });

        drawingWindow.setLayout(new BorderLayout()); // layout okna - CENTER i SOUTH
        drawingWindow.add(paintingPanel, BorderLayout.CENTER); // plansza zajmuje środek okna
        drawingWindow.add(launchButton, BorderLayout.SOUTH); // przycisk na dole okna
        drawingWindow.pack(); // zamiast ręcznie ustawiać rozmiar okna, Swing sam oblicza ile miejsca potrzebuje PaintingPanel i dopasowuje okno
        drawingWindow.setLocationRelativeTo(null); // wyśrodkowanie okna na ekranie
        drawingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // zamknięcie tego okna nie kończy całej aplikacji
        drawingWindow.setVisible(true); // pokaż okno rysowania
    }

    private void startSimulation() { // spina razem wszystkie klasy i odpala symulację

        SimulationEngine engine = new SimulationEngine(); // tworzy silnik fizyki
        SimulationPanel simulationPanel = new SimulationPanel(1920,1080); // tworzy panel wyświetlający symulację w Full HD

        SimulationThread simulationThread = new SimulationThread( // tworzy wątek symulacji
                engine, // przekazujemy silnik - wątek będzie go co klatkę wywoływać
                () -> simulationPanel.repaint() // lambda - mówi wątkowi jak odświeżyć ekran
        );

        JFrame simulationWindow = new JFrame("Symulacja"); // okno symulaci
        simulationWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // blokujemy domyślne zamknięcie - sami obsługujemy X
        simulationWindow.addWindowListener(new java.awt.event.WindowAdapter() { // przechwytuje naciśnięcie "X" w oknie i wtedy odpala stopSimulation() i dispose()
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                simulationThread.stopSimulation(); // zatrzymuje symulacje
                simulationWindow.dispose(); // zamyka symulacje
            }
        });

        simulationWindow.add(simulationPanel); // dodaj panel symulacji do okna
        simulationWindow.pack(); // dopasuj rozmiar okna do panelu
        simulationWindow.setLocationRelativeTo(null); // wyśrodkowanie okna na ekranie
        simulationWindow.setVisible(true); // pokaż okno symulacji

        simulationThread.start(); // odpala run() w osobnym wątku - musi być po setVisible() bo panel musi już istnieć na ekranie
    }
}
