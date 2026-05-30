package com.eldrinn.foreman.integration;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.context.IQuestContextMenuEntry;
import betterquesting.api2.client.gui.context.QuestContextMenuRegistry;
import betterquesting.api2.storage.DBEntry;
import bq_standard.tasks.TaskRetrieval;

import com.eldrinn.foreman.data.Subtask;
import com.eldrinn.foreman.data.Task;
import com.eldrinn.foreman.data.TaskStatus;
import com.eldrinn.foreman.network.CreateTaskPacket;
import com.eldrinn.foreman.network.ForemanNetwork;

/**
 * Registers Foreman's entry in BetterQuesting's quest context menu.
 * Only loaded when BetterQuesting is present (guarded by Loader.isModLoaded in ClientProxy).
 */
public final class BetterQuestingIntegration {

    private BetterQuestingIntegration() {}

    public static void register() {
        QuestContextMenuRegistry.register(new IQuestContextMenuEntry() {

            @Override
            public String getLabel(UUID questId, IQuest quest) {
                return "Add to Foreman";
            }

            @Override
            public Runnable getAction(UUID questId, IQuest quest) {
                return () -> createTaskFromQuest(quest);
            }
        });
    }

    private static void createTaskFromQuest(IQuest quest) {
        String title = quest.getProperty(NativeProps.NAME);
        Task task = new Task(UUID.randomUUID(), title, "", TaskStatus.OPEN);

        // Map quest icon to Foreman's "modid:itemname:meta" format
        BigItemStack iconStack = quest.getProperty(NativeProps.ICON);
        if (iconStack != null) {
            ItemStack base = iconStack.getBaseStack();
            if (base != null && base.getItem() != null) {
                String itemName = Item.itemRegistry.getNameForObject(base.getItem());
                if (itemName != null) {
                    task.iconItem = itemName + ":" + base.getItemDamage();
                }
            }
        }

        // Map TaskRetrieval required items to subtasks
        for (DBEntry<ITask> entry : quest.getTasks().getEntries()) {
            if (entry.getValue() instanceof TaskRetrieval) {
                TaskRetrieval retrieval = (TaskRetrieval) entry.getValue();
                for (BigItemStack required : retrieval.requiredItems) {
                    String itemName = required.getBaseStack().getDisplayName();
                    task.subtasks.add(new Subtask(UUID.randomUUID(), required.stackSize + "x " + itemName, false));
                }
            }
        }

        ForemanNetwork.CHANNEL.sendToServer(new CreateTaskPacket(task));

        Minecraft.getMinecraft().thePlayer.addChatMessage(
            new ChatComponentText("§aForeman: task \"" + title + "\" created."));
    }
}
