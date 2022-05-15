package com.csafinal.dodge;

import com.badlogic.gdx.math.MathUtils;

public class PointsText {
    static float lifetime = 1000f; //ms
    final String text;
    final double scale;
    final long timeCreated;
    int x;
    int y;

    public PointsText(final String text, int x, int y, final long creation) {
        this.text = text;
        this.scale = MathUtils.random(.4f, .7f);
        this.x = x;
        this.y = y;
        timeCreated = creation;
    }
}
