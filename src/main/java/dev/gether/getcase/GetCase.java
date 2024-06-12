package dev.gether.getcase;

import dev.gether.getcase.bstats.Metrics;
import dev.gether.getcase.cmd.GetCaseCmd;
import dev.gether.getcase.cmd.arguments.CaseArg;
import dev.gether.getcase.cmd.handler.InvalidUsageCommandHandler;
import dev.gether.getcase.cmd.handler.PermissionHandler;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.listener.InventoryClickListener;
import dev.gether.getcase.listener.InventoryCloseListener;
import dev.gether.getcase.listener.PlayerInteractionListener;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public final class GetCase extends JavaPlugin {

    // instance
    private static GetCase instance;

    // manager
    private LootBoxManager lootBoxManager;
    private LiteCommands<CommandSender> liteCommands;
    private HookManager hookManager;

    // file manager/config
    public static FileManager fileManager;

    private String secretKey;


    @Override
    public void onEnable() {
        // skeleton
        instance = this;
        fileManager = new FileManager(this);



        // hooks
        hookManager = new HookManager();

        lootBoxManager = new LootBoxManager(this, fileManager, hookManager);

        // register listener
        Stream.of(
                new InventoryCloseListener(lootBoxManager),
                new InventoryClickListener(lootBoxManager),
                new PlayerInteractionListener(lootBoxManager, fileManager)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        // register cmd
        registerLiteCmd();

        // register bstats
        new Metrics(this, 20299);

    }
    @Override
    public void onDisable() {

        lootBoxManager.deleteAllHolograms();

        if(liteCommands != null)
            liteCommands.getCommandManager().unregisterAll();

        HandlerList.unregisterAll(this);

    }

    public void reloadPlugin() {
        // close for all players inventory with case preview
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (LootBox caseDatum : lootBoxManager.getAllCases()) {
                // if player open inventory is same, then close the inventory
                if(player.getOpenInventory().getTopInventory().equals(caseDatum.getInventory())) {
                    player.closeInventory();
                    return;
                }
            }
        });
        // delete hologram
        lootBoxManager.deleteAllHolograms();
        // load new config
        fileManager.reload();

        // implements cases
        lootBoxManager.implementsAllCases();
        // create hologram for all cases
        lootBoxManager.getLocationCaseManager().createHolograms();
    }
    private void registerLiteCmd() {
        this.liteCommands =  LiteBukkitFactory.builder("getcase", this)
                .commands(
                        new GetCaseCmd(this, lootBoxManager)
                )
                // args
                .argument(LootBox.class, new CaseArg(lootBoxManager))
                //
                .invalidUsage(new InvalidUsageCommandHandler(fileManager))
                .missingPermission(new PermissionHandler(fileManager))
                .build();

    }

    public static GetCase getInstance() {
        return instance;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
