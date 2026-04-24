package com.demopanel;

import javax.swing.*;
import java.awt.*;

public class DemoPanel extends JPanel {
    final int maxCol = 15;
    final int maxRow = 10;
    final int nodeSize = 70;
    final int screenWidth = nodeSize*maxCol;
    final int screenHeight = nodeSize*maxRow;

    //nodes
    Node[][] nodes = new Node[maxCol][maxRow];

    public DemoPanel(){

        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setLayout(new GridLayout(maxRow,maxCol));

        int col = 0;
        int row = 0;

        while(col<maxCol && row<maxRow){
            nodes[col][row] = new Node(col,row);
            this.add(nodes[col][row]);

            col++;
            if(col ==maxCol){
                col =0;
                row++;
            }
        }

    }

}
