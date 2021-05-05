package me.minecraftauth.plugin.common.feature.subscription.provider;

import alexh.weak.Dynamic;
import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;

import java.util.Map;

public class DiscordSubscriptionProvider extends AbstractSubscriptionProvider {

    private final RequireSubscriptionFeature feature;
    private final Dynamic config;

    public DiscordSubscriptionProvider(RequireSubscriptionFeature feature, Dynamic config) {
        this.feature = feature;
        this.config = config;
    }

    public String getRoleId() {
        if (config.is(String.class)) {
            return null;
        } else if (config.is(Map.class)) {
            return config.dget("Discord").convert().intoString();
        } else {
            throw new IllegalArgumentException("Invalid type for Discord provider");
        }
    }

    @Override
    public boolean isSubscribed(MinecraftAccount account) throws LookupException {
        if (getRoleId() == null) return false;
        return AuthService.isSubscribedDiscord(getServerToken(feature, config), account.getUUID(), getRoleId());
    }

}
