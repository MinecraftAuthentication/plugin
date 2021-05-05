package me.minecraftauth.plugin.bukkit;

import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class BukkitEventsListener implements Listener {

    @EventHandler
    public void onPlayerLoginEvent(AsyncPlayerPreLoginEvent event) {
        try {
            MinecraftAuthBukkit.getInstance().getService().handleLoginEvent(new PlayerLoginEvent(event.getUniqueId(), event.getName()) {
                @Override
                public void disallow(String message) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
                }
            });
        } catch (LookupException e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Unable to verify linked account");
            e.printStackTrace();
        }
    }

}
