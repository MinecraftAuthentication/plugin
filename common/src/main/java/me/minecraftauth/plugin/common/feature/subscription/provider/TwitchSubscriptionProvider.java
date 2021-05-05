package me.minecraftauth.plugin.common.feature.subscription.provider;

import alexh.weak.Dynamic;
import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.account.platform.twitch.SubTier;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;

import java.util.Map;

public class TwitchSubscriptionProvider extends AbstractSubscriptionProvider {

    private final RequireSubscriptionFeature feature;
    private final Dynamic config;

    public TwitchSubscriptionProvider(RequireSubscriptionFeature feature, Dynamic config) {
        this.feature = feature;
        this.config = config;
    }

    public SubTier getTier() {
        if (config.is(String.class)) {
            // no specific tier given
            return SubTier.raw(1); // lowest possible sub tier value
        } else if (config.is(Map.class)) {

            int level = config.dget("Twitch").convert().intoInteger();
            if (level < 999) level *= 1000;
            return SubTier.raw(level);
        } else {
            throw new IllegalArgumentException("Invalid type for Twitch provider");
        }
    }

    @Override
    public boolean isSubscribed(MinecraftAccount account) throws LookupException {
        return AuthService.isSubscribedTwitch(getServerToken(feature, config), account.getUUID(), getTier());
    }

}
