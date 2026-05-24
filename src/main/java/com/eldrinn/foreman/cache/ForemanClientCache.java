package com.eldrinn.foreman.cache;

import com.eldrinn.foreman.data.Task;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Client-side in-memory task store. Replaced wholesale on each SyncAllTasksPacket.
 * Only safe to access from the client thread.
 */
@SideOnly(Side.CLIENT)
public class ForemanClientCache {

    private static Map<UUID, Task> tasks = new LinkedHashMap<>();

    public static void update(Collection<Task> incoming) {
        tasks.clear();
        for (Task t : incoming) {
            tasks.put(t.id, t);
        }
    }

    public static Collection<Task> getAll() {
        return Collections.unmodifiableCollection(tasks.values());
    }

    @Nullable
    public static Task get(UUID id) {
        return tasks.get(id);
    }
}
