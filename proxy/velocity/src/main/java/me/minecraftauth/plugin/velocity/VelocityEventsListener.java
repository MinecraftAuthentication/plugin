package me.minecraftauth.plugin.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VelocityEventsListener {

    @Subscribe(order = PostOrder.FIRST, async = false)
    public void onLogin(LoginEvent event) {
        try {
            MinecraftAuthVelocity.getInstance().getService().handleLoginEvent(new PlayerLoginEvent(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getUsername(),
                    event.getPlayer().hasPermission("minecraftauth.admin")
            ) {
                @Override
                public void disallow(String message) {
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text(message).color(NamedTextColor.RED)));
                }
            });
        } catch (LookupException e) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Unable to verify linked account").color(NamedTextColor.RED)));
            e.printStackTrace();
        }
    }

}
