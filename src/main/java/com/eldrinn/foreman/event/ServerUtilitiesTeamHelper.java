package com.eldrinn.foreman.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import com.eldrinn.foreman.cache.PlayerEntry;

import serverutils.lib.data.ForgePlayer;
import serverutils.lib.data.ForgeTeam;
import serverutils.lib.data.Universe;

public class ServerUtilitiesTeamHelper {

    public static List<PlayerEntry> getTeamMembers(EntityPlayerMP player) {
        List<PlayerEntry> result = new ArrayList<>();
        if (!Universe.loaded()) return result;
        ForgePlayer fp = Universe.get().getPlayer(player.getGameProfile());
        if (fp == null) return result;
        ForgeTeam team = fp.team;
        if (team == null) return result;
        for (ForgePlayer member : team.getMembers()) {
            result.add(new PlayerEntry(member.getId(), member.getName()));
        }
        return result;
    }
}
