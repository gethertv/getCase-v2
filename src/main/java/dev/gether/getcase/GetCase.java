package dev.gether.getcase;

import dev.gether.getcase.bstats.Metrics;
import dev.gether.getcase.cmd.GetCaseCmd;
import dev.gether.getcase.cmd.arguments.CaseArg;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.edit.EditLootBoxManager;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.listener.InventoryClickListener;
import dev.gether.getcase.listener.InventoryCloseListener;
import dev.gether.getcase.listener.PlayerInteractionListener;
import dev.gether.getcase.lootbox.animation.AnimationManager;
import dev.gether.getcase.lootbox.open.OpenCaseManager;
import dev.gether.getcase.manager.*;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public final class GetCase extends JavaPlugin {

    // instance
    private static GetCase instance;


    // manager
    private CaseManager caseManager;
    private LocationCaseManager locationCaseManager;
    private EditLootBoxManager editLootBoxManager;
    private AnimationManager spinCaseManager;
    private LiteCommands<CommandSender> liteCommands;
    private HookManager hookManager;

    // file manager/config
    private final FileManager fileManager = new FileManager(this);

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        // hooks
        hookManager = new HookManager();

        // manager implement
        caseManager = new CaseManager(caseConfig, caseLocationConfig);
        locationCaseManager = new LocationCaseManager(caseLocationConfig, caseManager, hookManager);
        editLootBoxManager = new EditLootBoxManager(caseManager, this);
        OpenCaseManager openCaseManager = new OpenCaseManager(this, caseConfig, langConfig);
        spinCaseManager = new AnimationManager(this,openCaseManager);

        // create hologram for cases
        locationCaseManager.createHolograms();

        // register listener
        Stream.of(
                new InventoryCloseListener(openCaseManager),
                new InventoryClickListener(caseManager, editLootBoxManager, openCaseManager),
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
            liteCommands.getCommandManager().unregisterAll();

        HandlerList.unregisterAll(this);

    }

    public boolean reloadPlugin() {
        // close for all players inventory with case preview
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (LootBox caseDatum : caseManager.getAllCases()) {
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

        // implements cases
        caseManager.implementsAllCases();
        // create hologram for all cases
        locationCaseManager.createHolograms();

        return true;
    }
    private void registerLiteCmd() {
        this.liteCommands =  LiteBukkitFactory.builder("getcase", this)
                .commands(
                        new GetCaseCmd(this, caseManager, locationCaseManager, editLootBoxManager)
                )
                // args
                .argument(LootBox.class, new CaseArg(caseManager))
                .build();

    }

    public static GetCase getInstance() {
        return instance;
    }

    public AnimationManager getSpinCaseManager() {
        return spinCaseManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public CaseConfig getCaseConfig() {
        return caseConfig;
    }
}
