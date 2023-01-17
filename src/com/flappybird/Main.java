package com.flappybird;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main extends JFrame {
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 800;


    public Main() {
        initUI();
    }

    private void initUI() {
        add(new Board(WIDTH, HEIGHT));

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        setTitle("Flappy Bird clone");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            com.flappybird.Main ex = new com.flappybird.Main();
            ex.setVisible(true);
        });
    }
}