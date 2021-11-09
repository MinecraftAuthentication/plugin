package me.minecraftauth.forge.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import github.scarsz.configuralize.ParseException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.io.IOException;

public class Command {

    public Command(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("minecraftauth").then(Commands.literal("reload").executes(this::reload)));
    }

    private int reload(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Entity entity = source.getEntity();

        if (entity == null || (entity instanceof PlayerEntity && ServerLifecycleHooks.getCurrentServer().getPlayerList().isOp(((PlayerEntity) entity).getGameProfile()))) {
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
        } else {
            source.sendFailure(new StringTextComponent("Server operator-only command").withStyle(TextFormatting.RED));
        }

        return -1;
    }

}
