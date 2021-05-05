package me.minecraftauth.plugin.common.service;

import alexh.weak.Dynamic;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;
import me.minecraftauth.plugin.common.feature.subscription.SubscriptionResult;

public class GameService {

    @Getter private final DynamicConfig config;
    @Getter private final RequireSubscriptionFeature subscriptionFeature;
    @Getter private String serverToken;

    private GameService(DynamicConfig config) {
        this.config = config;
        this.subscriptionFeature = new RequireSubscriptionFeature(this);
        reload();
    }

    public void reload() {
        Dynamic authenticationDynamic = config.dget("Authentication");
        serverToken = authenticationDynamic.isPresent() ? authenticationDynamic.convert().intoString() : null;
    }

    public void fullReload() {
        reload();
        subscriptionFeature.reload();
    }

    public void handleLoginEvent(PlayerLoginEvent event) throws LookupException {
        SubscriptionResult subscriptionResult = subscriptionFeature.verifySubscription(new MinecraftAccount(event.getUuid()));
        if (subscriptionResult.getType().willDenyLogin()) {
            event.disallow(subscriptionResult.getMessage());
        }
    }

    public static class Builder {

        private DynamicConfig config;

        public Builder withConfig(DynamicConfig config) {
            this.config = config;
            return this;
        }

        public GameService build() throws ParseException {
            return new GameService(config);
        }

    }

}
