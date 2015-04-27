package com.explicit.divination.utils;

import java.awt.Point;

/**
 * @author Enfilade
 */

public class MousePathPoint extends Point {

    private long finishTime;
    private double lastingTime;
    private int alpha = 255;

    public MousePathPoint(int x, int y, int lastingTime) {
        super(x, y);
        this.lastingTime = lastingTime;
        finishTime = System.currentTimeMillis() + lastingTime;
    }

    public int getAlpha() {
        int newAlpha = ((int) ((finishTime - System.currentTimeMillis()) / (lastingTime / alpha)));
        if (newAlpha > 255)
            newAlpha = 255;
        if (newAlpha < 0)
            newAlpha = 0;
        return newAlpha;
    }

    public boolean isUp() {
        return System.currentTimeMillis() >= finishTime;
    }

}
