package dev.gether.getcase.lootbox;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.lootbox.animation.Animation;
import dev.gether.getcase.lootbox.animation.AnimationManager;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getcase.lootbox.animation.MultiCaseAnimationManager;
import dev.gether.getcase.lootbox.edit.EditLootBoxManager;
import dev.gether.getcase.lootbox.inv.preview.MultiCaseHolder;
import dev.gether.getcase.lootbox.inv.preview.PreviewChestHolder;
import dev.gether.getcase.lootbox.location.LocationCaseManager;
import dev.gether.getcase.lootbox.model.CaseLocation;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getcase.user.User;
import dev.gether.getutils.ConfigManager;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.models.inventory.InventoryConfig;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.FileUtil;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.getutils.utils.PlayerUtil;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LootBoxManager {

    GetCase plugin;
    @Getter
    EditLootBoxManager editLootBoxManager;
    @Getter
    AnimationManager animationManager;
    @Getter
    RewardsManager rewardsManager;
    @Getter
    LocationCaseManager locationCaseManager;
    @Getter
    MultiCaseAnimationManager multiCaseAnimationManager;

    FileManager fileManager;
    // key: name file (name case) | value: CaseObject
    HashMap<String, LootBox> cases = new HashMap<>();

    public LootBoxManager(GetCase plugin, FileManager fileManager, HookManager hookManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;

        rewardsManager = new RewardsManager(plugin, this, fileManager);
        multiCaseAnimationManager = new MultiCaseAnimationManager(plugin, rewardsManager, fileManager);
        animationManager = new AnimationManager(plugin, rewardsManager, fileManager, multiCaseAnimationManager);

        locationCaseManager = new LocationCaseManager(fileManager, this, hookManager);
        editLootBoxManager = new EditLootBoxManager(plugin, this);

        // first implement case, after this create a hologram
        implementsAllCases();

        // create hologram for cases
        locationCaseManager.createHolograms();

    }


    // open case with animation
    public void openCase(Player player, final LootBox lootBox, AnimationType animationType) {
        // check case is enable
        if (!lootBox.isEnable()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getCaseIsDisable());
            return;
        }
        // check requirements like CASE is not empty and player has key for this case
        boolean hasRequirements = checkRequirements(player, lootBox, 1);
        // is not meets then return
        if (!hasRequirements)
            return;

        if (isInventoryFull(player)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getFullInventory());
            return;
        }

        // take key
        PlayerUtil.removeItemsByKey(player, lootBox.getNamespacedKey(), 1);
        // increase value with opened cases
        Optional<User> userByPlayer = plugin.getUserManager().findUserByPlayer(player);
        userByPlayer.ifPresent(user -> {
            user.openedCase(lootBox.getCaseName(), 1);
        });


        // open case with animation
        if (animationType == AnimationType.SPIN) {
            // start animation
            animationManager.startSpin(player, lootBox);
        }
        // open case without the animation
        else if (animationType == AnimationType.QUICK) {
            // give reward
            rewardsManager.giveReward(player, lootBox, true);
        }
    }

    private boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void openAllCase(Player player, final LootBox lootBox) {
        int amountKey = PlayerUtil.countItemsByKey(player, lootBox.getNamespacedKey());
        for (int i = 0; i < amountKey; i++) {
            // check case is enable
            if (!lootBox.isEnable()) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getCaseIsDisable());
                return;
            }

            // check requirements like CASE is not empty and player has key for this case
            boolean hasRequirements = checkRequirements(player, lootBox, 1);
            // is not meets then return
            if (!hasRequirements)
                return;

            if (isInventoryFull(player)) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getFullInventory());
                return;
            }

            // take key
            PlayerUtil.removeItemsByKey(player, lootBox.getNamespacedKey(), 1);
            // increase amount/counting
            Optional<User> userByPlayer = plugin.getUserManager().findUserByPlayer(player);
            userByPlayer.ifPresent(user -> {
                user.openedCase(lootBox.getCaseName(), 1);
            });


            rewardsManager.giveReward(player, lootBox, false);

        }
    }

    private boolean checkRequirements(Player player, LootBox lootBox, int amount) {
        // check case is not empty
        if (lootBox.getItems().isEmpty()) {
            MessageUtil.logMessage(ConsoleColor.RED, "Error! This lootbox is empty! (No items)");
            return false;
        }
        // check user has key
        int size = PlayerUtil.countItemsByKey(player, lootBox.getNamespacedKey());
        if (size < amount) {
            // send a message informing the user has not key
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getNoKey());
            player.playSound(player.getLocation(), fileManager.getCaseConfig().getNoKeySound(), 1F, 1F);
            player.closeInventory();
            return false;
        }
        return true;
    }


    public void implementsAllCases() {
        cases.clear();
        File[] files = FileManager.FILE_PATH_CASES.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            String caseName = file.getName();
            // lootbox
            LootBox lootBox = ConfigManager.create(LootBox.class, it -> {
            });

            // case location
            CaseLocation caseLocation = implementHolograms(caseName);
            lootBox.setCaseLocation(caseLocation);

            // implement loot box
            implementCase(caseName, lootBox);

            MessageUtil.logMessage(ConsoleColor.GREEN, "Loaded case " + caseName);
        }
    }

    private CaseLocation implementHolograms(String caseName) {
        return ConfigManager.create(CaseLocation.class, it -> {
            it.setFile(new File(FileManager.FILE_PATH_CASES + "/" + caseName + "/", caseName + "-location.yml"));
            it.load();
        });
    }

    public void createCase(String caseName) {

        UUID caseUUID = UUID.randomUUID();

        LootBox lootBox = LootBox.builder()
                // generate random ID
                .enable(true)
                .lootboxType(LootboxType.LOOTBOX)
                .caseId(caseUUID)
                .caseName(caseName)
                // title inv
                .item(Item.builder()
                        .material(Material.TRIPWIRE_HOOK)
                        .name("#77ff00&lKey " + caseName)
                        .lore(new ArrayList<>(List.of(" ")))
                        .glow(true)
                        .unbreakable(true)
                        .build()

                )
                .items(new HashSet<>())
                // broadcast message
                .broadcastCase(
                        LootBox.BroadcastCase.builder()
                                .enable(true)
                                .messages(new ArrayList<>())
                                .build()
                )
                .previewInventoryConfig(InventoryConfig.builder()
                        .size(54)
                        .cancelClicks(true)
                        .refreshInterval(0)
                        .title("&0getCase")
                        .decorations(new ArrayList<>(List.of(
                                DynamicItem.builder()
                                        .item(Item.builder().material(Material.BLACK_STAINED_GLASS_PANE).name("&7").build())
                                        .slots(new ArrayList<>(List.of(
                                                0, 1, 2, 3, 4, 5, 6, 7, 8,
                                                9, 10, 11, 12, 13, 14, 15, 16, 17,
                                                18, 19, 20, 21, 22, 23, 24, 25, 26,
                                                27, 28, 29, 30, 31, 32, 33, 34, 35,
                                                36, 37, 38, 39, 40, 41, 42, 43, 44,
                                                45, 46, 47, 48, 49, 50, 51, 52, 53
                                        )))
                                        .build()
                        )))
                        .build()
                )
                .caseLocation(new CaseLocation(caseUUID))
                .animation(new Animation(AnimationType.SPIN, Set.of(50, 51, 52), Set.of(46, 47, 48), Set.of(49)))
                .build();

        plugin.getUserManager().updateColumn(lootBox);
        implementCase(caseName, lootBox);
    }

    private void implementCase(String caseName, LootBox lootBox) {
        lootBox.setFile(new File(FileManager.FILE_PATH_CASES + "/" + caseName + "/", caseName + ".yml"));
        lootBox.getCaseLocation().setFile(new File(FileManager.FILE_PATH_CASES + "/" + caseName + "/", caseName + "-location.yml"));

        lootBox.load();
        lootBox.setNamespacedKey(new NamespacedKey(plugin, lootBox.getCaseId().toString()));
        // add case to map
        cases.put(caseName, lootBox);
    }

    public void disableCase(LootBox lootBox) {
        lootBox.setEnable(false);
        lootBox.save();
    }

    public void disableAllCases() {
        cases.values().forEach(this::disableCase);
    }

    public void enableCase(LootBox lootBox) {
        lootBox.setEnable(true);
        lootBox.save();
    }

    public void enableAllCases() {
        cases.values().forEach(this::enableCase);
    }

    public Optional<LootBox> findCaseByName(String caseName) {
        return Optional.ofNullable(cases.get(caseName));
    }

    public Optional<LootBox> findCaseByLocation(Location location) {
        return cases.values().stream().filter(lootBox -> {
            List<CaseLocation.CaseHologram> caseHolograms = lootBox.getCaseLocation().getCaseHolograms();
            if (caseHolograms == null || caseHolograms.isEmpty()) return false;
            return caseHolograms.stream().anyMatch(hologram -> hologram.getLocation().equals(location));
        }).findFirst();
    }

    public SuggestionResult getAllNameSuggestionOfCase() {
        return cases.values().stream().map(LootBox::getCaseName).collect(SuggestionResult.collector());
    }

    public void deleteAllHolograms() {
        for (Hologram hologram : locationCaseManager.getHolograms()) {
            hologram.destroy();
        }
    }

    public void openPreview(Player player, LootBox lootBox) {
        // play sound
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getPreviewMenuSound(), 1F, 1F);
        // open inventory with preview drop
        PreviewChestHolder previewChestHolder = new PreviewChestHolder(plugin, player, this, lootBox, fileManager);
        player.openInventory(previewChestHolder.getInventory());
    }

    public void deleteCase(LootBox lootBox) {
        CaseLocation caseLocation = lootBox.getCaseLocation();
        for (CaseLocation.CaseHologram caseHologram : caseLocation.getCaseHolograms()) {
            // delete case preview [hologram and action]
            locationCaseManager.deleteHologram(caseHologram.getHologramKey());
        }
        // delete object case
        cases.remove(lootBox.getCaseName());
        // delete file
        File file = new File(FileManager.FILE_PATH_CASES + "/" + lootBox.getCaseName() + "/");
        FileUtil.deleteDirectory(file);
    }


    public void givePlayerKey(Player target, LootBox lootBox, int amount) {
        ItemStack itemStack = lootBox.getItemKey().clone();
        itemStack.setAmount(amount);
        // give key
        target.getInventory().addItem(itemStack);
    }

    public void giveAllKey(LootBox lootBox, int amount) {
        if (fileManager.getCaseConfig().isUseGradualKeyDistribution()) {
            giveAllKeyGradually(lootBox, amount);
        } else {
            giveAllKeyImmediately(lootBox, amount);
        }
    }

    private void giveAllKeyGradually(LootBox lootBox, int amount) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        int playersPerTick = fileManager.getCaseConfig().getPlayersPerTickForKeyDistribution();
        int totalPlayers = players.size();

        new BukkitRunnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                for (int i = 0; i < playersPerTick && currentIndex < totalPlayers; i++) {
                    Player player = players.get(currentIndex);
                    givePlayerKey(player, lootBox, amount);
                    currentIndex++;
                }

                if (currentIndex >= totalPlayers) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void giveAllKeyImmediately(LootBox lootBox, int amount) {
        Bukkit.getOnlinePlayers().forEach(player -> givePlayerKey(player, lootBox, amount));
    }

    public Optional<LootBox> checkIsKey(ItemStack itemInMainHand, ItemStack offHand) {
        ItemMeta itemMeta = itemInMainHand.getItemMeta();
        if (itemMeta == null)
            return Optional.empty();

        return cases.values().stream().filter(lootBox -> {
            return itemMeta.getPersistentDataContainer().has(lootBox.getNamespacedKey(), PersistentDataType.STRING);
        }).findFirst();
    }

    public List<LootBox> getCases() {
        return cases.values().stream().toList();
    }


    public void openMultiCaseInv(Player player, LootBox lootBox) {
        MultiCaseHolder multiCaseHolder = new MultiCaseHolder(plugin, player, fileManager.getMultiCaseOpeningConfig().getInventorySelection(), fileManager, lootBox);
        player.openInventory(multiCaseHolder.getInventory());
    }

    public void openMultiCase(Player player, LootBox lootBox, int sizeOpeningCase) {
        // check case is enable
        if (!lootBox.isEnable()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getCaseIsDisable());
            return;
        }
        // check requirements like CASE is not empty and player has key for this case
        boolean hasRequirements = checkRequirements(player, lootBox, sizeOpeningCase);
        // is not meets then return
        if (!hasRequirements)
            return;

        if (isInventoryFull(player)) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getFullInventory());
            return;
        }

        // take key
        PlayerUtil.removeItemsByKey(player, lootBox.getNamespacedKey(), sizeOpeningCase);
        // stats
        Optional<User> userByPlayer = plugin.getUserManager().findUserByPlayer(player);
        userByPlayer.ifPresent(user -> {
            user.openedCase(lootBox.getCaseName(), sizeOpeningCase);
        });

        // start spinning
        animationManager.startMultiCaseSpin(player, lootBox, sizeOpeningCase);


    }
}
