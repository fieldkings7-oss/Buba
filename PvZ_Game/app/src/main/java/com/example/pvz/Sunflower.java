package com.example.pvz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.res.Resources;

public class Sunflower extends Plant {
    private long lastSunSpawn = 0;
    private static final long SUN_SPAWN_COOLDOWN = 6000; // 6 секунд
    private Bitmap bitmap;
    
    public Sunflower(int col, int row, float x, float y, Resources resources) {
        super(col, row, x, y);
        hp = 80;
        try {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.sunflower);
            if (bitmap != null) {
                // Увеличить подсолнух в 3 раза (было 60, сейчас 180)
                bitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameView gameView) {
        long now = System.currentTimeMillis();
        if (now - lastSunSpawn > SUN_SPAWN_COOLDOWN) {
            // Выпустить солнце как FloatingObject рядом с подсолнухом на земле (не падает)
            // Сдвиг на 100px вправо, лежит на той же высоте
            gameView.addFloatingObject(this.x + 100, this.y + 50, true);
            lastSunSpawn = now;
        }
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, x - 90, y - 90, paint);
        }
    }
}

