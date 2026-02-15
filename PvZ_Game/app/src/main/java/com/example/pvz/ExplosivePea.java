package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ExplosivePea extends Projectile {
    public ExplosivePea(float x, float y, int row) {
        super(x, y, 280, 50, row);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, 14, paint);
    }
}
