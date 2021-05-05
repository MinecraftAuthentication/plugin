package me.minecraftauth.plugin.sponge;

import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SpongeEventsListener {

    @Listener(order = Order.FIRST)
    public void onClientConnectionLogin(ClientConnectionEvent.Login event) {
        try {
            MinecraftAuthSponge.getInstance().getService().handleLoginEvent(new PlayerLoginEvent(
                    event.getProfile().getUniqueId(),
                    event.getProfile().getName().orElse("")
            ) {
                @Override
                public void disallow(String message) {
                    event.setCancelled(true);
                    event.setMessage(Text.builder(message).color(TextColors.RED));
                }
            });
        } catch (LookupException e) {
            event.setCancelled(true);
            event.setMessage(Text.builder("Unable to verify linked account").color(TextColors.RED));
            e.printStackTrace();
        }
    }

}
