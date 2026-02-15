package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class RedPea extends Projectile {
    public RedPea(float x, float y, int row) {
        super(x, y, 300, 100, row);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, 12, paint);
    }
}
