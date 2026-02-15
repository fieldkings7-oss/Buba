package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BlackPea extends Projectile {
    public BlackPea(float x, float y, int row) {
        super(x, y, 300, 60, row);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x, y, 12, paint);
    }
}
