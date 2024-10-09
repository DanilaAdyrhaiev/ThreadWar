package org.example.entities;

import org.example.settings.GameSettings;

import java.util.Random;

public class Enemy extends Thread {
    private int x, y;
    private boolean alive;
    private float speed;

    public Enemy(int x) {
        this.x = x;
        this.y = 0;
        this.alive = true;
        this.speed = 0.5f + new Random().nextFloat(GameSettings.ENEMY_SPEED_MIN, GameSettings.ENEMY_SPEED_MAX);
    }

    @Override
    public void run() {
        while (y < GameSettings.HEIGHT && alive) { // Двигаем врага пока он жив и не вышел за экран
            y += speed;
            try {
                Thread.sleep(50); // Задержка для плавного движения
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        alive = false; // Враг "умер", когда вышел за экран
    }

    public void move() {
        if (alive) {
            y += speed; // Враг двигается только если он жив
        }
    }

    // Геттеры и сеттеры
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}

