package dev.gether.getcase;

import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.manager.CaseManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class GetCasev extends JavaPlugin {

    // configuration/files
    private CaseConfig caseConfig;
    // manager
    private CaseManager caseManager;
    // logger
    public static final Logger LOG = Bukkit.getLogger();
    public void loadConfig() {

        caseConfig = ConfigManager.create(CaseConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(getDataFolder(), "config.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

    }
    @Override
    public void onEnable() {

        // implements config/files
        loadConfig();

        // manager implement
        caseManager = new CaseManager(caseConfig);

    }

    @Override
    public void onDisable() {

        HandlerList.unregisterAll(this);

    }
}
