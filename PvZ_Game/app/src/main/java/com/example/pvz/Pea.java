package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Pea extends Projectile {
    public Pea(float x, float y, int row) {
        super(x, y, 300, 15, row);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        canvas.drawCircle(x, y, 12, paint);
    }
}
