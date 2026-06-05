package com.settings;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JButton backButton = new JButton("Back");
    private JComboBox<String> resolutionCombo;
    private JCheckBox fragmentsCheckBox;

    private final JPanel mainContainer;
    private final CardLayout cardLayout;

    public SettingsPanel(JFrame jFrame, JPanel mainContainer, CardLayout cardLayout, SettingsManager settingsManager) {
        this.mainContainer = mainContainer;
        this.cardLayout = cardLayout;
        setBackground(Color.BLACK);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        boolean fragmentsDefault = settingsManager.getStringSetting("fragments", "true").equals("true");
        fragmentsCheckBox = new JCheckBox("Pokaż fragmenty", fragmentsDefault);
        fragmentsCheckBox.setBackground(Color.BLACK);
        fragmentsCheckBox.setForeground(Color.WHITE);
        fragmentsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        fragmentsCheckBox.addActionListener(e ->
                settingsManager.set("fragments", String.valueOf(fragmentsCheckBox.isSelected()))
        );

        String[] resolutions = {"1920x1080", "1280x720", "960x540", "1080x1080","720x720","540x540"};
        resolutionCombo = new JComboBox<>(resolutions);
        resolutionCombo.setSelectedItem(settingsManager.getStringSetting("resolution", "1920x1080"));
        resolutionCombo.setMaximumSize(new Dimension(200, 30));
        resolutionCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        resolutionCombo.addActionListener(e ->
                settingsManager.set("resolution", (String) resolutionCombo.getSelectedItem())
        );

        backButton.addActionListener(e -> {
            settingsManager.save();
            // apply resolution immediately after saving
            try {
                String resolution = settingsManager.getStringSetting("resolution", "1920x1080");
                String[] parts = resolution.split("x");
                int winW = Integer.parseInt(parts[0]);
                int winH = Integer.parseInt(parts[1]);
                jFrame.setSize(winW, winH);
                jFrame.setLocationRelativeTo(null);
            } catch (Exception ignored) {
            }
            cardLayout.show(mainContainer, "MENU");
            jFrame.revalidate();
        });

        Dimension gap = new Dimension(0, 15);
        add(Box.createVerticalGlue());
        add(resolutionCombo);
        add(Box.createRigidArea(gap));
        add(fragmentsCheckBox);
        add(Box.createRigidArea(gap));
        add(backButton);
        add(Box.createVerticalGlue());
    }
}