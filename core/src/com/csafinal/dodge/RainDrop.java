package com.csafinal.dodge;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Rectangle;

public class RainDrop extends Rectangle implements Pool.Poolable {

    boolean active;

    public RainDrop() {
        super();
        active = false;
    }

    public void init(int x, int y) {
        this.x = x;
        this.y = y;
        active = true;
    }

    @Override
    public void reset() {
        active = false;
    }
}