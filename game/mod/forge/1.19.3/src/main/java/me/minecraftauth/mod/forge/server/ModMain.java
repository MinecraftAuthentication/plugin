package me.minecraftauth.mod.forge.server;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ModMain.MOD_ID)
public class ModMain {

    public static final String MOD_ID = "mcauth";
    private static final Logger logger = LogManager.getLogger();
    private static ModMain instance;

    public ModMain() {
        instance = this;
        logger.info("Hello Forge world!");
    }

}
