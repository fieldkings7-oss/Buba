package com.example.pvz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private Bitmap backgroundBitmap;
    private Bitmap sunDropBitmap;
    
    private static final int ROWS = 5;
    private static final int COLS = 9;
    private int CELL_SIZE = 80;
    private int OFFSET_X = 0;
    private int OFFSET_Y = 0;
    private int screenWidth = 1;
    private int screenHeight = 1;
    
    private List<Plant> plants = new ArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private List<FloatingObject> floatingObjects = new ArrayList<>();
    private int sun = 50;
    private int coins = 0;
    private String selectedPlant = "";
    private long lastZombieSpawn = 0;
    private long lastSunSpawn = 0;
    private int zombiesSpawned = 0;
    private boolean gameOver = false;
    private boolean levelComplete = false;
    private boolean hasBoomCherry = false;
    private String mapType = "default"; // default или prepared_yard
    private long zombieSpawnCooldown = 5000; // базовое значение 5 сек
    private int maxZombies = 8;  // максимум зомби

    public GameView(Context context) {
        this(context, "default");
    }
    
    public GameView(Context context, String mapType) {
        super(context);
        this.mapType = mapType;
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        screenWidth = getWidth();
        screenHeight = getHeight();
        
        // Загрузить фон в зависимости от типа карты
        int bgResource = R.drawable.game_bg;
        if ("prepared_yard".equals(mapType)) {
            bgResource = R.drawable.prepared_yard_bg;
        }
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), bgResource);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth, screenHeight, true);
        
        // Загрузить спрайт падающего солнца - увеличено в 5 раз
        sunDropBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun_drop);
        if (sunDropBitmap != null) {
            sunDropBitmap = Bitmap.createScaledBitmap(sunDropBitmap, 250, 250, true);
        }
        
        // Загрузить статус BoomCherry
        SharedPreferences prefs = getContext().getSharedPreferences("game_data", Context.MODE_PRIVATE);
        hasBoomCherry = prefs.getBoolean("has_boomcherry", false);
        
        // Большие клеточки - 9x5 сетка под фото
        CELL_SIZE = (int) (screenHeight * 0.15f);
        OFFSET_X = (screenWidth - (COLS * CELL_SIZE)) / 2;
        OFFSET_Y = (int) (screenHeight * 0.25f);
        
        // Установить сложность в зависимости от карты
        if ("prepared_yard".equals(mapType)) {
            zombieSpawnCooldown = 4000; // быстрее спавнятся (4 сек вместо 5)
            maxZombies = 12;  // больше зомби (12 вместо 8)
        }
        
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

        // Spawn zombies - они шли постепенно, не толпой
        long now = System.currentTimeMillis();
        if (now - lastZombieSpawn > zombieSpawnCooldown && zombiesSpawned < maxZombies) {
            spawnZombie();
            lastZombieSpawn = now;
        }
        
        // Спавнить солнца случайно от 7 до 24 секунд
        if (now - lastSunSpawn > (7000 + (long)(Math.random() * 17000))) {
            spawnFloatingSun();
            lastSunSpawn = now;
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
            }
        }

        // Update projectiles
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.update(0.016f);
            
            // Check collision with zombies
            boolean hit = false;
            for (int z = 0; z < zombies.size(); z++) {
                Zombie zombie = zombies.get(z);
                float dist = (float) Math.sqrt((p.x - zombie.x) * (p.x - zombie.x) + 
                                              (p.y - zombie.y) * (p.y - zombie.y));
                if (dist < 30) {
                    zombie.takeDamage(p.damage);
                    hit = true;
                    break;
                }
            }
            
            if (hit || p.isOffScreen()) {
                projectiles.remove(i);
                i--;
            }
        }
        
        // Update floating objects (солнца)
        for (int i = 0; i < floatingObjects.size(); i++) {
            FloatingObject obj = floatingObjects.get(i);
            obj.update(0.016f);
            
            // Остановить солнце когда оно достигает земли (примерно 80% от высоты)
            if (!obj.grounded && obj.y > screenHeight * 0.8f) {
                obj.grounded = true;
            }
            
            if (obj.isOffScreen(screenHeight)) {
                floatingObjects.remove(i);
                i--;
            }
        }

        // Check win
        if (zombiesSpawned >= maxZombies && zombies.isEmpty()) {
            levelComplete = true;
            coins += 100;
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            
            // Рисовать фон изображение
            if (backgroundBitmap != null) {
                canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            // Рисовать растения
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
            
            // Draw floating objects (солнца)
            for (FloatingObject obj : floatingObjects) {
                obj.draw(canvas, paint, screenHeight);
            }

            // Draw UI
            paint.setTextSize(32);
            paint.setColor(Color.WHITE);
            canvas.drawText("Sun: " + sun, 50, 50, paint);
            canvas.drawText("Coins: " + coins, 50, 100, paint);
            
            // Прогресс бар наступления зомби
            if (!zombies.isEmpty()) {
                // Найти самого близкого зомби к левому краю
                float minX = Float.MAX_VALUE;
                for (Zombie z : zombies) {
                    if (z.x < minX) {
                        minX = z.x;
                    }
                }
                
                // Считать прогресс: от screenWidth(0%) до 0(100%)
                float progress = 1.0f - (minX / screenWidth);
                progress = Math.max(0, Math.min(1, progress));
                
                // Рисовать прогресс бар
                int barWidth = 300;
                int barHeight = 30;
                int barX = screenWidth - barWidth - 20;
                int barY = 20;
                
                // Фон бара
                paint.setColor(Color.DKGRAY);
                canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paint);
                
                // Заполненная часть
                paint.setColor(Color.RED);
                canvas.drawRect(barX, barY, barX + (int)(barWidth * progress), barY + barHeight, paint);
                
                // Граница
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paint);
                paint.setStyle(Paint.Style.FILL);
                
                // Флаг/метка конца поля
                paint.setColor(Color.YELLOW);
                paint.setTextSize(28);
                canvas.drawText("🚩", barX - 30, barY + 30, paint);
            }
            
            if (levelComplete) {
                paint.setColor(Color.GREEN);
                paint.setTextSize(64);
                canvas.drawText("VICTORY!", 300, 360, paint);
                
                // Сохранить монеты и перейти в меню
                saveCoinsAndReturnToMenu();
            }
            
            if (gameOver) {
                paint.setColor(Color.RED);
                paint.setTextSize(64);
                canvas.drawText("GAME OVER!", 250, 360, paint);
            }

            // Draw buttons
            int buttonY = 20;
            int buttonHeight = 80;
            int buttonWidth = 150;
            int buttonX1 = 20;
            int buttonX2 = 200;
            int buttonX3 = 380;
            
            paint.setColor(Color.YELLOW);
            canvas.drawRect(buttonX1, buttonY, buttonX1 + buttonWidth, buttonY + buttonHeight, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(20);
            canvas.drawText("Sun", buttonX1 + 40, buttonY + 60, paint);

            paint.setColor(Color.BLUE);
            canvas.drawRect(buttonX2, buttonY, buttonX2 + buttonWidth, buttonY + buttonHeight, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(24);
            canvas.drawText("Pea", buttonX2 + 50, buttonY + 60, paint);

            paint.setColor(Color.RED);
            int buttonX4 = 560;
            canvas.drawRect(buttonX4, buttonY, buttonX4 + buttonWidth, buttonY + buttonHeight, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("Bomb", buttonX4 + 40, buttonY + 60, paint);
            
            if (hasBoomCherry) {
                paint.setColor(Color.MAGENTA);
                canvas.drawRect(buttonX3, buttonY, buttonX3 + buttonWidth, buttonY + buttonHeight, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(20);
                canvas.drawText("BoomCh", buttonX3 + 25, buttonY + 60, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Check if collecting floating sun
            for (int i = 0; i < floatingObjects.size(); i++) {
                FloatingObject obj = floatingObjects.get(i);
                float dist = (float) Math.sqrt((x - obj.x) * (x - obj.x) + (y - obj.y) * (y - obj.y));
                if (dist < obj.getRadius()) {
                    sun += obj.value;
                    floatingObjects.remove(i);
                    return true;
                }
            }
            
            // Check buttons
            if (x > 20 && x < 170 && y > 20 && y < 100) {
                selectedPlant = "sun";
                return true;
            }
            if (x > 200 && x < 350 && y > 20 && y < 100) {
                selectedPlant = "pea";
                return true;
            }
            // BoomCherry - если куплена, нажимается на позицию 380
            if (hasBoomCherry && x > 380 && x < 530 && y > 20 && y < 100) {
                selectedPlant = "boom";
                return true;
            }
            // Bomb - если BoomCherry не куплена, также на позицию 380
            if (!hasBoomCherry && x > 380 && x < 530 && y > 20 && y < 100) {
                selectedPlant = "bomb";
                return true;
            }
            // Bomb - вторая позиция на 560
            if (x > 560 && x < 710 && y > 20 && y < 100) {
                selectedPlant = "bomb";
                return true;
            }

            // Check grid
            if (x > OFFSET_X && y > OFFSET_Y) {
                int col = (int) ((x - OFFSET_X) / CELL_SIZE);
                int row = (int) ((y - OFFSET_Y) / CELL_SIZE);
                if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
                    placePlant(col, row);
                }
            }
        }
        return true;
    }

    private void placePlant(int col, int row) {
        // Проверить есть ли уже растение на этой ячейке
        boolean cellOccupied = false;
        for (Plant p : plants) {
            if (p.col == col && p.row == row) {
                cellOccupied = true;
                break;
            }
        }
        
        // Если ячейка занята, позволить только BoomCherry если куплен
        if (cellOccupied) {
            if (selectedPlant.equals("boom") && hasBoomCherry && sun >= 200) {
                plants.add(new BoomCherry(col, row, OFFSET_X + col * CELL_SIZE + CELL_SIZE / 2, OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2, this));
                sun -= 200;
            }
            return; // В любом случае не ставим обычные растения на занятую ячейку
        }
        
        // Ячейка свободна - ставим обычные растения
        if (selectedPlant.equals("pea")) {
            if (sun >= 100) {
                plants.add(new PeaShooter(col, row, OFFSET_X + col * CELL_SIZE + CELL_SIZE / 2, OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2, this));
                sun -= 100;
            }
        } else if (selectedPlant.equals("sun")) {
            if (sun >= 50) {
                plants.add(new Sunflower(col, row, OFFSET_X + col * CELL_SIZE + CELL_SIZE / 2, OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2, getResources()));
                sun -= 50;
            }
        } else if (selectedPlant.equals("bomb")) {
            if (sun >= 150) {
                plants.add(new CherryBomb(col, row, OFFSET_X + col * CELL_SIZE + CELL_SIZE / 2, OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2));
                sun -= 150;
            }
        } else if (selectedPlant.equals("boom")) {
            if (hasBoomCherry && sun >= 200) {
                plants.add(new BoomCherry(col, row, OFFSET_X + col * CELL_SIZE + CELL_SIZE / 2, OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2, this));
                sun -= 200;
            }
        }
    }

    private void spawnZombie() {
        int row = (int) (Math.random() * ROWS);
        zombies.add(new Zombie(screenWidth + 100, OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2, row, this));
        zombiesSpawned++;
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void addSun(int amount) {
        sun += amount;
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
    
    public void addFloatingObject(float x, float y) {
        if (sunDropBitmap != null) {
            FloatingObject obj = new FloatingObject(x, y, sunDropBitmap);
            floatingObjects.add(obj);
        }
    }
    
    public void addFloatingObject(float x, float y, boolean grounded) {
        if (sunDropBitmap != null) {
            FloatingObject obj = new FloatingObject(x, y, sunDropBitmap);
            obj.grounded = grounded;
            floatingObjects.add(obj);
        }
    }
    
    private void spawnFloatingSun() {
        // Спавнить солнце в случайном горизонтальном положении
        float randomX = (float) (Math.random() * (screenWidth - 100)) + 50;
        float randomY = -50;
        addFloatingObject(randomX, randomY);
    }
    
    private static boolean victoryHandled = false;

    private void saveCoinsAndReturnToMenu() {
        if (victoryHandled) return;
        victoryHandled = true;
        
        // Сохранить монеты
        SharedPreferences prefs = getContext().getSharedPreferences("game_data", Context.MODE_PRIVATE);
        int currentCoins = prefs.getInt("coins", 0);
        prefs.edit().putInt("coins", currentCoins + coins).commit();
        
        // Запустить MenuActivity
        Intent intent = new Intent(getContext(), MenuActivity.class);
        getContext().startActivity(intent);
        
        // Завершить текущую Activity
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).finish();
        }
    }
    
    public List<Plant> getPlants() {
        return plants;
    }
}
