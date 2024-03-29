/*
 * Copyright 2021-2024 MinecraftAuth.me
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

package me.minecraftauth.plugin.common.abstracted.event;

import lombok.Getter;

import java.util.UUID;

public abstract class RealmJoinEvent implements Event {

    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final boolean admin;
    @Getter private final String server;

    public RealmJoinEvent(UUID uuid, String name, boolean admin, String server) {
        this.uuid = uuid;
        this.name = name;
        this.admin = admin;
        this.server = server;
    }

    public abstract void disallow(String message);

}
