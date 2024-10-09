package org.example;

import org.example.settings.GameSettings;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Война потоков");
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setSize(GameSettings.WIDTH, GameSettings.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
