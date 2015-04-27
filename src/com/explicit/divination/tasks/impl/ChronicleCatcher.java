package com.explicit.divination.tasks.impl;

import com.explicit.divination.eDivination;
import com.explicit.divination.tasks.Task;
import com.explicit.divination.context.RS3Context;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Npc;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/15
 */

public class ChronicleCatcher extends Task<RS3Context> {

    public ChronicleCatcher(RS3Context ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.backpack.select().count() < 28 && !ctx.npcs.select().name(eDivination.Constants.CHRONICLE_NAME).isEmpty();
    }

    @Override
    public void execute() {
        final Npc chronicle = ctx.npcs.nearest().poll();

        if (ctx.methods.interact(chronicle, "Capture", Random.nextBoolean())) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !chronicle.valid();
                }
            });
        }
    }

}
