package com.example.pvz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.res.Resources;

public class PibbyVirus extends Plant {
    private long lastShot = 0;
    private static final long SHOOT_COOLDOWN = 1000; // в 2 раза быстрее чем PeaShooter (2000)
    private Bitmap bitmap;
    
    public PibbyVirus(int col, int row, float x, float y, Resources resources) {
        super(col, row, x, y);
        hp = 100;
        try {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.pibby_peashoter);
            if (bitmap != null) {
                // Масштабировать до 80x80
                bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameView gameView) {
        long now = System.currentTimeMillis();
        if (now - lastShot > SHOOT_COOLDOWN) {
            java.util.List<Zombie> zombies = gameView.getZombiesInRow(row);
            if (!zombies.isEmpty()) {
                gameView.addProjectile(new BlackPea(x + 15, y, row));
                lastShot = now;
            }
        }
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (bitmap != null) {
            try {
                canvas.drawBitmap(bitmap, x - 40, y - 40, paint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
