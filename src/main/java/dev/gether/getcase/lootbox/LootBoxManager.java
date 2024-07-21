package dev.gether.getcase.lootbox;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.chest.BroadcastCase;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.lootbox.addons.AddonsManager;
import dev.gether.getcase.lootbox.animation.Animation;
import dev.gether.getcase.lootbox.animation.AnimationManager;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getcase.lootbox.edit.EditLootBoxManager;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getcase.lootbox.location.LocationCaseManager;
import dev.gether.getconfig.ConfigManager;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemDecoration;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class LootBoxManager {

    private final EditLootBoxManager editLootBoxManager;
    private final AnimationManager animationManager;
    private final RewardsManager rewardsManager;
    private final LocationCaseManager locationCaseManager;
    private final FileManager fileManager;
    // key: name file (name case) | value: CaseObject
    private final HashMap<String, LootBox> allCases = new HashMap<>();

    public LootBoxManager(GetCase plugin, FileManager fileManager) {
        this.fileManager = fileManager;

        rewardsManager = new RewardsManager(fileManager);
        animationManager = new AnimationManager(plugin, rewardsManager, fileManager);

        locationCaseManager = new LocationCaseManager(fileManager, this, new AddonsManager(plugin));
        editLootBoxManager = new EditLootBoxManager(plugin, this);

        // first implement case, after this create a hologram
        implementsAllCases();

        // create hologram for cases
        locationCaseManager.createHolograms();


    }

    // open case with animation
    public void openCase(Player player, final LootBox lootBox, AnimationType animationType) {
        // check case is enable
        if(!lootBox.isEnable()) {
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getCaseIsDisable());
            return;
        }
        // check requirements like CASE is not empty and player has key for this case
        boolean hasRequirements  = checkRequirements(player, lootBox);
        // is not meets then return
        if(!hasRequirements)
            return;

        // take key
        removeKey(player, lootBox, 1);

        // open case with animation
        if(animationType == AnimationType.SPIN) {
            // start animation
            animationManager.startSpin(player, lootBox);
        }
        // open case without the animation
        else if(animationType == AnimationType.QUICK) {
            // give reward
            rewardsManager.giveReward(player, lootBox);
        }
    }

    private void removeKey(Player player, LootBox lootBox, int amount) {
        int remove = amount;

        for(int i = 0; i < player.getInventory().getSize(); ++i) {
            ItemStack current = player.getInventory().getItem(i);
            if (current != null) {
                ItemMeta itemMeta = current.getItemMeta();
                if(itemMeta==null)
                    continue;

                String caseUUIDString = itemMeta.getPersistentDataContainer().get(GetCase.NAMESPACED_KEY, PersistentDataType.STRING);
                if(caseUUIDString==null)
                    continue;

                UUID caseUUID  = UUID.fromString(caseUUIDString);
                if(!lootBox.getCaseId().equals(caseUUID))
                    continue;

                int currentAmount = current.getAmount();
                if (currentAmount >= remove) {
                    current.setAmount(currentAmount - remove);
                    break;
                }

                player.getInventory().setItem(i, (ItemStack)null);
                remove -= currentAmount;
            }
        }
    }
    public void openAllCase(Player player, final LootBox lootBox) {
        int amountKey = ItemUtil.calcItem(player, lootBox.getKey());
        for (int i = 0; i < amountKey; i++) {
            // check case is enable
            if(!lootBox.isEnable()) {
                MessageUtil.sendMessage(player, fileManager.getLangConfig().getCaseIsDisable());
                return;
            }
            // check requirements like CASE is not empty and player has key for this case
            boolean hasRequirements  = checkRequirements(player, lootBox);
            // is not meets then return
            if(!hasRequirements)
                return;

            // take key
            ItemUtil.removeItem(player, lootBox.getKey(), 1);
            rewardsManager.giveRewardWithoutPreview(player, lootBox);
        }
    }

    private boolean checkRequirements(Player player, LootBox lootBox) {
        // check case is not empty
        if(lootBox.getItems().isEmpty()) {
            MessageUtil.logMessage(ConsoleColor.RED, "Error! This lootbox is empty! (No items)");
            return false;
        }
        // check user has key
        if(!hasKey(player)) {
            // send a message informing the user has not key
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getNoKey());
            player.playSound(player.getLocation(), fileManager.getCaseConfig().getNoKeySound(), 1F, 1F);
            player.closeInventory();
            return false;
        }
        return true;
    }

    public boolean hasKey(Player player)
    {
        for(ItemStack itemStack : player.getInventory()) {
            if(itemStack==null || itemStack.getType()== Material.AIR)
                continue;

            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta==null)
                continue;

            if(itemMeta.getPersistentDataContainer().has(GetCase.NAMESPACED_KEY, PersistentDataType.STRING))
                return true;
        }

        return false;
    }



    public void implementsAllCases() {
        allCases.clear();
        File[] files = FileManager.FILE_PATH_CASES.listFiles();
        if(files == null)
            return;

        for (File file : files) {
            if(file.isDirectory())
                continue;

            String caseName = file.getName().replace(".yml", "");
            LootBox lootBox = ConfigManager.create(LootBox.class, it -> {});
            implementCase(caseName, lootBox);
            MessageUtil.logMessage(ConsoleColor.GREEN, "Loaded case "+caseName);
        }
    }

    public void createCase(String caseName) {

        LootBox lootBox = LootBox.builder()
                // generate random ID
                .enable(true)
                .lootboxType(LootboxType.LOOTBOX)
                .caseId(UUID.randomUUID())
                .name(caseName)
                // title inv
                .titleInv("&0LootBox "+caseName)
                // size inv
                .sizeInv(54)
                // key
                .itemKey(Item.builder()
                        .material(Material.TRIPWIRE_HOOK)
                        .displayname("#77ff00&lKey "+caseName)
                        .lore(new ArrayList<>(List.of(" ")))
                        .glow(true)
                        .unbreakable(true)
                        .build()

                )
                .items(new HashSet<>())
                // broadcast message
                .broadcastCase(
                        BroadcastCase.builder()
                                .enable(true)
                                .messages(new ArrayList<>())
                                .build()
                )
                // decoration items
                .decorations(Set.of(
                        ItemDecoration.builder()
                                .item(Item.builder()
                                        .material(Material.BLACK_STAINED_GLASS_PANE)
                                        .displayname("&7")
                                        .lore(new ArrayList<>(List.of(" ")))
                                        .glow(false)
                                        .build())
                                .slots(Set.of(0,1,2,3,4,5,6,7,8))
                                .build()
                ))
                // no animation
                .animation(Animation.builder()
                        .animationType(AnimationType.SPIN)
                        .noAnimationSlots(
                                Set.of(50,51,52)
                        )
                        // animation
                        .animationSlots(
                                Set.of(46,47,48)
                        )
                        .build())
                .build();

        implementCase(caseName, lootBox);
    }

    private void implementCase(String caseName, LootBox lootBox) {
        lootBox.file(new File(FileManager.FILE_PATH_CASES, "{name}.yml".replace("{name}", caseName)));
        lootBox.load();
        // create inv (preview drop)
        lootBox.createInv();
        // add case to map
        allCases.put(caseName, lootBox);
    }
    public void disableCase(LootBox lootBox) {
        lootBox.setEnable(false);
        lootBox.save();
    }

    public void disableAllCases() {
        allCases.values().forEach(this::disableCase);
    }

    public void enableCase(LootBox lootBox) {
        lootBox.setEnable(true);
        lootBox.save();
    }

    public void enableAllCases() {
        allCases.values().forEach(this::enableCase);
    }

    // find case by ID
    public Optional<LootBox> findCaseByID(UUID caseId) {
        return allCases.values().stream().filter(caseObject -> caseObject.getCaseId().equals(caseId)).findFirst();
    }

    public Optional<ItemCase> findItemByCaseAndSlot(LootBox lootBox, int slot) {
        return lootBox.getItems().stream().filter(item -> item.getSlot()==slot).findFirst();
    }
    public boolean isAnimationSlot(int slot, LootBox lootBox) {
        return lootBox.getAnimation().getAnimationSlots().contains(slot);
    }

    public boolean isNoAnimationSlot(int slot, LootBox lootBox) {
        return lootBox.getAnimation().getNoAnimationSlots().contains(slot);
    }

    public Optional<LootBox> findCaseByName(String caseName) {
        return Optional.ofNullable(allCases.get(caseName));
    }

    public SuggestionResult getAllNameSuggestionOfCase() {
        return allCases.values().stream().map(LootBox::getName).collect(SuggestionResult.collector());
    }

    public Optional<LootBox> findCaseByInv(Inventory inventory) {
        return allCases.values().stream().filter(caseObject -> caseObject.getInventory().equals(inventory)).findFirst();
    }

    public void deleteCase(LootBox lootBox) {
        // delete all hologram/action with preview case
        List<CaseLocation> listCaseLocation = locationCaseManager.findCaseLocationById(lootBox.getCaseId());
        if(!listCaseLocation.isEmpty()) {
            for (CaseLocation caseLocation : listCaseLocation) {
                // delete case preview [hologram and action]
                locationCaseManager.removeCaseLocation(caseLocation);
            }
            // save file
            locationCaseManager.saveFile();
        }

        // delete object case
        allCases.remove(lootBox.getName());
        // delete file
        File file = new File(FileManager.FILE_PATH_CASES, lootBox.getName()+".yml");
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void givePlayerKey(Player target, LootBox lootBox, int amount) {
        ItemStack itemStack = lootBox.getKey().clone();
        itemStack.setAmount(amount);
        // give key
        target.getInventory().addItem(itemStack);
    }

    public void giveAllKey(LootBox lootBox, int amount) {
        ItemStack itemStack = lootBox.getKey().clone();
        itemStack.setAmount(amount);
        // give all key
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(itemStack));
    }

    public Optional<LootBox> checkIsKey(ItemStack itemInMainHand, ItemStack offHand) {
        for (LootBox lootBox : allCases.values()) {
            ItemStack keyItem = lootBox.getKey();
            if(keyItem.isSimilar(itemInMainHand) || keyItem.isSimilar(offHand))
                return Optional.of(lootBox);
        }
        return Optional.empty();
    }

    public List<LootBox> getAllCases() {
        return allCases.values().stream().toList();
    }


    public RewardsManager getRewardsManager() {
        return rewardsManager;
    }

    public EditLootBoxManager getEditLootBoxManager() {
        return editLootBoxManager;
    }

    public LocationCaseManager getLocationCaseManager() {
        return locationCaseManager;
    }


}
