/*
 * Copyright 2021-2022 MinecraftAuth.me
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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class Command {

    public Command(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("minecraftauth")
                .then(Commands.literal("reload")
                        .requires(cs -> cs.hasPermission(3))
                        .executes(context -> reload(context, context.getSource()))
                )
        );
    }

    private int reload(CommandContext<CommandSource> context, CommandSource source) {
        try {
            MinecraftAuthMod.getInstance().getService().fullReload();
            source.sendSuccess(new StringTextComponent("MinecraftAuth config reloaded").withStyle(TextFormatting.RED), true);
            return 1;
        } catch (IOException e) {
            source.sendFailure(new StringTextComponent("IO exception while reading config: " + e.getMessage()).withStyle(TextFormatting.RED));
            e.printStackTrace();
        } catch (ParseException e) {
            source.sendFailure(new StringTextComponent("Exception while parsing config: " + e.getMessage()).withStyle(TextFormatting.RED));
            e.printStackTrace();
        }

        return -1;
    }

}
