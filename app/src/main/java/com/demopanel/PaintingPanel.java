package com.demopanel;

import com.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PaintingPanel extends JPanel {
    // public final int maxCol = 48;  //fullhd 48
//   public int maxCol = 192;  //fullhd 48
    // public final int maxRow = 27;  //       27
//   public int maxRow = 108;  //       27
    // public final int nodeSize = 40;
//   public int nodeSize = 10;
    SettingsManager settingsManager = new SettingsManager();

    String modesStr = settingsManager.getStringSetting("paintingmodes","48x27x40");
    String[] parts = modesStr.split("x");
    public int maxCol = Integer.parseInt(parts[0]);
    public int maxRow = Integer.parseInt(parts[1]);
    public int nodeSize = Integer.parseInt(parts[2]);

    final int screenWidth = nodeSize*maxCol;
    final int screenHeight = nodeSize*maxRow;
    private java.io.File lastDirectory = loadLastDirectory();

    // Odczytuje ostatnio używany folder. Jeśli zapisana ścieżka nie istnieje
    // (np. pochodzi z innego systemu), wraca do katalogu domowego użytkownika.
    private java.io.File loadLastDirectory() {
        String saved = settingsManager.getStringSetting("lastDirectory", "");
        if (!saved.isEmpty()) {
            java.io.File dir = new java.io.File(saved);
            if (dir.exists() && dir.isDirectory()) return dir;
        }
        return new java.io.File(System.getProperty("user.dir")); // katalog domowy użytkownika
    }

    boolean paintMode = false;

    //nodes
    public Block[][] blocks = new Block[maxCol][maxRow];

    public PaintingPanel(){

        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setLayout(new GridLayout(maxRow,maxCol));

        System.out.println(maxCol+"x"+maxRow+"x"+nodeSize);

        int col = 0;
        int row = 0;

        while(col<maxCol && row<maxRow){
            blocks[col][row] = new Block(col,row,this);
            this.add(blocks[col][row]);

            col++;
            if(col ==maxCol){
                System.out.println(row+"/"+maxRow+"     "+col+"/"+maxCol);
                col =0;
                row++;
            }
        }

        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                paintMode = false;
            }
        },AWTEvent.MOUSE_EVENT_MASK
        );

        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "printGrid");
        actionMap.put("printGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                printGrid();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0),"saveGrid");
        actionMap.put("saveGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveGrid();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"exit");
        actionMap.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), "loadGrid");
        actionMap.put("loadGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadGrid();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "resetGrid");
        actionMap.put("resetGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                resetGrid();
            }
        });

    }

    public void saveGrid(){
        JFileChooser fileChooser = new JFileChooser(lastDirectory);
        fileChooser.setDialogTitle("Zapisz siatkę");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));
        fileChooser.setSelectedFile(new java.io.File("grid.txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".txt")) {
            file = new java.io.File(file.getAbsolutePath() + ".txt");
        }

        lastDirectory = file.getParentFile();
        settingsManager.set("lastDirectory", lastDirectory.getAbsolutePath());
        settingsManager.save();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
            for(int r = 0;r<maxRow;r++){
                StringBuilder stringBuilder = new StringBuilder();
                for (int c = 0;c<maxCol;c++){
                    stringBuilder.append(blocks[c][r].isPainted ? 1:0);
                    if (c<maxCol-1){
                        stringBuilder.append(" ");
                    }
                }
                bufferedWriter.write(stringBuilder.toString());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd zapisu pliku:\n" + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void printGrid(){
        for(int r = 0;r<maxRow;r++){
            StringBuilder stringBuilder = new StringBuilder();
            for (int c = 0;c<maxCol;c++){
                stringBuilder.append(blocks[c][r].isPainted ? 1:0);
//                stringBuilder.append(blocks[c][r].isPainted ? 1:" ");
                if (c<maxCol-1){
                    stringBuilder.append(" ");
                }
            }
            System.out.println(stringBuilder);
        }
    }

    public void resetGrid() {
        for (int r = 0; r < maxRow; r++){
            for (int c = 0; c < maxCol; c++) {
                blocks[c][r].clear();
            }
        }
    }

    public void loadGrid() {
        JFileChooser fileChooser = new JFileChooser(lastDirectory);
        fileChooser.setDialogTitle("Wczytaj siatkę");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fileChooser.getSelectedFile();

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            // Wyczyść siatkę
            for (int r = 0; r < maxRow; r++)
                for (int c = 0; c < maxCol; c++)
                    blocks[c][r].clear();

            String line;
            int r = 0;
            while ((line = reader.readLine()) != null && r < maxRow) {
                String[] tokens = line.split(" ");
                for (int c = 0; c < Math.min(tokens.length, maxCol); c++) {
                    if ("1".equals(tokens[c])) {
                        blocks[c][r].paint();
                    }
                }
                r++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd wczytywania pliku:\n" + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
        lastDirectory = file.getParentFile();
        settingsManager.set("lastDirectory", lastDirectory.getAbsolutePath());
        settingsManager.save();
    }

}
