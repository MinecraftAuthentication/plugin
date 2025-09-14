package me.minecraftauth.mod.forge.server;

import me.minecraftauth.game.Common;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod("mcauth")
public class ModMain {

    private static final Logger logger = LogManager.getLogger();
    public static Common.CommonAPI commonAPI = null;
    private static ModMain instance;

    public ModMain() throws Exception {
        instance = this;
        try {
            commonAPI = new Common().init(new File("config").toPath());
        } catch (IllegalStateException e) {
            logger.warn("Application not configured. See config.yml. Thanks! *(" + e.getMessage() + ")");
            throw new Exception("Application not configured. See config.yml. Thanks!");
        }
    }

}
