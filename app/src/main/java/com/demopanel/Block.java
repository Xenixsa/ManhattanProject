package com.demopanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Node extends JButton implements ActionListener {

    Node parent;
    int col;
    int row;

    public Node(int col,int row){
        this.col = col;
        this.row = row;

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        setBackground(Color.BLUE);
    }
}
