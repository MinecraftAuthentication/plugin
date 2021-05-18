/*
 * Copyright 2021 MinecraftAuth.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
