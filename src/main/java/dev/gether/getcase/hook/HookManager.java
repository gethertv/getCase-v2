package dev.gether.getcase.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class HookManager {

    private boolean decentHologramsEnable = false;

    public HookManager() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        // check plugin exists on server
        if(pluginManager.getPlugin("DecentHolograms") != null) {
            decentHologramsEnable = true;
        }
    }

    public boolean isDecentHologramsEnable() {
        return decentHologramsEnable;
    }
}
