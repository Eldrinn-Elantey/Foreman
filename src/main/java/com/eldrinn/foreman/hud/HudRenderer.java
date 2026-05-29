package com.eldrinn.foreman.hud;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.eldrinn.foreman.cache.ForemanClientCache;
import com.eldrinn.foreman.config.PinnedTasksConfig;
import com.eldrinn.foreman.data.Subtask;
import com.eldrinn.foreman.data.Task;
import com.eldrinn.foreman.data.TaskStatus;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudRenderer {

    private static final int MAX_SUBTASKS_SHOWN = 3;
    private static final int LINE_H = 10;
    private static final int BLOCK_GAP = 4;
    private static final int PADDING = 4;

    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_GRAY = 0xAAAAAA;
    private static final int COLOR_YELLOW = 0xF0C040;
    private static final int COLOR_GREEN = 0x8BC34A;

    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) return;

        PinnedTasksConfig cfg = ForemanClientCache.getPinConfig();
        if (!cfg.isHudVisible()) return;

        List<Task> pinned = ForemanClientCache.getPinnedTasks();
        if (pinned.isEmpty()) return;

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int sw = res.getScaledWidth();
        int sh = res.getScaledHeight();

        int totalHeight = totalHeight(pinned);
        int blockW = maxBlockWidth(pinned, mc);

        int startX = anchorX(cfg.getAnchor(), sw, blockW) + cfg.getOffsetX();
        int startY = anchorY(cfg.getAnchor(), sh, totalHeight) + cfg.getOffsetY();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glScaled(cfg.getScale(), cfg.getScale(), 1.0);

        // Coordinates must be divided by scale because GL matrix already scaled up
        double s = cfg.getScale();
        int sx = (int) (startX / s);
        int sy = (int) (startY / s);

        if (cfg.isShowBackground()) {
            net.minecraft.client.gui.Gui.drawRect(
                sx - PADDING, sy - PADDING,
                sx + blockW + PADDING, sy + totalHeight + PADDING,
                0x88000000);
        }

        int y = sy;
        for (Task task : pinned) {
            y = drawTaskBlock(mc, task, sx, y);
            y += BLOCK_GAP;
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private int drawTaskBlock(Minecraft mc, Task task, int x, int y) {
        net.minecraft.client.gui.FontRenderer fr = mc.fontRenderer;

        // Status line
        String statusText = "[" + task.status.displayName()
            .toUpperCase() + "]";
        int statusColor = statusColor(task.status);
        fr.drawStringWithShadow(statusText, x, y, statusColor);
        y += LINE_H;

        // Title
        fr.drawStringWithShadow(task.title, x, y, COLOR_WHITE);
        y += LINE_H;

        // Subtasks
        if (!task.subtasks.isEmpty()) {
            int shown = 0;
            for (Subtask st : task.subtasks) {
                if (shown >= MAX_SUBTASKS_SHOWN) break;
                int color = st.checked ? COLOR_GRAY : COLOR_WHITE;
                String label = st.checked ? "§m✔ " + st.title + "§r" : "○ " + st.title;
                fr.drawStringWithShadow(label, x + PADDING, y, color);
                y += LINE_H;
                shown++;
            }
            int remaining = task.subtasks.size() - shown;
            if (remaining > 0) {
                fr.drawStringWithShadow("  +" + remaining + " more", x + PADDING, y, COLOR_GRAY);
                y += LINE_H;
            }
        }

        return y;
    }

    private int totalHeight(List<Task> pinned) {
        int h = 0;
        for (Task t : pinned) {
            h += LINE_H * 2; // status + title
            int subtaskLines = Math.min(t.subtasks.size(), MAX_SUBTASKS_SHOWN);
            h += LINE_H * subtaskLines;
            if (t.subtasks.size() > MAX_SUBTASKS_SHOWN) h += LINE_H; // "+N more"
            h += BLOCK_GAP;
        }
        return h;
    }

    private int maxBlockWidth(List<Task> pinned, Minecraft mc) {
        int max = 80; // minimum width
        net.minecraft.client.gui.FontRenderer fr = mc.fontRenderer;
        for (Task t : pinned) {
            max = Math.max(
                max,
                fr.getStringWidth(
                    "[" + t.status.displayName()
                        .toUpperCase() + "]"));
            max = Math.max(max, fr.getStringWidth(t.title));
            int shown = 0;
            for (Subtask st : t.subtasks) {
                if (shown >= MAX_SUBTASKS_SHOWN) break;
                max = Math.max(max, PADDING + fr.getStringWidth((st.checked ? "✔ " : "○ ") + st.title));
                shown++;
            }
        }
        return max + PADDING * 2;
    }

    private int anchorX(PinnedTasksConfig.Anchor anchor, int sw, int blockW) {
        switch (anchor) {
            case TOP_LEFT:
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                return 2;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                return (sw - blockW) / 2;
            default: // RIGHT
                return sw - blockW - 2;
        }
    }

    private int anchorY(PinnedTasksConfig.Anchor anchor, int sh, int totalH) {
        switch (anchor) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                return 2;
            case MIDDLE_LEFT:
            case MIDDLE_CENTER:
            case MIDDLE_RIGHT:
                return (sh - totalH) / 2;
            default: // BOTTOM
                return sh - totalH - 2;
        }
    }

    private int statusColor(TaskStatus status) {
        switch (status) {
            case IN_PROGRESS:
                return COLOR_YELLOW;
            case DONE:
                return COLOR_GREEN;
            default:
                return COLOR_GRAY;
        }
    }
}
