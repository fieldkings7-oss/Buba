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
        if (eating && targetPlant != null && targetPlant.hp > 0) {
            targetPlant.takeDamage(10);
        } else {
            x -= speed / 60.0f;
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
}
