package com.csafinal.dodge;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Rectangle;

public class RainDrop extends Rectangle {
    boolean active;
    final double speedMultiplier;
    final int quantity;
    int value;

    public RainDrop() {
        active = false;
        speedMultiplier = 1;
        quantity = 1;
        value = 1;
    }

    public RainDrop(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        value = (int) (speedMultiplier * 1.5);
        quantity = 1;
    }

    public RainDrop(int quantity) {
        speedMultiplier = 1;
        this.quantity = quantity;
        value = quantity;
    }

    public void init(int x, int y) {
        this.x = x;
        this.y = y;
        active = true;
    }
}