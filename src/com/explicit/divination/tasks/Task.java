package com.explicit.divination.tasks;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/14
 */

public abstract class Task<C extends ClientContext> extends ClientAccessor<C> {

    public Task(C ctx) {
        super(ctx);
    }

    public abstract boolean activate();
    public abstract void execute();

}
