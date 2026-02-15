package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;

public class CherryBomb extends Plant {
    private long detectionTime = 0;
    private boolean detected = false;

    public CherryBomb(int col, int row, float x, float y) {
        super(col, row, x, y);
        hp = 150;
    }

    @Override
    public void update(GameView gameView) {
        List<Zombie> zombies = gameView.getZombiesInRange(x, y, 200);
        if (!zombies.isEmpty() && !detected) {
            detected = true;
            detectionTime = System.currentTimeMillis();
        }

        if (detected && System.currentTimeMillis() - detectionTime > 1500) {
            explode(gameView);
            hp = 0;
        }
    }

    private void explode(GameView gameView) {
        List<Zombie> zombies = gameView.getZombiesInRange(x, y, 300);
        for (Zombie z : zombies) {
            z.takeDamage(300);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(detected ? Color.RED : Color.RED);
        canvas.drawRect(x - 20, y - 20, x + 20, y + 20, paint);
    }
}
