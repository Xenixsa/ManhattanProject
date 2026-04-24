package com.demopanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintingPanel extends JPanel {
    final int maxCol = 192;
    final int maxRow = 108;
    final int nodeSize = 10;
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

    }

}
