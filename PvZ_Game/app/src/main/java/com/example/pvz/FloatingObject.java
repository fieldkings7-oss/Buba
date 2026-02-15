package com.example.pvz;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class FloatingObject {
    public float x, y;
    public float vx = 0, vy = 0.5f;
    private Bitmap bitmap;
    private float rotation = 0;
    private float rotationSpeed = 5f;
    public int value = 25; // Сколько дает солнц
    public boolean collected = false;
    public boolean grounded = false; // Лежит на земле, не падает

    public FloatingObject(float x, float y, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
    }

    public void update(float deltaTime) {
        // Если уже на земле, не падает дальше
        if (!grounded) {
            // Гравитация
            vy += 0.3f;
            
            // Применить скорость
            x += vx;
            y += vy;
        }
        
        // Вращение
        rotation += rotationSpeed;
        if (rotation > 360) rotation -= 360;
    }

    public void draw(Canvas canvas, Paint paint, float screenHeight) {
        if (bitmap != null) {
            // Сохранить текущий canvas state
            canvas.save();
            
            // Перейти в центр объекта
            canvas.translate(x, y);
            
            // Повернуть
            canvas.rotate(rotation);
            
            // Нарисовать
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, paint);
            
            // Восстановить canvas state
            canvas.restore();
        }
    }

    public boolean isOffScreen(float screenHeight) {
        return y > screenHeight + 100;
    }

    public float getRadius() {
        if (bitmap != null) {
            return bitmap.getWidth() / 2f;
        }
        return 25f;
    }
}
