package com.eldrinn.foreman.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import com.eldrinn.foreman.cache.PlayerEntry;
import com.eldrinn.foreman.network.ForemanNetwork;
import com.eldrinn.foreman.network.SyncAllTasksPacket;
import com.eldrinn.foreman.network.SyncTeamMembersPacket;
import com.eldrinn.foreman.storage.ForemanWorldData;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerLoginHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ForemanWorldData data = ForemanWorldData.get();
        ForemanNetwork.CHANNEL.sendTo(new SyncAllTasksPacket(data.getAllTasks()), player);
        ForemanNetwork.CHANNEL.sendTo(new SyncTeamMembersPacket(resolveTeamMembers(player)), player);
    }

    private List<PlayerEntry> resolveTeamMembers(EntityPlayerMP player) {
        if (!Loader.isModLoaded("serverutilities")) return new ArrayList<>();
        return ServerUtilitiesTeamHelper.getTeamMembers(player);
    }
}
