package org.example;

import org.example.entities.Bullet;
import org.example.entities.Enemy;
import org.example.entities.Gun;
import org.example.settings.GameSettings;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameService {
    Gun gun;
    ArrayList<Bullet> bullets;
    ArrayList<Enemy> enemies;
    int killed_enemies = 0;
    int missed_enemies = 0;
    boolean game_over = false;

    private final Semaphore bulletSemaphore = new Semaphore(3); // Максимум 3 активні кулі
    private final Lock bulletLock = new ReentrantLock();
    private final Lock enemyLock = new ReentrantLock();

    // Об'єкт для синхронізації події початку спавну ворогів
    private final Object enemySpawnEvent = new Object();
    private boolean eventTriggered = false;

    public GameService() {
        this.gun = new Gun(550);
        this.bullets = new ArrayList<>();
        this.enemies = new ArrayList<>();

        // Запуск потоку для очікування 15 секунд перед спавном ворогів
        new Thread(() -> {
            try {
                synchronized (enemySpawnEvent) {
                    enemySpawnEvent.wait(15000); // Очікуємо 15 секунд
                }
                eventTriggered = true; // Подія спрацьовує, якщо час минув
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void shoot() {
        if (bulletSemaphore.tryAcquire()) {
            Bullet bullet = new Bullet(gun.getX(), gun.getY());
            bullets.add(bullet);
            bullet.setSemaphore(bulletSemaphore); // Передаємо семафор кулі для звільнення
        } else {
            System.out.println("Забагато куль на екрані!");
        }
    }

    public void spawn_enemy() {
        // Якщо подія ще не спрацювала, вороги не з'являються
        if (!eventTriggered) return;

        int enemy_count = new Random().nextInt(3) + 1; // Випадкове число ворогів (від 1 до 3)
        ArrayList<Integer> spawned_sections = new ArrayList<>(); // Для відслідковування зайнятих секцій

        for (int i = 0; i < enemy_count; i++) {
            int section = new Random().nextInt(8); // Кількість секцій на екрані

            // Перевіряємо, чи є вороги в цій секції
            if (!spawned_sections.contains(section)) {
                int enemy_x = section * 50; // Координата x ворога
                Enemy enemy = new Enemy(enemy_x);
                enemy.start(); // Важливо: запуск потоку для ворога
                enemies.add(enemy);
                spawned_sections.add(section); // Додаємо секцію в список зайнятих
            }
        }
    }

    public void update() {
        bulletLock.lock();
        try {
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                bullet.move();
                if (!bullet.getAlive()) {
                    bullets.remove(i); // Видалення мертвих куль
                }
            }
        } finally {
            bulletLock.unlock();
        }

        enemyLock.lock();
        try {
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                enemy.move();
                if (!enemy.getAlive() && enemy.getY() >= GameSettings.HEIGHT) {
                    enemies.remove(i); // Видалення мертвих ворогів
                    missed_enemies++; // Збільшуємо лічильник пропущених ворогів
                }
            }
        } finally {
            enemyLock.unlock();
        }

        if (missed_enemies >= 30) {
            game_over = true;
            System.out.println("Ви програли!");
        }

        check_collisions();
    }

    private void check_collisions() {
        bulletLock.lock();
        enemyLock.lock();
        try {
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    if (bullet.getAlive() && enemy.getAlive() &&
                            bullet.getX() < enemy.getX() + 50 && bullet.getX() + 5 > enemy.getX() &&
                            bullet.getY() < enemy.getY() + 50 && bullet.getY() + 15 > enemy.getY()) {

                        bullet.setAlive(false); // Знищуємо кулю
                        enemy.setAlive(false); // Знищуємо ворога
                        killed_enemies++; // Збільшуємо лічильник убитих ворогів
                    }
                }
            }
        } finally {
            bulletLock.unlock();
            enemyLock.unlock();
        }
    }

    // Метод для встановлення першого руху
    public void set_first_move() {
        if (!eventTriggered) {
            synchronized (enemySpawnEvent) {
                enemySpawnEvent.notify(); // Активуємо подію при першому русі
            }
            eventTriggered = true; // Помічаємо, що подія була спрацьована
        }
    }
}

