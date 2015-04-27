package com.explicit.divination.context.extensions;

import com.explicit.divination.context.RS3Context;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.*;
import org.powerbot.script.rt6.*;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/14
 */

public class Methods extends ClientAccessor<RS3Context> {

    public Methods(RS3Context ctx) {
        super(ctx);
    }

    public <T extends Interactive & Locatable & Nameable> boolean interact(T target, String action, boolean rightClick) {
        if (target.valid() && ctx.widgetCloser.closeOpenWidget()) {
            if (distance(target) <= Random.nextInt(5, 14)) {
                if (target.inViewport()) {
                    return target.interact(!rightClick, action, target.name()) && ctx.game.crosshair() == Game.Crosshair.ACTION;
                } else {
                    if (ctx.players.local().name().length() > 6) {
                        ctx.mouseCamera.turnTo(target);
                    } else {
                        ctx.camera.turnTo(target, Random.nextInt(-90, 90));
                    }
                }
            } else {
                ctx.movement.step(target);
            }
        }

        return false;
    }

    public int distance(Locatable l2) {
        Tile myTile = ctx.players.local().tile();

        return (int) Math.sqrt(Math.pow(myTile.x() - l2.tile().x(), 2) + Math.pow(myTile.y() - l2.tile().y(), 2));
    }

    public Component searchComponentsForText(String... text) {
        for (Widget w : ctx.widgets) {
            Component c = searchComponentsForText(w.id(), text);

            if (c != null) {
                return c;
            }
        }

        return null;
    }

    public Component searchComponentsForText(int widget, String... text) {
        if (ctx.widgets.widget(widget).valid()) {
            for (Component c : ctx.widgets.widget(widget).components()) {
                for (String s : text) {
                    if (c.visible() && c.text().toLowerCase().contains(s.toLowerCase())) {
                        return c;
                    }
                }
            }
        }

        return null;
    }

}
