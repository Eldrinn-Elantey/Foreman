package com.eldrinn.foreman.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Keyboard;

import com.eldrinn.foreman.cache.ForemanClientCache;
import com.eldrinn.foreman.config.PinnedTasksConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudSettingsScreen extends GuiScreen {

    private static final int HANDLE_SIZE = 10;
    private static final int PANEL_H = 24;
    private static final int PANEL_PADDING = 6;

    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

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
        fontRenderer.drawStringWithShadow(hint, (sw - fontRenderer.getStringWidth(hint)) / 2, 6, 0xAAAAAA);

        int hx = cfg.getOffsetX();
        int hy = cfg.getOffsetY();
        drawRect(hx, hy, hx + HANDLE_SIZE, hy + HANDLE_SIZE, 0xFFCC3333);
        fontRenderer.drawStringWithShadow("⇔", hx + 1, hy + 1, 0xFFFFFF);

        drawControlPanel(cfg, sw, sh);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawControlPanel(PinnedTasksConfig cfg, int sw, int sh) {
        int panelW = 300;
        int px = (sw - panelW) / 2;
        int py = sh - PANEL_H - PANEL_PADDING;

        drawRect(px - 4, py - 4, px + panelW + 4, py + PANEL_H + 4, 0xCC000000);

        int cx = px;

        fontRenderer.drawStringWithShadow("Scale:", cx, py + 7, 0xAAAAAA);
        cx += fontRenderer.getStringWidth("Scale:") + 4;

        drawRect(cx, py + 2, cx + 14, py + 22, 0xFF444444);
        fontRenderer.drawStringWithShadow("-", cx + 4, py + 7, 0xFFFFFF);
        cx += 16;

        String scaleLabel = String.format("%.2fx", cfg.getScale());
        fontRenderer.drawStringWithShadow(scaleLabel, cx, py + 7, 0xFFFFFF);
        cx += fontRenderer.getStringWidth(scaleLabel) + 4;

        drawRect(cx, py + 2, cx + 14, py + 22, 0xFF444444);
        fontRenderer.drawStringWithShadow("+", cx + 3, py + 7, 0xFFFFFF);
        cx += 20;

        String bgLabel = "BG: " + (cfg.isShowBackground() ? "ON" : "OFF");
        int bgColor = cfg.isShowBackground() ? 0xFF8BC34A : 0xFFAAAAAA;
        drawRect(cx, py + 2, cx + fontRenderer.getStringWidth(bgLabel) + 8, py + 22, 0xFF444444);
        fontRenderer.drawStringWithShadow(bgLabel, cx + 4, py + 7, bgColor);
        cx += fontRenderer.getStringWidth(bgLabel) + 12;

        String hudLabel = "HUD: " + (cfg.isHudVisible() ? "ON" : "OFF");
        int hudColor = cfg.isHudVisible() ? 0xFF8BC34A : 0xFFAAAAAA;
        drawRect(cx, py + 2, cx + fontRenderer.getStringWidth(hudLabel) + 8, py + 22, 0xFF444444);
        fontRenderer.drawStringWithShadow(hudLabel, cx + 4, py + 7, hudColor);
        cx += fontRenderer.getStringWidth(hudLabel) + 12;

        String resetLabel = "Reset";
        drawRect(cx, py + 2, cx + fontRenderer.getStringWidth(resetLabel) + 8, py + 22, 0xFF884444);
        fontRenderer.drawStringWithShadow(resetLabel, cx + 4, py + 7, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) return;
        PinnedTasksConfig cfg = ForemanClientCache.getPinConfig();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int sw = res.getScaledWidth();
        int sh = res.getScaledHeight();

        int hx = cfg.getOffsetX();
        int hy = cfg.getOffsetY();

        if (mouseX >= hx && mouseX <= hx + HANDLE_SIZE && mouseY >= hy && mouseY <= hy + HANDLE_SIZE) {
            dragging = true;
            dragOffsetX = mouseX - hx;
            dragOffsetY = mouseY - hy;
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

        cx += fontRenderer.getStringWidth("Scale:") + 4;

        if (mouseX >= cx && mouseX <= cx + 14) {
            cfg.setScale(cfg.getScale() - 0.25);
            return;
        }
        cx += 16;

        String scaleLabel = String.format("%.2fx", cfg.getScale());
        cx += fontRenderer.getStringWidth(scaleLabel) + 4;

        if (mouseX >= cx && mouseX <= cx + 14) {
            cfg.setScale(cfg.getScale() + 0.25);
            return;
        }
        cx += 20;

        String bgLabel = "BG: " + (cfg.isShowBackground() ? "ON" : "OFF");
        int bgW = fontRenderer.getStringWidth(bgLabel) + 8;
        if (mouseX >= cx && mouseX <= cx + bgW) {
            cfg.setShowBackground(!cfg.isShowBackground());
            return;
        }
        cx += bgW + 12;

        String hudLabel = "HUD: " + (cfg.isHudVisible() ? "ON" : "OFF");
        int hudW = fontRenderer.getStringWidth(hudLabel) + 8;
        if (mouseX >= cx && mouseX <= cx + hudW) {
            cfg.setHudVisible(!cfg.isHudVisible());
            return;
        }
        cx += hudW + 12;

        String resetLabel = "Reset";
        int resetW = fontRenderer.getStringWidth(resetLabel) + 8;
        if (mouseX >= cx && mouseX <= cx + resetW) {
            cfg.resetToDefaults();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (!dragging || button != 0) return;
        PinnedTasksConfig cfg = ForemanClientCache.getPinConfig();
        cfg.setOffsetXRaw(mouseX - dragOffsetX);
        cfg.setOffsetYRaw(mouseY - dragOffsetY);
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
