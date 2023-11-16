package dev.gether.getcase.utils;

import dev.rollczi.litecommands.platform.LiteSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class MessageUtil {

    private MessageUtil() {
        throw new IllegalStateException("Utility class");
    }
    // logger
    private static final Logger LOG = Bukkit.getLogger();

    public static void logMessage(String consoleColor, String message) {
        LOG.info(consoleColor+message);
    }
    public static void sendMessage(Player player, String message) {
        player.sendMessage(ColorFixer.addColors(message));
    }

    public static void sendMessage(LiteSender sender, String message) {
        sender.sendMessage(ColorFixer.addColors(message));
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(ColorFixer.addColors(message));
    }
}
