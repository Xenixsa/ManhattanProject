package com.settings;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JButton backButton = new JButton("Back");
    private JComboBox<String> resolutionCombo;
    private JCheckBox fragmentsCheckBox;

    public SettingsPanel(JFrame jFrame, JPanel mainMenuPanel, SettingsManager settingsManager) {
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

        String[] resolutions = {"1920x1080", "1280x720", "960x540"};
        resolutionCombo = new JComboBox<>(resolutions);
        resolutionCombo.setSelectedItem(settingsManager.getStringSetting("resolution", "1920x1080"));
        resolutionCombo.setMaximumSize(new Dimension(200, 30));
        resolutionCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        resolutionCombo.addActionListener(e ->
                settingsManager.set("resolution", (String) resolutionCombo.getSelectedItem())
        );

        backButton.addActionListener(e -> {
            jFrame.setContentPane(mainMenuPanel);
            jFrame.revalidate();
            settingsManager.save();
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