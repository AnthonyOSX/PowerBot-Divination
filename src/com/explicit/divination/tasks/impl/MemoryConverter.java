package com.explicit.divination.tasks.impl;

import com.explicit.divination.eDivination;
import com.explicit.divination.context.RS3Context;
import com.explicit.divination.tasks.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.Item;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/15
 */

public class MemoryConverter extends Task<RS3Context> {

    public MemoryConverter(RS3Context ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.backpack.select().count() == 28 && !ctx.objects.select().id(eDivination.Constants.RIFT_IDS).isEmpty();
    }

    @Override
    public void execute() {
        if (ctx.methods.interact(ctx.objects.poll(), ctx.properties.getProperty(eDivination.Constants.RIFT_INTERACTION_PROPERTY_KEY, "Convert to experience"), Random.nextBoolean()) && Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().animation() != -1;
            }
        })) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.backpack.select().select(new Filter<Item>() {
                        @Override
                        public boolean accept(Item item) {
                            return item.name().toLowerCase().contains("memory");
                        }
                    }).isEmpty();
                }
            }, 2000, 30);
        }
    }

}
