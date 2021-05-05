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
