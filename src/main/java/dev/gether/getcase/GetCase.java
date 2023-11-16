package dev.gether.getcase;

import dev.gether.getcase.bstats.Metrics;
import dev.gether.getcase.cmd.GetCaseCmd;
import dev.gether.getcase.cmd.arguments.CaseArg;
import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.config.CaseLocationConfig;
import dev.gether.getcase.config.LangConfig;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.listener.InventoryClickListener;
import dev.gether.getcase.listener.InventoryCloseListener;
import dev.gether.getcase.listener.PlayerInteractionListener;
import dev.gether.getcase.manager.*;
import dev.gether.getcase.serializer.CraftItemStackSerializer;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Stream;

public final class GetCase extends JavaPlugin {

    // instance
    private static GetCase instance;

    // configuration/files
    private CaseConfig caseConfig;
    private CaseLocationConfig caseLocationConfig;
    private LangConfig langConfig;
    // manager
    private CaseManager caseManager;
    private LocationCaseManager locationCaseManager;
    private AdminEditManager adminEditManager;
    private SpinCaseManager spinCaseManager;
    private LiteCommands<CommandSender> liteCommands;
    // lite commands

    public void loadConfig() {

        // custom item serializer
        SerdesRegistry serdesRegistry = new SerdesRegistry();
        serdesRegistry.register(new CraftItemStackSerializer());

        // register serializer
        OkaeriSerdesPack okaeriSerdesPack = new SerdesBukkit();
        okaeriSerdesPack.register(serdesRegistry);

        caseConfig = ConfigManager.create(CaseConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), okaeriSerdesPack);
            it.withBindFile(new File(getDataFolder(), "case.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        caseLocationConfig = ConfigManager.create(CaseLocationConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(getDataFolder(), "location.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        langConfig = ConfigManager.create(LangConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(getDataFolder(), "lang.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

    }
    @Override
    public void onEnable() {

        instance = this;
        // implements config/files
        loadConfig();
        // hooks
        HookManager hookManager = new HookManager();

        // manager implement
        caseManager = new CaseManager(caseConfig, hookManager);
        locationCaseManager = new LocationCaseManager(caseLocationConfig, caseManager);
        adminEditManager = new AdminEditManager(caseManager, this);
        OpenCaseManager openCaseManager = new OpenCaseManager(this, caseConfig, langConfig);
        spinCaseManager = new SpinCaseManager(this,openCaseManager);

        // inject inventory all hologram for all cases
        caseManager.createAllInv();
        locationCaseManager.createHolograms();


        // register listener
        Stream.of(
                new InventoryCloseListener(openCaseManager),
                new InventoryClickListener(caseManager, adminEditManager, openCaseManager),
                new PlayerInteractionListener(locationCaseManager, caseManager, caseConfig)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        // register cmd
        registerLiteCmd();

        // register bstats
        new Metrics(this, 20299);

    }

    @Override
    public void onDisable() {

        caseManager.deleteAllHolograms();

        if(liteCommands != null)
            liteCommands.getPlatform().unregisterAll();

        HandlerList.unregisterAll(this);

    }

    public boolean reloadPlugin() {
        // close for all players inventory with case preview
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (CaseObject caseDatum : caseConfig.getCaseData()) {
                // if player open inventory is same, then close the inventory
                if(player.getOpenInventory().getTopInventory().equals(caseDatum.getInventory())) {
                    player.closeInventory();
                    return;
                }
            }
        });
        // delete hologram
        caseManager.deleteAllHolograms();
        // load new config
        caseLocationConfig.load();
        caseConfig.load();

        // create inv with new items for all cases
        caseManager.createAllInv();
        // create hologram for all cases
        locationCaseManager.createHolograms();

        return true;
    }
    private void registerLiteCmd() {
        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "getcase")
                .commandInstance(
                        new GetCaseCmd(this, caseManager, locationCaseManager, adminEditManager)
                )
                // contextual bind
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("&cPodany gracz nie jest online!"))

                // args
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), "&cPodany gracz nie jest online!"))
                .argument(CaseObject.class, new CaseArg(caseManager))
                .register();

    }

    public static GetCase getInstance() {
        return instance;
    }

    public SpinCaseManager getSpinCaseManager() {
        return spinCaseManager;
    }

    public CaseConfig getCaseConfig() {
        return caseConfig;
    }
}
