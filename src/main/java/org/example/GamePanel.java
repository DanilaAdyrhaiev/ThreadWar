package org.example;

import org.example.entities.Bullet;
import org.example.entities.Enemy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements ActionListener {
    GameService gameService;
    Timer timer;

    public GamePanel() {
        this.gameService = new GameService();
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        gameService.gun.move(-1, gameService);
                        break;
                    case KeyEvent.VK_RIGHT:
                        gameService.gun.move(1, gameService);
                        break;
                    case KeyEvent.VK_SPACE:
                        gameService.shoot(); // Вызов метода shoot
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0); // Завершение игры
                        break;
                }
            }
        });

        timer = new Timer(16, this); // 16 миллисекунд для ~60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameService.game_over) {
            gameService.update();
            if (Math.random() < 0.02) {
                gameService.spawn_enemy();
            }

            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Гра закінчена. Ви програли!");
            System.exit(0); // Завершение программы
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Рисуем пушку
        g.setColor(Color.GREEN);
        g.fillRect(gameService.gun.getX(), gameService.gun.getY(), 50, 30);

        // Рисуем пули
        g.setColor(Color.BLACK);
        for (Bullet bullet : gameService.bullets) {
            if (bullet.getAlive()) {
                g.fillRect(bullet.getX(), bullet.getY(), 5, 15);
            }
        }

        // Рисуем врагов
        g.setColor(Color.RED);
        for (Enemy enemy : gameService.enemies) {
            if (enemy.getAlive()) {
                g.fillRect(enemy.getX(), enemy.getY(), 50, 50);
            }
        }


        g.setColor(Color.BLACK);
        g.drawString("Killed: " + gameService.killed_enemies + " Missed: " + gameService.missed_enemies, 10, 20);
    }
}
