package com.eldrinn.foreman.cache;

import java.util.UUID;

public class PlayerEntry {

    public final UUID id;
    public final String name;

    public PlayerEntry(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

}
