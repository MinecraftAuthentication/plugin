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

package me.minecraftauth.forge.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import github.scarsz.configuralize.ParseException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class Command {

    public Command(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("minecraftauth")
                .then(Commands.literal("reload")
                        .requires(cs -> cs.hasPermission(3))
                        .executes(context -> reload(context, context.getSource()))
                )
        );
    }

    private int reload(CommandContext<CommandSourceStack> context, CommandSourceStack source) {
        try {
            MinecraftAuthMod.getInstance().getService().fullReload();
            source.sendSuccess(Component.literal("MinecraftAuth config reloaded").withStyle(ChatFormatting.RED), true);
            return 1;
        } catch (IOException e) {
            source.sendFailure(Component.literal("IO exception while reading config: " + e.getMessage()).withStyle(ChatFormatting.RED));
            e.printStackTrace();
        } catch (ParseException e) {
            source.sendFailure(Component.literal("Exception while parsing config: " + e.getMessage()).withStyle(ChatFormatting.RED));
            e.printStackTrace();
        }
        return -1;
    }

}
