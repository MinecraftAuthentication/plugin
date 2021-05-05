package me.minecraftauth.plugin.common.abstracted.event;

import java.util.UUID;

public abstract class PlayerLoginEvent implements Event {

    private final UUID uuid;
    private final String name;

    public PlayerLoginEvent(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public abstract void disallow(String message);

    public UUID getUuid() {
        return uuid;
    }
    public String getName() {
        return name;
    }

}
