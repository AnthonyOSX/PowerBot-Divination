package com.explicit.divination.context;

import com.explicit.divination.context.extensions.Methods;
import com.explicit.divination.context.extensions.MouseCamera;
import com.explicit.divination.context.extensions.WidgetCloser;
import org.powerbot.script.rt6.ClientContext;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/14
 */

public class RS3Context extends ClientContext {

    public final Methods methods;
    public final MouseCamera mouseCamera;
    public final WidgetCloser widgetCloser;

    public RS3Context(ClientContext ctx) {
        super(ctx);
        this.methods = new Methods(this);
        this.mouseCamera = new MouseCamera(this);
        this.widgetCloser = new WidgetCloser(this);
    }

}
