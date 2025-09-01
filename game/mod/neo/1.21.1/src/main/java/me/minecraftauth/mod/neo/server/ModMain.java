package me.minecraftauth.mod.neo.server;

import com.mojang.logging.LogUtils;
import me.minecraftauth.game.Common;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.io.File;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModMain.MOD_ID)
public class ModMain
{
    public static final String MOD_ID = "mcauth";
    private static final Logger logger = LogUtils.getLogger();
    public static Common.CommonAPI commonAPI = null;
    public static ModMain instance;

    public ModMain(IEventBus modEventBus, ModContainer modContainer) throws Exception {
        instance = this;
        try {
            commonAPI = new Common().init(new File("config").toPath());
        } catch (IllegalStateException e) {
            logger.warn("Application not configured. See config.yml. Thanks!");
            throw new Exception("Application not configured. See config.yml. Thanks!");
        }
    }

}
