package com.explicit.divination;

import com.explicit.divination.tasks.impl.ChronicleCatcher;
import com.explicit.divination.tasks.impl.ChronicleDestroyer;
import com.explicit.divination.tasks.impl.MemoryConverter;
import com.explicit.divination.tasks.impl.MemoryHarvester;
import com.explicit.divination.context.RS3Context;
import com.explicit.divination.tasks.Task;
import com.explicit.divination.utils.MousePathPoint;
import com.explicit.divination.utils.Painter;
import org.powerbot.script.*;
import org.powerbot.script.rt6.Skills;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 1/7/15
 */

@Script.Manifest(
        name = "eDivination",
        description = "On the fly customizable Divination trainer. Works in every location and achieves maximum experience rates.",
        properties = "client=6;topic=1240637;hidden=false;vip=false"
)
public class eDivination extends PollingScript<RS3Context> implements PaintListener, MessageListener, BotMenuListener {

    private final List<Task<RS3Context>> tasks;

    private Thread mainThread;
    private Color backgroundColor;
    private Color textColor;
    private Painter painter;
    private Painter.PaintProperty time;
    private Painter.PaintProperty converted;
    private Painter.PaintProperty chronicles;
    private Painter.PaintProperty xp;
    private Painter.PaintProperty level;
    private Painter.PaintProperty timeTill;
    private LinkedList<MousePathPoint> mousePath;
    private long angle;
    private int startLevel;
    private int startXP;
    private int convertedCount;
    private int chroniclesCollected;

    public eDivination() {
        this.tasks = new ArrayList<Task<RS3Context>>();
        this.backgroundColor = new Color(69, 47, 158);
        this.textColor = new Color(47, 158, 146);
        this.painter = new Painter("eDivination", "1.0.0", backgroundColor, textColor);
        this.time = new Painter.PaintProperty();
        this.converted = new Painter.PaintProperty();
        this.chronicles = new Painter.PaintProperty();
        this.xp = new Painter.PaintProperty();
        this.level = new Painter.PaintProperty();
        this.timeTill = new Painter.PaintProperty();
        this.mousePath = new LinkedList<MousePathPoint>();
    }

    @Override
    public void start() {
        mainThread = Thread.currentThread();
        startLevel = ctx.skills.realLevel(Skills.DIVINATION);
        startXP = ctx.skills.experience(Skills.DIVINATION);

        Collections.addAll(
                tasks,
                new ChronicleCatcher(ctx),
                new ChronicleDestroyer(ctx),
                new MemoryConverter(ctx),
                new MemoryHarvester(ctx)
        );
    }

    @Override
    public void poll() {
        for (Task<RS3Context> t : tasks) {
            if (t.activate()) {
                t.execute();
            }
        }

        Condition.sleep(Random.getDelay());
    }

    @Override
    public void repaint(Graphics graphics) {
        long runtime = ctx.controller.script().getRuntime();
        int xpGained = ctx.skills.experience(Skills.DIVINATION) - startXP;
        int levelsGained = ctx.skills.realLevel(Skills.DIVINATION) - startLevel;

        drawMouse((Graphics2D) graphics);

        painter.properties(
                time.value("Runtime: " + painter.formatTime(runtime)),
                converted.value("Converted (P/H): " + painter.format(convertedCount) + " (" + painter.format(painter.getHourlyRate(convertedCount, runtime)) + ")"),
                chronicles.value("Chronicles (P/H): " + painter.format(chroniclesCollected) + " (" + painter.format(painter.getHourlyRate(chroniclesCollected, runtime)) + ")"),
                xp.value("XP (P/H): " + painter.format(xpGained) + " (" + painter.format(painter.getHourlyRate(xpGained, runtime)) + ")"),
                level.value("Level: " + startLevel + " + (" + levelsGained + ")"),
                timeTill.value("TTL: " + painter.getTimeToLevel(ctx, Skills.DIVINATION, ctx.skills.realLevel(Skills.DIVINATION) + 1, (int) painter.getHourlyRate(xpGained, runtime)))
        ).draw(graphics);
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        String message = messageEvent.text().toLowerCase();

        if (message.contains("you convert")) {
            ++convertedCount;
        } else if (message.contains("you capture")) {
            ++chroniclesCollected;
        }
    }

