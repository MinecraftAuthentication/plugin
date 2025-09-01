package me.minecraftauth.mod.forge.server.mixin;

import com.mojang.authlib.GameProfile;
import me.minecraftauth.game.config.GatekeeperResult;
import me.minecraftauth.mod.forge.server.ModMain;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class LoginMixin {

    @Inject(at = @At("RETURN"), method = "canPlayerLogin", cancellable = true)
    private void init(SocketAddress address, GameProfile prof, CallbackInfoReturnable<Component> component) {
        if (component.getReturnValue() == null) {
            GatekeeperResult rst = ModMain.commonAPI.onJoin(prof.getId());
            if (rst.getType() == GatekeeperResult.Type.DENIED)
                component.setReturnValue(Component.literal(rst.getMessage() == null ? "Unknown MCAuth error" : rst.getMessage()));
        }
    }

}
