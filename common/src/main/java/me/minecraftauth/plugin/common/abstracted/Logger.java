package me.minecraftauth.plugin.common.abstracted;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface Logger {

    void info(String message);

    void warning(String message);

    void error(String message);
    default void error(String message, Throwable throwable) {
        error(message);

        StringWriter writer = new StringWriter();
        try (PrintWriter print = new PrintWriter(writer)) {
            throwable.printStackTrace(print);
        }
        error(writer.toString());
    }

    void debug(String message);

}
