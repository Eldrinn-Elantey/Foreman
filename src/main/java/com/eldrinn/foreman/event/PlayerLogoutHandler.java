package com.eldrinn.foreman.event;

import net.minecraft.entity.player.EntityPlayerMP;

import com.eldrinn.foreman.storage.ForemanWorldData;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerLogoutHandler {

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) return;
        ForemanWorldData.get()
            .setPlayerLastSeen(player.getUniqueID(), System.currentTimeMillis());
    }
}
