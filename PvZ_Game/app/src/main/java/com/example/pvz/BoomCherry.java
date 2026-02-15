package com.example.pvz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;

public class BoomCherry extends Plant {
    private long lastShot = 0;
    private static final long SHOOT_COOLDOWN = 1500;
    private Bitmap boomcherryBitmap;
    private Bitmap boomcherryBitmapScaled;

    public BoomCherry(int col, int row, float x, float y, GameView gameView) {
        super(col, row, x, y);
        hp = 150;
        
        try {
            boomcherryBitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.boomcherry);
            if (boomcherryBitmap != null) {
                boomcherryBitmapScaled = Bitmap.createScaledBitmap(boomcherryBitmap, 120, 120, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameView gameView) {
        long now = System.currentTimeMillis();
        if (now - lastShot > SHOOT_COOLDOWN) {
            List<Zombie> zombies = gameView.getZombiesInRow(row);
            if (!zombies.isEmpty()) {
                gameView.addProjectile(new RedPea(x + 15, y, row));
                lastShot = now;
            }
        }
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (boomcherryBitmapScaled != null) {
            try {
                canvas.drawBitmap(boomcherryBitmapScaled, x - 60, y - 60, paint);
            } catch (Exception e) {
                drawDefault(canvas, paint);
            }
        } else {
            drawDefault(canvas, paint);
        }
    }
    
    private void drawDefault(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, 40, paint);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y - 20, 35, paint);
    }
}
