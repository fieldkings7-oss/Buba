package com.example.pvz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;
    private Thread gameThread;
    private boolean running = false;
    private Paint paint;
    
    private static final int ROWS = 6;
    private static final int COLS = 15;
    private static final int CELL_SIZE = 80;
    
    private List<Plant> plants = new ArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private int sun = 200;
    private int coins = 0;
    private String selectedPlant = "";
    private long lastZombieSpawn = 0;
    private int zombiesSpawned = 0;
    private boolean gameOver = false;
    private boolean levelComplete = false;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            update();
            draw();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (gameOver || levelComplete) return;

        // Spawn zombies
        long now = System.currentTimeMillis();
        if (now - lastZombieSpawn > 3000 && zombiesSpawned < 8) {
            spawnZombie();
            lastZombieSpawn = now;
        }

        // Update plants
        for (Plant plant : plants) {
            plant.update(this);
        }

        // Update zombies
        for (int i = 0; i < zombies.size(); i++) {
            Zombie z = zombies.get(i);
            z.update(this);
            if (z.x < 0) {
                gameOver = true;
            }
            if (z.hp <= 0) {
                zombies.remove(i);
                i--;
                sun += 15;
            }
        }

        // Update projectiles
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.update(0.016f);
            if (p.isOffScreen()) {
                projectiles.remove(i);
                i--;
            }
        }

        // Check win
        if (zombiesSpawned >= 8 && zombies.isEmpty()) {
            levelComplete = true;
            coins += 100;
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            // Draw grid
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(1);
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    int x = col * CELL_SIZE + 100;
                    int y = row * CELL_SIZE + 150;
                    canvas.drawRect(x, y, x + CELL_SIZE, y + CELL_SIZE, paint);
                }
            }

            // Draw plants
            for (Plant plant : plants) {
                plant.draw(canvas, paint);
            }

            // Draw zombies
            for (Zombie zombie : zombies) {
                zombie.draw(canvas, paint);
            }

            // Draw projectiles
            for (Projectile proj : projectiles) {
                proj.draw(canvas, paint);
            }

            // Draw UI
            paint.setTextSize(32);
            paint.setColor(Color.WHITE);
            canvas.drawText("Sun: " + sun, 50, 50, paint);
            canvas.drawText("Coins: " + coins, 50, 100, paint);
            
            if (levelComplete) {
                paint.setColor(Color.GREEN);
                paint.setTextSize(64);
                canvas.drawText("VICTORY!", 300, 360, paint);
            }
            
            if (gameOver) {
                paint.setColor(Color.RED);
                paint.setTextSize(64);
                canvas.drawText("GAME OVER!", 250, 360, paint);
            }

            // Draw buttons
            paint.setColor(Color.BLUE);
            canvas.drawRect(500, 20, 700, 100, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("Pea", 550, 70, paint);

            paint.setColor(Color.RED);
            canvas.drawRect(700, 20, 900, 100, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("Bomb", 750, 70, paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Check buttons
            if (x > 500 && x < 700 && y > 20 && y < 100) {
                selectedPlant = "pea";
                return true;
            }
            if (x > 700 && x < 900 && y > 20 && y < 100) {
                selectedPlant = "bomb";
                return true;
            }

            // Check grid
            if (x > 100 && y > 150) {
                int col = (int) ((x - 100) / CELL_SIZE);
                int row = (int) ((y - 150) / CELL_SIZE);
                if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
                    placePlant(col, row);
                }
            }
        }
        return true;
    }

    private void placePlant(int col, int row) {
        if (selectedPlant.equals("pea")) {
            if (sun >= 50) {
                plants.add(new PeaShooter(col, row, 100 + col * CELL_SIZE + CELL_SIZE / 2, 150 + row * CELL_SIZE + CELL_SIZE / 2));
                sun -= 50;
            }
        } else if (selectedPlant.equals("bomb")) {
            if (sun >= 150) {
                plants.add(new CherryBomb(col, row, 100 + col * CELL_SIZE + CELL_SIZE / 2, 150 + row * CELL_SIZE + CELL_SIZE / 2));
                sun -= 150;
            }
        }
    }

    private void spawnZombie() {
        int row = (int) (Math.random() * ROWS);
        zombies.add(new Zombie(1280, 150 + row * CELL_SIZE + CELL_SIZE / 2, row));
        zombiesSpawned++;
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public List<Zombie> getZombiesInRow(int row) {
        List<Zombie> list = new ArrayList<>();
        for (Zombie z : zombies) {
            if (z.row == row) {
                list.add(z);
            }
        }
        return list;
    }

    public List<Zombie> getZombiesInRange(float x, float y, float range) {
        List<Zombie> list = new ArrayList<>();
        for (Zombie z : zombies) {
            float dist = (float) Math.sqrt((z.x - x) * (z.x - x) + (z.y - y) * (z.y - y));
            if (dist < range) {
                list.add(z);
            }
        }
        return list;
    }

    public List<Plant> getPlants() {
        return plants;
    }
}
