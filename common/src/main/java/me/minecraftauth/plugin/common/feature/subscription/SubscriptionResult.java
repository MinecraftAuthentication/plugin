/*-
 * LICENSE
 * MinecraftAuth Plugin - Common
 * -------------
 * Copyright (C) 2021 MinecraftAuth.me
 * -------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * END
 */

package me.minecraftauth.plugin.common.feature.subscription;

import org.jetbrains.annotations.NotNull;

public class SubscriptionResult {

    @NotNull private final Type type;
    @NotNull private final String message;

    public SubscriptionResult(@NotNull Type type) {
        this.type = type;
        this.message = "";
    }
    public SubscriptionResult(@NotNull Type type, @NotNull String message) {
        this.type = type;
        this.message = message;
    }

    public @NotNull Type getType() {
        return type;
    }
    public @NotNull String getMessage() {
        return message;
    }

    public enum Type {

        NOT_ENABLED,
        NOT_SUBSCRIBED(true),
        SUBSCRIBED;

        private final boolean willDenyLogin;

        Type() {
            this.willDenyLogin = false;
        }
        Type(boolean willDenyLogin) {
            this.willDenyLogin = willDenyLogin;
        }

        public boolean willDenyLogin() {
            return willDenyLogin;
        }

    }

}
