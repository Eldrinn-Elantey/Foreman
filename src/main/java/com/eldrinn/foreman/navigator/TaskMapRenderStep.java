package com.eldrinn.foreman.navigator;

import java.util.List;

import com.eldrinn.foreman.data.TaskStatus;
import com.gtnewhorizons.navigator.api.model.steps.UniversalInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TaskMapRenderStep extends UniversalInteractableStep<TaskMapLocation> {

    public TaskMapRenderStep(TaskMapLocation location) {
        super(location);
        width = 16;
        height = 16;
    }

    @Override
    public void draw(double topX, double topY, float drawScale, double zoom) {
        int bgColor = bgColor(location.getStatus());
        DrawUtils.drawRect(topX, topY, getAdjustedWidth(), getAdjustedHeight(), bgColor, 200);
        DrawUtils.drawHollowRect(topX, topY, getAdjustedWidth(), getAdjustedHeight(), 0xFFFFFF, 180);
        String letter = statusLetter(location.getStatus());
        DrawUtils.drawLabel(
            letter,
            topX + getAdjustedWidth() / 2,
            topY + getAdjustedHeight() / 2,
            0xFFFFFFFF,
            0x88000000,
            false,
            fontScale);
    }

    @Override
    public void getTooltip(List<String> list) {
        list.add(location.getTitle());
        list.add(statusLabel(location.getStatus()));
    }

    private static int bgColor(TaskStatus status) {
        return switch (status) {
            case OPEN -> 0x888800;
            case IN_PROGRESS -> 0x004488;
            case DONE -> 0x228822;
        };
    }

    private static String statusLetter(TaskStatus status) {
        return switch (status) {
            case OPEN -> "O";
            case IN_PROGRESS -> "~";
            case DONE -> "V";
        };
    }

    private static String statusLabel(TaskStatus status) {
        return switch (status) {
            case OPEN -> "Open";
            case IN_PROGRESS -> "In Progress";
            case DONE -> "Done";
        };
    }
}
