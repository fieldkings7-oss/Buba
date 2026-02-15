package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import pl.droidsonroids.gif.GifDrawable;
import java.io.IOException;
import java.util.List;

public class Zombie {
    public float x, y;
    public int row;
    public int hp = 100;
    private float speed = 50;
    private Plant targetPlant;
    private boolean eating = false;
    private GifDrawable gifDrawable;
    
    // Статус отравления
    private boolean poisoned = false;
    private long poisonedTime = 0;
    private static final long POISON_DURATION = 8000; // 8 сек отравления
    private static final float POISON_SPEED_MULTIPLIER = 0.3f; // 30% от обычной скорости

    public Zombie(float x, float y, int row, GameView gameView) {
        this.x = x;
        this.y = y;
        this.row = row;
        
        try {
            gifDrawable = new GifDrawable(gameView.getResources(), R.drawable.zombie);
        } catch (IOException e) {
            e.printStackTrace();
            gifDrawable = null;
        }
    }

    public void update(GameView gameView) {
        // Проверить окончание отравления
        if (poisoned) {
            long now = System.currentTimeMillis();
            if (now - poisonedTime > POISON_DURATION) {
                poisoned = false;
            }
        }
        
        if (eating && targetPlant != null && targetPlant.hp > 0) {
            targetPlant.takeDamage(10);
        } else {
            // Учитывать замедление если отравлен
            float currentSpeed = poisoned ? (speed * POISON_SPEED_MULTIPLIER) : speed;
            x -= currentSpeed / 60.0f;
            eating = false;

            List<Plant> plants = gameView.getPlants();
            for (Plant plant : plants) {
                if (plant.row == row && Math.abs(plant.x - x) < 50) {
                    targetPlant = plant;
                    eating = true;
                    break;
                }
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (gifDrawable != null) {
            // Увеличен в два раза: 80x80 -> 160x160
            gifDrawable.setBounds((int)(x - 80), (int)(y - 80), (int)(x + 80), (int)(y + 80));
            gifDrawable.draw(canvas);
            
            // Рисовать фиолетовый оверлей если отравлен
            if (poisoned) {
                paint.setColor(0x7700AA00); // фиолетовый с прозрачностью
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(x - 80, y - 80, x + 80, y + 80, paint);
            }
        } else {
            // Fallback если gif не загрузилась
            paint.setColor(Color.GRAY);
            canvas.drawRect(x - 80, y - 80, x + 80, y + 80, paint);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(x - 15, y - 10, 5, paint);
            canvas.drawCircle(x + 15, y - 10, 5, paint);
        }
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }
    
    public void poison() {
        poisoned = true;
        poisonedTime = System.currentTimeMillis();
    }
    
    public boolean isPoisoned() {
        return poisoned;
    }
}
