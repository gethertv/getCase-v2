package dev.gether.getcase.hook;

import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class HookManager {

    boolean decentHologramsEnabled = false;
    boolean placeholderEnabled = false;

    public HookManager() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        decentHologramsEnabled = pluginManager.getPlugin("DecentHolograms") != null;
        placeholderEnabled = pluginManager.getPlugin("PlaceholderAPI") != null;

        if(decentHologramsEnabled)
            MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] ✔ DecentHolograms hook enabled");

        if(placeholderEnabled)
            MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] ✔ PlaceholderAPI hook enabled");

    }

}
