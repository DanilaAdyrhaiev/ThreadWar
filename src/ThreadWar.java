import java.util.List;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class ThreadWar extends JPanel {
    // Семафор для обмеження кількості куль
    private final Semaphore bulletSemaphore = new Semaphore(3);

    // Лічильники
    private int score = 0;
    private int missedEnemies = 0;

    // Потоки
    private final List<Enemy> enemies = new ArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    // Гармата
    private Cannon cannon;

    public ThreadWar() {
        setFocusable(true);
        cannon = new Cannon(getWidth() / 2);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> cannon.moveLeft();
                    case KeyEvent.VK_RIGHT -> cannon.moveRight();
                    case KeyEvent.VK_SPACE -> shoot();
                }
            }
        });

        // Старт ворогів
        new Thread(this::generateEnemies).start();
    }

    // Генерація ворогів
    private void generateEnemies() {
        Random random = new Random();
        while (missedEnemies < 30) {
            try {
                Thread.sleep(1000 + random.nextInt(2000)); // затримка між ворогами
                Enemy enemy = new Enemy();
                enemies.add(enemy);
                executor.submit(enemy);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        endGame();
    }

    // Постріл
    private void shoot() {
        if (bulletSemaphore.tryAcquire()) {
            Bullet bullet = new Bullet(cannon.getX());
            executor.submit(bullet);
        }
    }

    // Завершення гри
    private void endGame() {
        executor.shutdown();
        JOptionPane.showMessageDialog(this, "Гру завершено. Ваш рахунок: " + score);
        System.exit(0);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        cannon.draw(g);
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
    }

    // Основний метод запуску гри
    public static void main(String[] args) {
        JFrame frame = new JFrame("Thread War");
        ThreadWar game = new ThreadWar();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Timer(16, e -> game.repaint()).start(); // Оновлення екрана
    }
}

class Cannon {
    private int x;

    public Cannon(int startX) {
        this.x = startX;
    }

    public void moveLeft() {
        x = Math.max(x - 10, 0);
    }

    public void moveRight() {
        x = Math.min(x + 10, 800); // Максимальна ширина екрану
    }

    public int getX() {
        return x;
    }

    public void draw(Graphics g) {
        g.fillRect(x, 550, 50, 20); // Малювання гармати
    }
}

class Bullet implements Runnable {
    private int x;
    private int y = 550;

    public Bullet(int startX) {
        this.x = startX;
    }

    @Override
    public void run() {
        try {
            while (y > 0) {
                y -= 5;
                Thread.sleep(50); // Рух кулі
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        g.fillRect(x, y, 5, 10); // Малювання кулі
    }
}

class Enemy implements Runnable {
    private int x;
    private int y = 0;
    private boolean alive = true;

    public Enemy() {
        this.x = new Random().nextInt(800);
    }

    @Override
    public void run() {
        try {
            while (y < 600 && alive) {
                y += 5; // Рух ворога вниз
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        if (alive) {
            g.fillRect(x, y, 20, 20); // Малювання ворога
        }
    }
}