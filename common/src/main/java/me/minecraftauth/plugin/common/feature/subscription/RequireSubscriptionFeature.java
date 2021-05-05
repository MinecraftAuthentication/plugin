package me.minecraftauth.plugin.common.feature.subscription;

import alexh.weak.Dynamic;
import lombok.Getter;
import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.Feature;
import me.minecraftauth.plugin.common.feature.subscription.provider.*;
import me.minecraftauth.plugin.common.service.GameService;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RequireSubscriptionFeature extends Feature {

    @Getter private final GameService service;
    @Getter private final Set<MembershipProvider> providers = new HashSet<>();
    @Getter private String kickMessage = null;

    public RequireSubscriptionFeature(GameService service) {
        this.service = service;
        reload();
    }

    public @NotNull SubscriptionResult verifySubscription(MinecraftAccount account) throws LookupException {
        if (service.getServerToken() == null || providers.size() == 0) return new SubscriptionResult(SubscriptionResult.Type.NOT_ENABLED);
        boolean subscribed = false;
        for (MembershipProvider provider : providers) {
            if (provider.isSubscribed(account)) {
                subscribed = true;
                break;
            }
        }
        return subscribed ? new SubscriptionResult(SubscriptionResult.Type.SUBSCRIBED) : new SubscriptionResult(SubscriptionResult.Type.NOT_SUBSCRIBED, kickMessage);
    }

    @Override
    public void reload() {
        Dynamic kickMessageDynamic = service.getConfig().dget("Subscription.Kick message");
        kickMessage = kickMessageDynamic.isPresent() ? kickMessageDynamic.convert().intoString() : null;

        Dynamic providersDynamic = service.getConfig().dget("Subscription.Providers");
        providersDynamic.children().forEach(providerDynamic -> {
            MembershipProvider provider;

            if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("discord"))
                    || providerDynamic.dget("Discord").isPresent()) {
                provider = new DiscordSubscriptionProvider(this, providerDynamic);
            } else if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("patreon"))
                    || providerDynamic.dget("Patreon").isPresent()) {
                provider = new PatreonSubscriptionProvider(this, providerDynamic);
            } else if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("twitch"))
                    || providerDynamic.dget("Twitch").isPresent()) {
                if (providerDynamic.isMap() && providerDynamic.dget("Twitch").convert().intoString().toLowerCase().startsWith("follow")) {
                    provider = new TwitchFollowerProvider(this, providerDynamic);
                } else {
                    provider = new TwitchSubscriptionProvider(this, providerDynamic);
                }
            } else if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("youtube"))
                    || providerDynamic.dget("YouTube").isPresent()) {
                if (providerDynamic.isMap() && providerDynamic.dget("YouTube").asString().toLowerCase().startsWith("sub")) {
                    provider = new YouTubeSubscriberProvider(this, providerDynamic);
                } else {
                    provider = new YouTubeMemberProvider(this, providerDynamic);
                }
            } else {
                throw new IllegalArgumentException("Unknown provider config " + providerDynamic.key().convert().intoString());
            }

            providers.add(provider);
        });
    }

}
