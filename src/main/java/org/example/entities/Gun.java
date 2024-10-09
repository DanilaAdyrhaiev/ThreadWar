package org.example.entities;

import org.example.GameService;
import org.example.settings.GameSettings;

public class Gun {
    private int x, y;

    public Gun(int y) {
        this.x = 200;
        this.y = y;
    }

    public void move(int direction, GameService gameService) {
        x += direction * 50;
        if (x < 0) x = 0;
        if (x > GameSettings.WIDTH - 50) x = GameSettings.WIDTH - 50;
        gameService.set_first_move();
    }

    // Геттеры и сеттеры
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
