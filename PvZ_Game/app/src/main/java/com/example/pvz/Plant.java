package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class Plant {
    public int col, row;
    public float x, y;
    public int hp = 100;

    public Plant(int col, int row, float x, float y) {
        this.col = col;
        this.row = row;
        this.x = x;
        this.y = y;
    }

    public abstract void update(GameView gameView);
    
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        canvas.drawRect(x - 40, y - 40, x + 40, y + 40, paint);
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }
}
