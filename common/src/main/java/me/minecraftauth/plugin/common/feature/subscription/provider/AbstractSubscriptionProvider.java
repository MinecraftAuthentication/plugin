package me.minecraftauth.plugin.common.feature.subscription.provider;

import alexh.weak.Dynamic;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;

public abstract class AbstractSubscriptionProvider implements MembershipProvider {

    public String getServerToken(RequireSubscriptionFeature feature, Dynamic providerConfig) {
        Dynamic authenticationDynamic = providerConfig.dget("Authentication");
        return authenticationDynamic.isPresent() ? authenticationDynamic.convert().intoString() : feature.getService().getServerToken();
    }

}
