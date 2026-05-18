package com.demopanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class NewPaintingPanel extends JPanel implements MouseListener, MouseMotionListener {

    int screenHeight = 1920;
    int screenWidth = 1080;

    BufferedImage canvas;

    public NewPaintingPanel(){

        //Setting things up for JPanel
        this.setPreferredSize(new Dimension(screenHeight,screenWidth));
        this.setBackground(Color.BLACK);

        setupCanvas();

        //add Mouse Listener things
        addMouseListener(this);
        addMouseMotionListener(this);

        //Keybindings
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"printGrid");
        actionMap.put("printGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0),"saveGrid");
        actionMap.put("saveGrid", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"exit");
        actionMap.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T,0),"ToggleMode");
        actionMap.put("ToggleMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void setupCanvas(){
        canvas = new BufferedImage(screenWidth,screenHeight,BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = canvas.createGraphics();
        graphics2D.setComposite(AlphaComposite.Clear);
        graphics2D.fillRect(0,0,screenWidth,screenHeight);
        graphics2D.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
