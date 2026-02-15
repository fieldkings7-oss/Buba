package com.example.pvz;

import java.util.List;

public class PeaShooter extends Plant {
    private long lastShot = 0;
    private static final long SHOOT_COOLDOWN = 1500;

    public PeaShooter(int col, int row, float x, float y) {
        super(col, row, x, y);
        hp = 100;
    }

    @Override
    public void update(GameView gameView) {
        long now = System.currentTimeMillis();
        if (now - lastShot > SHOOT_COOLDOWN) {
            List<Zombie> zombies = gameView.getZombiesInRow(row);
            if (!zombies.isEmpty()) {
                gameView.addProjectile(new Pea(x, y, row));
                lastShot = now;
            }
        }
    }
}
