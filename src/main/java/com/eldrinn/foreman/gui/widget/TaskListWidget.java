package com.eldrinn.foreman.gui.widget;

import java.util.Collection;

import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.eldrinn.foreman.cache.ForemanClientCache;
import com.eldrinn.foreman.data.Task;
import com.eldrinn.foreman.data.TaskStatus;
import com.eldrinn.foreman.gui.ForemanGui;
import com.eldrinn.foreman.gui.ForemanGuiData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TaskListWidget extends Flow {

    public TaskListWidget(ForemanGuiData data) {
        super(com.cleanroommc.modularui.api.GuiAxis.Y);
        size(ForemanGui.LEFT_WIDTH, ForemanGui.HEIGHT);
        padding(ForemanGui.PADDING);

        final int P = ForemanGui.PADDING;
        final int W = ForemanGui.LEFT_WIDTH - 2 * P;
        final int H = ForemanGui.HEIGHT - 2 * P;

        // Tabs — each tab takes exactly 1/3 of the available width
        final int TAB_W = W / 3;
        child(
            Flow.row()
                .size(W, 24)
                .child(tabButton("To do", TaskStatus.OPEN, data, TAB_W))
                .child(tabButton("Doing", TaskStatus.IN_PROGRESS, data, TAB_W))
                .child(tabButton("Done", TaskStatus.DONE, data, TAB_W)));

        // Search placeholder (non-functional, reserved for Phase 4)
        TextWidget searchPlaceholder = new TextWidget("Search...");
        searchPlaceholder.size(W, 18);
        child(searchPlaceholder);

        // Task list
        ListWidget<TaskRowWidget, ?> list = new ListWidget<>();
        list.size(W, H - 24 - 18 - 28);
        Collection<Task> all = ForemanClientCache.getAll();
        for (Task task : all) {
            if (task.status == data.activeTab) {
                list.child(new TaskRowWidget(task, data));
            }
        }
        child(list);

        // Bottom bar: New Task + theme toggle
        final int THEME_BTN_W = 28;
        final int NEW_TASK_W = W - THEME_BTN_W - 4;

        TextWidget newTaskLabel = new TextWidget("+ New Task");
        newTaskLabel.size(NEW_TASK_W, 24);
        newTaskLabel.alignment(Alignment.Center);
        newTaskLabel.color(0xFFFFFF);

        TextWidget themeLabel = new TextWidget("☀");
        themeLabel.size(THEME_BTN_W, 24);
        themeLabel.alignment(Alignment.Center);
        themeLabel.color(0xFFFFFF);

        child(
            Flow.row()
                .size(W, 24)
                .child(
                    new ButtonWidget<>().size(NEW_TASK_W, 24)
                        .child(newTaskLabel)
                        .onMousePressed(btn -> {
                            if (btn != 0) return false;
                            data.enterCreateMode();
                            ForemanGui.open(data);
                            return true;
                        }))
                .child(
                    new ButtonWidget<>().size(THEME_BTN_W, 24)
                        .child(themeLabel)
                        .onMousePressed(btn -> {
                            if (btn != 0) return false;
                            ForemanGui.toggleTheme();
                            ForemanGui.open(data);
                            return true;
                        })));
    }

    private static ToggleButton tabButton(String label, TaskStatus status, ForemanGuiData data, int width) {
        TextWidget normalLabel = new TextWidget(label);
        normalLabel.size(width, 22);
        normalLabel.alignment(Alignment.Center);
        normalLabel.color(0xFFFFFF);

        TextWidget activeLabel = new TextWidget(label);
        activeLabel.size(width, 22);
        activeLabel.alignment(Alignment.Center);
        activeLabel.color(0xFFFFFF);

        return new ToggleButton().size(width, 22)
            .value(new BoolValue.Dynamic(() -> data.activeTab == status, selected -> {
                if (selected) {
                    data.activeTab = status;
                    data.clear();
                    ForemanGui.open(data);
                }
            }))
            .child(false, normalLabel)
            .child(true, activeLabel);
    }
}
