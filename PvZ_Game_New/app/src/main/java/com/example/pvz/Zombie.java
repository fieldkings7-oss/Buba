package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;

public class Zombie {
    public float x, y;
    public int row;
    public int hp = 100;
    private float speed = 50;
    private Plant targetPlant;
    private boolean eating = false;

    public Zombie(float x, float y, int row) {
        this.x = x;
        this.y = y;
        this.row = row;
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
        paint.setColor(Color.GRAY);
        canvas.drawRect(x - 20, y - 20, x + 20, y + 20, paint);
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }
}
