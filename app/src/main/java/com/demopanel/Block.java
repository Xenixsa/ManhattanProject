package com.demopanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Block extends JButton implements MouseListener {

    Block parent;
    int col;
    int row;
    PaintingPanel panel;
    boolean isPainted = false;

    public Block(int col, int row,PaintingPanel panel){
        this.col = col;
        this.row = row;
        this.panel = panel;

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        addMouseListener(this);
    }

    public void paint(){
        setBackground(Color.BLUE);
        isPainted = true;
    }

    @Override
    public String toString(){
        if(this.isPainted){
            return " 1 ";
        }else{
            return " 0 ";
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        panel.paintMode = true;
        paint();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        panel.paintMode = false;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if (panel.paintMode){
            paint();
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
