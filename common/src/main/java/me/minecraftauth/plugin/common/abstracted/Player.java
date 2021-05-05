package me.minecraftauth.plugin.common.abstracted;

import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.account.Account;
import me.minecraftauth.lib.account.AccountType;
import me.minecraftauth.lib.exception.LookupException;

import java.util.UUID;

public abstract class Player {

    public abstract String getName();
    public abstract UUID getUUID();

    public Account getLinkedAccount(AccountType type) throws LookupException {
        switch (type) {
            case DISCORD:
            case PATREON:
            case TWITCH:
            case GOOGLE:
                AuthService.lookup(AccountType.MINECRAFT, getUUID(), type);
            default:
                throw new IllegalArgumentException("Invalid account type to lookup: " + type.name().toLowerCase());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) && getUUID().equals(((Player) obj).getUUID());
    }

}
