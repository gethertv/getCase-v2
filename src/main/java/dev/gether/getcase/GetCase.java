package dev.gether.getcase;

import dev.gether.getcase.bstats.Metrics;
import dev.gether.getcase.cmd.GetCaseCmd;
import dev.gether.getcase.cmd.arguments.CaseArg;
import dev.gether.getcase.cmd.handler.InvalidUsageCommandHandler;
import dev.gether.getcase.cmd.handler.PermissionHandler;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.listener.ConnectPlayerListener;
import dev.gether.getcase.listener.InventoryCloseListener;
import dev.gether.getcase.listener.PlayerInteractionListener;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.inv.preview.PreviewChestHolder;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.placeholder.CasePlaceholder;
import dev.gether.getcase.storage.MySQL;
import dev.gether.getcase.user.UserManager;
import dev.gether.getutils.models.inventory.GetInventory;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class GetCase extends JavaPlugin {

    // instance
    @Getter static GetCase instance;

    // manager
    LootBoxManager lootBoxManager;
    LiteCommands<CommandSender> liteCommands;
    HookManager hookManager;
    UserManager userManager;

    // file manager/config
    FileManager fileManager;

    // database
    MySQL mySQL;

    // placeholder
    CasePlaceholder casePlaceholder;

    @Override
    public void onEnable() {
        // skeleton
        instance = this;
        // config/file
        fileManager = new FileManager(this);

        // database
        mySQL = new MySQL(this, fileManager);
        if (!mySQL.isConnected()) {
            getLogger().severe("Cannot connect to the database!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        // init gui/lib - getUtils
        new GetInventory(this);

        // hooks
        hookManager = new HookManager();

        // main manager
        lootBoxManager = new LootBoxManager(this, fileManager, hookManager);
        // user manager
        userManager = new UserManager(this, lootBoxManager, mySQL);

        // register listener
        Stream.of(
                new InventoryCloseListener(lootBoxManager),
                new PlayerInteractionListener(this, lootBoxManager, fileManager),
                new ConnectPlayerListener(userManager)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        // register cmd
        registerLiteCmd();

        // placeholder
        if(hookManager.isPlaceholderEnabled()) {
            casePlaceholder = new CasePlaceholder(userManager);
            casePlaceholder.register();
            MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] Placeholder registered!");
        }


        // register bstats
        new Metrics(this, 20299);

    }

    @Override
    public void onDisable() {

        lootBoxManager.deleteAllHolograms();

        if(casePlaceholder != null) {
            casePlaceholder.unregister();
        }

        // database/save
        if (mySQL != null) {
            userManager.saveUsersSync();
            mySQL.disconnect();
        }

        if (liteCommands != null)
            liteCommands.getCommandManager().unregisterAll();

        HandlerList.unregisterAll(this);

    }

    public void reloadPlugin() {
        // close for all players inventory with case preview
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (LootBox lootBox : lootBoxManager.getCases()) {
                // if player open inventory is same, then close the inventory
                Inventory topInventory = player.getOpenInventory().getTopInventory();
                if (topInventory.getHolder() instanceof PreviewChestHolder)
                    player.closeInventory();
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
        this.liteCommands = LiteBukkitFactory.builder("getcase", this)
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

}
