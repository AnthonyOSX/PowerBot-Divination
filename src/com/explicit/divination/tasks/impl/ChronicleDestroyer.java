package com.explicit.divination.tasks.impl;

import com.explicit.divination.context.RS3Context;
import com.explicit.divination.eDivination;
import com.explicit.divination.tasks.Task;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.Component;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/15
 */

public class ChronicleDestroyer extends Task<RS3Context> {

    public ChronicleDestroyer(RS3Context ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return Boolean.parseBoolean(ctx.properties.getProperty(eDivination.Constants.DESTROY_CHRONICLES_PROPERTY_KEY, "false")) && !ctx.backpack.select().name(eDivination.Constants.CHRONICLE_NAME).isEmpty();
    }

    @Override
    public void execute() {
        if (ctx.backpack.poll().interact("Destroy")) {
            final Component[] destroy = new Component[1];

            if (Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return (destroy[0] = ctx.methods.searchComponentsForText("yes")) != null;
                }
            }) && destroy[0].click()) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.backpack.select().name(eDivination.Constants.CHRONICLE_NAME).isEmpty();
                    }
                });
            }
        }
    }

}
