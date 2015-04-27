package com.explicit.divination.context.extensions;

import com.explicit.divination.context.RS3Context;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Widget;

import java.awt.*;

/**
 * ----- Original -----
 * @author Bassnectar
 *
 * ----- Updated 1/7/2014 -----
 * @author Swizzbeat
 */

public class WidgetCloser extends ClientAccessor<RS3Context> {

    private enum UnwantedWidget {
        DEPOSIT_BOX(11, new Rectangle(565, 165, 16, 16)),       //DEPOSIT BOX IN BANK
        COLLECTION_BOX(109, new Rectangle(563, 153, 16, 16)),   //RIGHT CLICK BANKER COLLECT INTERFACE
        CLAN_CUP(205, new Rectangle(636, 137, 16, 16)),         //CLAN CUP PLAQUE NEXT TO EDGEVILLE BANK BOOTH
        BONDS(229, new Rectangle(636, 137, 16, 16)),            //MANAGE BONDS INTERFACE
        GUIDANCE_TIP(669, new Rectangle(349, 363, 100, 27)),    //GUIDANCE TIPS THAT POP-UP
        TRANSACTION(906, new Rectangle(315, 355, 80, 26)),      //WHEN A PLAYER PRESSES 'SUBSCRIBE' or 'SOLOMON STORE'
        LODESTONE(1092, new Rectangle(636, 137, 16, 16)),       //HOME TELEPORT LODESTONE NETWORK 1092, 46
        CLAN_VEXILLUM(1107, new Rectangle(613, 156, 16, 15)),   //WHEN YOU CLICK CLAN BANNER
        EXTRAS(1139, new Rectangle(570, 172, 15, 15)),          //TAB WITH SOLOMON, MEMBERSHIP, BONDS, TREASURE HUNTER
        TASK_COMPLETE(1223, new Rectangle(368, 208, 65, 20)),   //WHEN A PLAYER COMPLETES A TASK
        TREASURE_HUNTER(1253, new Rectangle(761, 39, 24, 24)),  //TREASURE HUNTER INTERFACE
        WORLD_MAP(1422, new Rectangle(768, 5, 24, 24)),         //WORLD MAP
        GUIDE(1477, new Rectangle(760, 23, 24, 24)),            //GUIDE SUCH AS HERO , POWERS, ABILITIES, PRESET INVENT
        RS_HELPER(1496, new Rectangle(620, 156, 24, 24)),       //RS HELPER INTERFACE
        GRAND_EXCHANGE(105, new Rectangle(636, 137, 16, 16));   //GRAND EXCHANGE INTERFACE

        private final int index;
        private final Rectangle boundingRect;
        private Component closeButton;

        UnwantedWidget(final int index, final Rectangle boundingRect) {
            this.index = index;
            this.boundingRect = boundingRect;
            this.closeButton = null;
        }
    }

    private UnwantedWidget unwantedWidget;

    public WidgetCloser(RS3Context ctx) {
        super(ctx);
        this.unwantedWidget = null;
    }

    public boolean closeOpenWidget() {
        if (ctx.bank.opened()) {
            return ctx.bank.close();
        }

        return (unwantedWidget = getUnwantedWidget()) == null || unwantedWidget.closeButton.click();
    }

    private UnwantedWidget getUnwantedWidget() {
        for (final UnwantedWidget unwantedWidget : UnwantedWidget.values()) {
            final Widget widget = ctx.widgets.widget(unwantedWidget.index);
            if (widget.valid() && isCloseButtonVisible(unwantedWidget)) {
                return unwantedWidget;
            }
        }
        return null;
    }

    private boolean isCloseButtonVisible(final UnwantedWidget unwantedWidget) {
        if(unwantedWidget.closeButton == null) {
            for(final Component component : ctx.widgets.widget(unwantedWidget.index).components()) {
                if(component.visible() && component.boundingRect().equals(unwantedWidget.boundingRect)) {
                    unwantedWidget.closeButton = component;
                    break;
                }
                for(int i = 0; i < component.childrenCount(); i ++) {
                    final Component child = component.component(i);
                    if(child.visible() && child.boundingRect().equals(unwantedWidget.boundingRect)) {
                        unwantedWidget.closeButton = child;
                        break;
                    }
                }
            }
            return false;
        }
        return unwantedWidget.closeButton.visible();
    }

}
