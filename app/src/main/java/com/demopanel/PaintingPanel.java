package com.demopanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PaintingPanel extends JPanel {
    final int maxCol = 48;  //fullhd 48
    final int maxRow = 25;  //       25
    final int nodeSize = 40;
    final int screenWidth = nodeSize*maxCol;
    final int screenHeight = nodeSize*maxRow;

    boolean paintMode = false;

    //nodes
    Block[][] blocks = new Block[maxCol][maxRow];

    public PaintingPanel(){

        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setLayout(new GridLayout(maxRow,maxCol));

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
    }

    public void saveGrid(){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("grid.txt"))){
            for(int r = 0;r<maxRow;r++){
                StringBuilder stringBuilder = new StringBuilder();
                for (int c = 0;c<maxCol;c++){
                    stringBuilder.append(blocks[c][r].isPainted ? 1:0);
//                    stringBuilder.append(blocks[c][r].isPainted ? 1:" ");
                    if (c<maxCol-1){
                        stringBuilder.append(" ");
                    }
                }
                bufferedWriter.write(stringBuilder.toString());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

}
