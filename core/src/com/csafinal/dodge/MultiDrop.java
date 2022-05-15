package com.csafinal.dodge;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

public class MultiDrop extends RainDrop {
    double[] offsets;

    public MultiDrop(int quantity) {
        super(quantity);
    }
    public void init(int x, int y) {
        super.init(x,y);
    }

    public void initOffsets(float oQ) { // float between .1, .3
        double[] temp = new double[quantity];
        for (int i = 0 ; i < quantity; i ++) {
            temp[i] = oQ + 4 *(MathUtils.random() - 0.5);
        }
        offsets = temp;
        System.out.println("OFFSETS: " + Arrays.toString(offsets));
    }
}
