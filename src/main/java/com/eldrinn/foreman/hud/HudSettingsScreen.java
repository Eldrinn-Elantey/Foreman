package com.eldrinn.foreman.hud;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Keyboard;

import com.eldrinn.foreman.cache.ForemanClientCache;
import com.eldrinn.foreman.config.PinnedTasksConfig;
import com.eldrinn.foreman.data.Task;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudSettingsScreen extends GuiScreen {

    private static final int HANDLE_SIZE = 10;
    private static final int PANEL_H = 24;
    private static final int PANEL_PADDING = 6;

    private boolean dragging = false;
    private int dragOffsetX; // mouse offset within handle at drag start
    private int dragOffsetY;
    private int dragAnchorX; // anchorX at drag start (constant during drag)
    private int dragAnchorY;

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0x44000000);

        PinnedTasksConfig cfg = ForemanClientCache.getPinConfig();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int sw = res.getScaledWidth();
        int sh = res.getScaledHeight();

        String hint = "Drag handle to reposition HUD  |  ESC to close";
        fontRendererObj.drawStringWithShadow(hint, (sw - fontRendererObj.getStringWidth(hint)) / 2, 6, 0xAAAAAA);

        int[] pos = hudPos(cfg, sw, sh);
        int hx = pos[0];
        int hy = pos[1];
        drawRect(hx, hy, hx + HANDLE_SIZE, hy + HANDLE_SIZE, 0xFFCC3333);
        fontRendererObj.drawStringWithShadow("⇔", hx + 1, hy + 1, 0xFFFFFF);

        drawControlPanel(cfg, sw, sh);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int[] hudPos(PinnedTasksConfig cfg, int sw, int sh) {
        List<Task> pinned = ForemanClientCache.getPinnedTasks();
        return HudRenderer.computeHudPosition(cfg, sw, sh, fontRendererObj, pinned);
    }

    private void drawControlPanel(PinnedTasksConfig cfg, int sw, int sh) {
        int panelW = 300;
        int px = (sw - panelW) / 2;
        int py = sh - PANEL_H - PANEL_PADDING;

        drawRect(px - 4, py - 4, px + panelW + 4, py + PANEL_H + 4, 0xCC000000);

        int cx = px;

        fontRendererObj.drawStringWithShadow("Scale:", cx, py + 7, 0xAAAAAA);
        cx += fontRendererObj.getStringWidth("Scale:") + 4;

        drawRect(cx, py + 2, cx + 14, py + 22, 0xFF444444);
        fontRendererObj.drawStringWithShadow("-", cx + 4, py + 7, 0xFFFFFF);
        cx += 16;

        String scaleLabel = String.format("%.2fx", cfg.getScale());
        fontRendererObj.drawStringWithShadow(scaleLabel, cx, py + 7, 0xFFFFFF);
        cx += fontRendererObj.getStringWidth(scaleLabel) + 4;

        drawRect(cx, py + 2, cx + 14, py + 22, 0xFF444444);
        fontRendererObj.drawStringWithShadow("+", cx + 3, py + 7, 0xFFFFFF);
        cx += 20;

        String bgLabel = "BG: " + (cfg.isShowBackground() ? "ON" : "OFF");
        int bgColor = cfg.isShowBackground() ? 0xFF8BC34A : 0xFFAAAAAA;
        drawRect(cx, py + 2, cx + fontRendererObj.getStringWidth(bgLabel) + 8, py + 22, 0xFF444444);
        fontRendererObj.drawStringWithShadow(bgLabel, cx + 4, py + 7, bgColor);
        cx += fontRendererObj.getStringWidth(bgLabel) + 12;

        String hudLabel = "HUD: " + (cfg.isHudVisible() ? "ON" : "OFF");
        int hudColor = cfg.isHudVisible() ? 0xFF8BC34A : 0xFFAAAAAA;
        drawRect(cx, py + 2, cx + fontRendererObj.getStringWidth(hudLabel) + 8, py + 22, 0xFF444444);
        fontRendererObj.drawStringWithShadow(hudLabel, cx + 4, py + 7, hudColor);
        cx += fontRendererObj.getStringWidth(hudLabel) + 12;

        String resetLabel = "Reset";
        drawRect(cx, py + 2, cx + fontRendererObj.getStringWidth(resetLabel) + 8, py + 22, 0xFF884444);
        fontRendererObj.drawStringWithShadow(resetLabel, cx + 4, py + 7, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) return;
        PinnedTasksConfig cfg = ForemanClientCache.getPinConfig();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int sw = res.getScaledWidth();
        int sh = res.getScaledHeight();

        int[] pos = hudPos(cfg, sw, sh);
        int hx = pos[0];
        int hy = pos[1];

        if (mouseX >= hx && mouseX <= hx + HANDLE_SIZE && mouseY >= hy && mouseY <= hy + HANDLE_SIZE) {
            dragging = true;
            dragOffsetX = mouseX - hx;
            dragOffsetY = mouseY - hy;
            // anchorX = hx - offsetX; store so drag updates offset correctly
            dragAnchorX = hx - cfg.getOffsetX();
            dragAnchorY = hy - cfg.getOffsetY();
            return;
        }

        handlePanelClick(cfg, mouseX, mouseY, sw, sh);
    }

    private void handlePanelClick(PinnedTasksConfig cfg, int mouseX, int mouseY, int sw, int sh) {
        int panelW = 300;
        int px = (sw - panelW) / 2;
        int py = sh - PANEL_H - PANEL_PADDING;

        if (mouseY < py - 4 || mouseY > py + PANEL_H + 4) return;

        int cx = px;

        cx += fontRendererObj.getStringWidth("Scale:") + 4;

        if (mouseX >= cx && mouseX <= cx + 14) {
            cfg.setScale(cfg.getScale() - 0.25);
            return;
        }
        cx += 16;

        String scaleLabel = String.format("%.2fx", cfg.getScale());
        cx += fontRendererObj.getStringWidth(scaleLabel) + 4;

        if (mouseX >= cx && mouseX <= cx + 14) {
            cfg.setScale(cfg.getScale() + 0.25);
            return;
        }
        cx += 20;

        String bgLabel = "BG: " + (cfg.isShowBackground() ? "ON" : "OFF");
        int bgW = fontRendererObj.getStringWidth(bgLabel) + 8;
        if (mouseX >= cx && mouseX <= cx + bgW) {
            cfg.setShowBackground(!cfg.isShowBackground());
            return;
        }
        cx += fontRendererObj.getStringWidth(bgLabel) + 12;

        String hudLabel = "HUD: " + (cfg.isHudVisible() ? "ON" : "OFF");
        int hudW = fontRendererObj.getStringWidth(hudLabel) + 8;
        if (mouseX >= cx && mouseX <= cx + hudW) {
            cfg.setHudVisible(!cfg.isHudVisible());
            return;
        }
        cx += fontRendererObj.getStringWidth(hudLabel) + 12;

        String resetLabel = "Reset";
        int resetW = fontRendererObj.getStringWidth(resetLabel) + 8;
        if (mouseX >= cx && mouseX <= cx + resetW) {
            cfg.resetToDefaults();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (!dragging || button != 0) return;
        PinnedTasksConfig cfg = ForemanClientCache.getPinConfig();
        // offset = absolute position - anchor (keeps offsetX/Y as delta from anchor)
        cfg.setOffsetXRaw(mouseX - dragOffsetX - dragAnchorX);
        cfg.setOffsetYRaw(mouseY - dragOffsetY - dragAnchorY);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            ForemanClientCache.getPinConfig().save();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }
}
