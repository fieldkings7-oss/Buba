package com.example.pvz;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Projectile {
    public float x, y;
    public float speed;
    public int damage;
    public int row;

    public Projectile(float x, float y, float speed, int damage, int row) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.row = row;
    }

    public void update(float delta) {
        x += speed * delta;
    }

    public boolean isOffScreen() {
        return x > 1280;
    }

    public abstract void draw(Canvas canvas, Paint paint);
}
