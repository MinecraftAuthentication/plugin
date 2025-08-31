package me.minecraftauth.mod.neo.server;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModMain.MOD_ID)
public class ModMain
{
    public static final String MOD_ID = "mcauth";
    private static final Logger logger = LogUtils.getLogger();
    private static ModMain instance;

    public ModMain(IEventBus modEventBus, ModContainer modContainer)
    {
        instance = this;
        logger.info("Hello NeoForge world!");
    }

}