    @Override
    public void menuSelected(MenuEvent e) {
        JMenu menu = (JMenu) e.getSource();
        ButtonGroup convertingStringGroup = new ButtonGroup();
        JRadioButtonMenuItem toEnergy = new JRadioButtonMenuItem("Convert to energy");
        JRadioButtonMenuItem toExperience = new JRadioButtonMenuItem("Convert to experience");
        JRadioButtonMenuItem toEnhancedExperience = new JRadioButtonMenuItem("Convert to enhanced experience");
        final JCheckBoxMenuItem destroyChronicles = new JCheckBoxMenuItem("Destroy chronicles");
        JMenuItem showStackTrace = new JMenuItem("[DEBUG] Show Stack Trace");
        ActionListener riftInteractionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRiftInteraction((JRadioButtonMenuItem) e.getSource());
            }
        };

        toEnergy.setSelected(ctx.properties.getProperty(Constants.RIFT_INTERACTION_PROPERTY_KEY, toExperience.getText()).equals(toEnergy.getText()));
        toExperience.setSelected(ctx.properties.getProperty(Constants.RIFT_INTERACTION_PROPERTY_KEY, toExperience.getText()).equals(toExperience.getText()));
        toEnhancedExperience.setSelected(ctx.properties.getProperty(Constants.RIFT_INTERACTION_PROPERTY_KEY, toExperience.getText()).equals(toEnhancedExperience.getText()));
        destroyChronicles.setSelected(Boolean.parseBoolean(ctx.properties.getProperty(Constants.DESTROY_CHRONICLES_PROPERTY_KEY, "false")));

        convertingStringGroup.add(toEnergy);
        convertingStringGroup.add(toExperience);
        convertingStringGroup.add(toEnhancedExperience);

        toEnergy.addActionListener(riftInteractionListener);
        toExperience.addActionListener(riftInteractionListener);
        toEnhancedExperience.addActionListener(riftInteractionListener);
        destroyChronicles.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ctx.properties.setProperty(Constants.DESTROY_CHRONICLES_PROPERTY_KEY, String.valueOf(destroyChronicles.isSelected()));
            }
        });
        showStackTrace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, new Object[]{
                        mainThread.getStackTrace()
                });
            }
        });

        menu.add(toEnergy);
        menu.add(toExperience);
        menu.add(toEnhancedExperience);
        menu.addSeparator();
        menu.add(destroyChronicles);
        menu.addSeparator();
        menu.add(showStackTrace);
    }

    @Override
    public void menuDeselected(MenuEvent e) {}

    @Override
    public void menuCanceled(MenuEvent e) {}

    private void drawMouse(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();
        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        //MOUSE TRAIL
        while (!mousePath.isEmpty() && mousePath.peek().isUp())
            mousePath.remove();
        Point mousePosition = ctx.input.getLocation();
        MousePathPoint mpp = new MousePathPoint(mousePosition.x, mousePosition.y, 300);
        if (mousePath.isEmpty() || !mousePath.getLast().equals(mpp))
            mousePath.add(mpp);
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : mousePath) {
            if (lastPoint != null) {
                g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), a.getAlpha()));  //trail color
                g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
            }
            lastPoint = a;
        }

        g.setStroke(new BasicStroke(2));
        g.setColor(backgroundColor);
        g.drawLine(mousePosition.x - 3, mousePosition.y - 3, mousePosition.x + 2, mousePosition.y + 2);
        g.drawLine(mousePosition.x - 3, mousePosition.y + 2, mousePosition.x + 2, mousePosition.y - 3);

        g.rotate(Math.toRadians(angle += 6), mousePosition.x, mousePosition.y);

        g.draw(new Arc2D.Double(mousePosition.x - 12, mousePosition.y - 12, 24, 24, 330, 60, Arc2D.OPEN));
        g.draw(new Arc2D.Double(mousePosition.x - 12, mousePosition.y - 12, 24, 24, 151, 60, Arc2D.OPEN));

        g.setTransform(oldTransform);
    }

    private void setRiftInteraction(JRadioButtonMenuItem radioButtonMenuItem) {
        ctx.properties.setProperty(Constants.RIFT_INTERACTION_PROPERTY_KEY, radioButtonMenuItem.getText());
    }

    public static class Constants {

        public static final String CHRONICLE_NAME = "Chronicle fragment";
        public static final String RIFT_INTERACTION_PROPERTY_KEY = "rift_interaction";
        public static final String DESTROY_CHRONICLES_PROPERTY_KEY = "destroy_chronicles";

        public static final int[] RIFT_IDS = {87306, 93489};

    }

}
