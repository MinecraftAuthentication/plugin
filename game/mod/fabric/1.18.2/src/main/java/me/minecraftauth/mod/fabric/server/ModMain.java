package me.minecraftauth.mod.fabric.server;

import me.minecraftauth.game.Common;
import me.minecraftauth.game.config.GatekeeperResult;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.UUID;

public class ModMain implements ModInitializer {
    public static final String MOD_ID = "mcauth";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static Common.CommonAPI commonAPI = null;

    @Override
    public void onInitialize() {
        try {
            commonAPI = new Common().init(new File("config").toPath());
        } catch (IllegalStateException e) {
            logger.error("Application not configured. See config.yml. Thanks!");
            return;
        }

        ServerPlayConnectionEvents.INIT.register(Event.DEFAULT_PHASE, (nh, srv) -> {
            GatekeeperResult rst = commonAPI.onJoin(nh.player.getUuid());
            if (rst.getType() == GatekeeperResult.Type.DENIED)
                nh.disconnect(Text.of(rst.getMessage() == null ? "Unknown MCAuth error" : rst.getMessage()));
        });
    }
}