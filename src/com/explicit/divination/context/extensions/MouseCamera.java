package com.explicit.divination.context.extensions;

import com.explicit.divination.context.RS3Context;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.Locatable;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;

/**
 * ----- Original -----
 * @author Coma
 *
 * ----- Updated 1/7/2014 -----
 * @author Swizzbeat
 */

public class MouseCamera extends ClientAccessor<RS3Context> {

    public MouseCamera(RS3Context ctx) {
        super(ctx);
    }

    public boolean turnTo(Locatable locatable) {
        return turnTo(locatable, 0);
    }

    public boolean turnTo(Locatable locatable, int degreesDeviation) {
        return setAngle(mobileAngle(locatable), degreesDeviation);
    }

    public boolean setAngle(int degrees, int degreesDeviation) {
        final double DEGREES_PER_PIXEL_X = 0.35;
        degrees %= 360;
        int angleTo = ctx.camera.angleTo(degrees);
        while (Math.abs(angleTo) > degreesDeviation) {
            angleTo = ctx.camera.angleTo(degrees);
            int pixelsTo = (int) Math.abs(angleTo / DEGREES_PER_PIXEL_X)
                    + Random.nextInt(-(int) (degreesDeviation / DEGREES_PER_PIXEL_X) + 1,
                    (int) (degreesDeviation / DEGREES_PER_PIXEL_X) - 1);
            if (pixelsTo > 450) pixelsTo = pixelsTo / 450 * 450;
            int startY = Random.nextInt(-85, 85) + 200;
            if (angleTo > degreesDeviation) {//right
                int startX = (500 - pixelsTo) - Random.nextInt(0, 500 - pixelsTo - 10);
                dragMouse(startX, startY, startX + pixelsTo, startY + Random.nextInt(-10, 10));
            } else if (angleTo < -degreesDeviation) {//left
                int startX = (pixelsTo + 10) + Random.nextInt(0, 500 - pixelsTo + 10);
                dragMouse(startX, startY, startX - pixelsTo, startY + Random.nextInt(-10, 10));
            }
        }
        return Math.abs(ctx.camera.angleTo(degrees)) <= degreesDeviation;
    }

    public boolean dragMouse(int x1, int y1, int x2, int y2) {
        ctx.input.move(x1, y1);
        ctx.input.press(2);
        ctx.input.move(x2, y2);
        ctx.input.release(2);
        return ctx.input.getLocation().getX() == x2 && ctx.input.getLocation().getY() == y2;
    }

    public boolean setPitch(int pitch, int deviation) {
        final double DEGREES_PER_PIXEL_Y = 0.39;
        while (Math.abs(ctx.camera.pitch() - pitch) > deviation) {
            boolean up = ctx.camera.pitch() < pitch;
            int startX = Random.nextInt(-200, 200) + 250;
            int pixels = (int) (Math.abs(ctx.camera.pitch() - pitch) / DEGREES_PER_PIXEL_Y)
                    + Random.nextInt(-(int) (deviation / DEGREES_PER_PIXEL_Y) + 1,
                    (int) (deviation / DEGREES_PER_PIXEL_Y) - 1);
            if (pixels > 270) pixels = pixels / 270 * 270;
            if (up) {
                int startY = (300 - pixels - 10) - Random.nextInt(0, 300 - pixels - 65);
                dragMouse(startX, startY, startX + Random.nextInt(-10, 10), startY + pixels);

            } else {
                int startY = (60 + pixels + 10) + Random.nextInt(0, 300 - pixels - 70);
                dragMouse(startX, startY, startX + Random.nextInt(-10, 10), startY - pixels);
            }
        }
        return Math.abs(ctx.camera.pitch() - pitch) <= deviation;
    }

    private int mobileAngle(Locatable locatable) {
        final Tile t = locatable.tile();
        final Tile me = ctx.players.local().tile();
        return ((int) Math.toDegrees(Math.atan2(t.y() - me.y(), t.x() - me.x()))) - 90;
    }

}
