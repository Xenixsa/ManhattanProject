package com.settings;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JButton backButton = new JButton("Back");
    private JComboBox<String> resolutionCombo;
    private JCheckBox fragmentsCheckBox;
    private JCheckBox exitOnNeutronsCheckBox;
    private JComboBox<String> paintingCombo;
    private JButton applyResolutionButton = new JButton("Zastosuj rozdzielczość");

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

        // opis trybu zakończenia - szary tekst nad checkboxem
        JLabel exitModeLabel = new JLabel("<html><center>Domyślnie symulacja kończy się gdy<br>wszystkie atomy się rozszczepią.</center></html>");
        exitModeLabel.setForeground(Color.GRAY);

        JPanel exitModelLabelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        exitModelLabelWrapper.setBackground(Color.BLACK);
        exitModelLabelWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitModelLabelWrapper.add(exitModeLabel);
        exitModelLabelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, exitModelLabelWrapper.getPreferredSize().height));

        // checkbox trybu alternatywnego
        exitOnNeutronsCheckBox = new JCheckBox("Zakończ gdy wszystkie neutrony wylecą poza planszę", false);
        exitOnNeutronsCheckBox.setBackground(Color.BLACK);
        exitOnNeutronsCheckBox.setForeground(Color.WHITE);
        exitOnNeutronsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] resolutions = {"1920x1080", "1280x720", "960x540", "1080x1080","720x720","540x540"};
        resolutionCombo = new JComboBox<>(resolutions);
        resolutionCombo.setSelectedItem(settingsManager.getStringSetting("resolution", "1920x1080"));
        resolutionCombo.setMaximumSize(new Dimension(200, 30));
        resolutionCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] paintingmodes = {"24x13x80","48x27x40","96x54x20","192x108x10"};
        paintingCombo = new JComboBox<>(paintingmodes);
        paintingCombo.setSelectedItem(settingsManager.getStringSetting("paintingmodes","48x27x49"));
        paintingCombo.setMaximumSize(new Dimension(200,30));
        paintingCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        paintingCombo.addActionListener(e ->
                settingsManager.set("paintingmodes", (String) paintingCombo.getSelectedItem())
        );

        backButton.addActionListener(e -> {
            settingsManager.save(); // zapisujemy fragmenty, tryb końca, siatkę itd.
            cardLayout.show(mainContainer, "MENU"); // wracamy do menu BEZ zmiany rozmiaru okna
        });

        applyResolutionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        applyResolutionButton.addActionListener(e -> {
            // dopiero tutaj realnie zmieniamy rozmiar okna i utrwalamy wybór
            String resolution = (String) resolutionCombo.getSelectedItem();
            settingsManager.set("resolution", resolution);
            settingsManager.save();
            try {
                String[] parts = resolution.split("x");
                int winW = Integer.parseInt(parts[0]);
                int winH = Integer.parseInt(parts[1]);
                Point location = jFrame.getLocation(); // zapamiętujemy pozycję, żeby okno nie skakało
                jFrame.setSize(winW, winH);
                jFrame.setLocation(location);
            } catch (Exception ignored) {}
        });

        Dimension gap = new Dimension(0, 15);
        add(Box.createVerticalGlue());
        add(resolutionCombo);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(applyResolutionButton);
        add(Box.createRigidArea(gap));
        add(paintingCombo);
        add(Box.createRigidArea(gap));
        add(fragmentsCheckBox);
        add(Box.createRigidArea(gap));
        add(exitModelLabelWrapper);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(exitOnNeutronsCheckBox);
        add(Box.createRigidArea(gap));
        add(backButton);
        add(Box.createVerticalGlue());
    }

    public boolean isExitOnNeutrons() {
        return exitOnNeutronsCheckBox.isSelected();
    }
}
