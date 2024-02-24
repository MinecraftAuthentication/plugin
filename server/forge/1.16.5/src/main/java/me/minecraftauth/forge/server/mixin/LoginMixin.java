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

package me.minecraftauth.forge.server.mixin;

import com.mojang.authlib.GameProfile;
import me.minecraftauth.forge.server.MinecraftAuthMod;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.RealmJoinEvent;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public abstract class LoginMixin {

    @Inject(at = @At("RETURN"), method = "canPlayerLogin", cancellable = true)
    private void init(SocketAddress address, GameProfile profile, CallbackInfoReturnable<ITextComponent> returnedMessage) {
        if (returnedMessage.getReturnValue() == null) {
//            System.out.println("Player " + profile.getName() + "[" + profile.getId() + "] is logging in @ " + address);

            try {
                MinecraftAuthMod.getInstance().getService().handleRealmJoinEvent(new RealmJoinEvent(
                        profile.getId(),
                        profile.getName(),
                        ServerLifecycleHooks.getCurrentServer().getPlayerList().isOp(profile),
                        null
                ) {
                    @Override
                    public void disallow(String message) {
                        returnedMessage.setReturnValue(errorComponent(message));
                    }
                });
            } catch (LookupException e) {
                returnedMessage.setReturnValue(errorComponent("Unable to verify linked account"));
                e.printStackTrace();
            }
        }
    }

    private IFormattableTextComponent errorComponent(String message) {
        return new StringTextComponent(message).withStyle(TextFormatting.RED);
    }

}
