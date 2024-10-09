package org.example.entities;

import org.example.settings.GameSettings;

import java.util.concurrent.Semaphore;

public class Bullet {
    private int x, y;
    private boolean alive;
    private Semaphore bulletSemaphore;

    public Bullet(int x, int y) {
        this.x = x + 20;
        this.y = y;
        this.alive = true;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.bulletSemaphore = semaphore;
    }

    public void move() {
        y -= GameSettings.BULLET_SPEED;
        if (y < 0) {
            alive = false;
            releaseSemaphore();
        }
    }

    // Визволення семафора
    // Критична секція
    private synchronized void releaseSemaphore() {
        if (bulletSemaphore != null) {
            bulletSemaphore.release();
            bulletSemaphore = null;
        }
    }

    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        if (!alive) {
            releaseSemaphore();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}