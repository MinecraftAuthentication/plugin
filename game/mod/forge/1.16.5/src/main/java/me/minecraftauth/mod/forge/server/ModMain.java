package me.minecraftauth.mod.forge.server;

import me.minecraftauth.game.Common;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(ModMain.MOD_ID)
public class ModMain {

    public static final String MOD_ID = "mcauth";
    private static final Logger logger = LogManager.getLogger();
    public static Common.CommonAPI commonAPI = null;
    private static ModMain instance;

    public ModMain() throws Exception {
        instance = this;
        try {
            commonAPI = new Common().init(new File("config").toPath());
        } catch (IllegalStateException e) {
            logger.warn("Application not configured. See config.yml. Thanks!");
            throw new Exception("Application not configured. See config.yml. Thanks!");
        }
    }

}
