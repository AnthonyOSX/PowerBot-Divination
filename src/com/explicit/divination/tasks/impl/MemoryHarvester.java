package com.explicit.divination.tasks.impl;

import com.explicit.divination.context.RS3Context;
import com.explicit.divination.tasks.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Npc;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/15
 */

public class MemoryHarvester extends Task<RS3Context> {

    public MemoryHarvester(RS3Context ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.players.local().animation() == -1 && ctx.backpack.select().count() < 28 && !ctx.npcs.select().select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                String name = npc.name().toLowerCase();

                return name.contains("wisp") || name.contains("spring");
            }
        }).isEmpty();
    }

    @Override
    public void execute() {
        if (ctx.methods.interact(ctx.npcs.nearest().poll(), "Harvest", Random.nextBoolean())) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            });
        }
    }

}
