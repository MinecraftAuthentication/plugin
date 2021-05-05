package me.minecraftauth.plugin.common.feature.subscription.provider;

import me.minecraftauth.lib.account.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;

public interface MembershipProvider {

    boolean isSubscribed(MinecraftAccount account) throws LookupException;

}
