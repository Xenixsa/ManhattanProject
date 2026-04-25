package com.menus;

import com.demopanel.PaintingPanel;

import javax.swing.*;
import java.awt.*;

public class MainMenu {

    JFrame mainMenuFrame = new JFrame();

    public MainMenu(){
        long createdtime = System.nanoTime();
        JPanel menuPanel = new JPanel();

        JButton startbutton = new JButton();
        JButton settingsbutton = new JButton();
        JButton exitbutton = new JButton();

        menuPanel.setBackground(Color.BLACK);
        menuPanel.setLayout(new BoxLayout(menuPanel,BoxLayout.Y_AXIS));

        startbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitbutton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension spacingBetweenButtons = new Dimension(0,10);

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(startbutton);
        menuPanel.add(Box.createRigidArea(spacingBetweenButtons));
        menuPanel.add(settingsbutton);
        menuPanel.add(Box.createRigidArea(spacingBetweenButtons));
        menuPanel.add(exitbutton);
        menuPanel.add(Box.createVerticalGlue());

        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension mainMenuDimension = new Dimension(800,600);
        mainMenuFrame.setSize(mainMenuDimension);
        mainMenuFrame.setTitle("Manhattan");
        mainMenuFrame.setLocationRelativeTo(null);
        mainMenuFrame.setResizable(false);

        mainMenuFrame.add(menuPanel);


        mainMenuFrame.setVisible(true);

        JFrame demowindow = new JFrame();
        demowindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        demowindow.setResizable(false);
        demowindow.add(new PaintingPanel());

        demowindow.pack();
        demowindow.setLocationRelativeTo(null);

        System.out.print("\nNow start button is aviable.");
        long estimatedTime = System.nanoTime() - createdtime;
        System.out.print("It took: "+estimatedTime/100000000+" seconds to get started.");
        startbutton.addActionListener(actionEvent -> {
            System.out.println("Start button pressed.");
            mainMenuFrame.dispose();
            demowindow.setVisible(true);
        });

        settingsbutton.addActionListener(actionEvent -> {
            System.out.println("Settings button pressed.");
        });

        exitbutton.addActionListener(actionEvent -> {
            System.out.print("Exit button pressed.");
            System.exit(0);
        });
    }
}
