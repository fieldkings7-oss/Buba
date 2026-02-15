package com.example.pvz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import pl.droidsonroids.gif.GifDrawable;
import java.io.IOException;
import java.util.List;

public class PeaShooter extends Plant {
    private long lastShot = 0;
    private static final long SHOOT_COOLDOWN = 1500;
    private Bitmap shooterBitmap;
    private Bitmap shooterBitmapScaled;
    private GifDrawable shootGif;
    private long shootStartTime = 0;
    private boolean isShooting = false;

    public PeaShooter(int col, int row, float x, float y, GameView gameView) {
        super(col, row, x, y);
        hp = 100;
        
        try {
            // Загрузить статичное изображение горохострела
            shooterBitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.peashooter);
            if (shooterBitmap != null) {
                shooterBitmapScaled = Bitmap.createScaledBitmap(shooterBitmap, 100, 100, true);
            }
            
            // Загрузить gif анимацию стрельбы
            shootGif = new GifDrawable(gameView.getResources(), R.drawable.peashooter_shoot);
        } catch (IOException e) {
            e.printStackTrace();
            shootGif = null;
        } catch (Exception e) {
            e.printStackTrace();
            shooterBitmap = null;
        }
    }

    @Override
    public void update(GameView gameView) {
        long now = System.currentTimeMillis();
        if (now - lastShot > SHOOT_COOLDOWN) {
            List<Zombie> zombies = gameView.getZombiesInRow(row);
            if (!zombies.isEmpty()) {
                gameView.addProjectile(new Pea(x + 15, y, row));
                lastShot = now;
                shootStartTime = now;
                isShooting = true;
            }
        }
        
        // Проверить отошла ли анимация стрельбы обратно
        if (isShooting && now - shootStartTime > 500) {
            isShooting = false;
        }
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (isShooting && shootGif != null) {
            try {
                // Показать gif анимацию стрельбы
                shootGif.setBounds((int)(x - 50), (int)(y - 50), (int)(x + 50), (int)(y + 50));
                shootGif.draw(canvas);
            } catch (Exception e) {
                // Fallback на статичное изображение
                drawDefault(canvas, paint);
            }
        } else if (shooterBitmapScaled != null) {
            try {
                // Показать масштабированную картинку с центром на x,y
                canvas.drawBitmap(shooterBitmapScaled, x - 50, y - 50, paint);
            } catch (Exception e) {
                // Fallback на цветную отрисовку
                drawDefault(canvas, paint);
            }
        } else {
            // Fallback цветная отрисовка
            drawDefault(canvas, paint);
        }
    }
    
    private void drawDefault(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 35, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x + 15, y - 15, 10, paint);
    }
}
