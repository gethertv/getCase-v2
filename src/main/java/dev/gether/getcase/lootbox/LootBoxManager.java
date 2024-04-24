package dev.gether.getcase.lootbox;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.chest.BroadcastCase;
import dev.gether.getcase.config.domain.chest.CaseHologram;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.lootbox.animation.Animation;
import dev.gether.getcase.lootbox.animation.AnimationManager;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getcase.lootbox.edit.EditLootBoxManager;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getcase.lootbox.location.LocationCaseManager;
import dev.gether.getconfig.ConfigManager;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemDecoration;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class LootBoxManager {

    private final EditLootBoxManager editLootBoxManager;
    private final AnimationManager animationManager;
    private final RewardsManager rewardsManager;
    private final LocationCaseManager locationCaseManager;
    private final FileManager fileManager;
    // key: name file (name case) | value: CaseObject
    private final HashMap<String, LootBox> allCases = new HashMap<>();

    public LootBoxManager(GetCase plugin, FileManager fileManager, HookManager hookManager) {
        this.fileManager = fileManager;

        rewardsManager = new RewardsManager(fileManager);
        animationManager = new AnimationManager(plugin, rewardsManager, fileManager);

        locationCaseManager = new LocationCaseManager(fileManager, this, hookManager);
        editLootBoxManager = new EditLootBoxManager(plugin, this);

        // create hologram for cases
        locationCaseManager.createHolograms();

        implementsAllCases();

    }

    // open case with animation
    public void openCase(Player player, final LootBox lootBox) {
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
        ItemUtil.removeItem(player, lootBox.getKeyItemStack(), 1);

        // open case with animation
        if(lootBox.getAnimation().getAnimationType() == AnimationType.SPIN) {
            // start animation
            animationManager.startSpin(player, lootBox);
        }
        // open case without the animation
        else if(lootBox.getAnimation().getAnimationType() == AnimationType.QUICK) {
            // give reward
            rewardsManager.giveReward(player, lootBox);
        }
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
        ItemUtil.removeItem(player, lootBox.getKeyItemStack(), 1);

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

    private boolean checkRequirements(Player player, LootBox lootBox) {
        // check case is not empty
        if(lootBox.getItems().isEmpty()) {
            MessageUtil.logMessage(ConsoleColor.RED, "Blad! Skrzynia nie posiada przedmiotow!");
            return false;
        }
        // check user has key
        if(!ItemUtil.hasItemStack(player, lootBox.getKeyItemStack())) {
            // send a message informing the user has not key
            MessageUtil.sendMessage(player, fileManager.getLangConfig().getNoKey());
            player.playSound(player.getLocation(), fileManager.getCaseConfig().getNoKeySound(), 1F, 1F);
            player.closeInventory();
            return false;
        }
        return true;
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
            MessageUtil.logMessage(ConsoleColor.GREEN, "Wczytano skrzynie "+caseName);
        }
    }

    public void createCase(String caseName) {

        LootBox lootBox = LootBox.builder()
                // generate random ID
                .enable(true)
                .caseId(UUID.randomUUID())
                .name(caseName)
                // title inv
                .titleInv("&0Skrzynia "+caseName)
                // size inv
                .sizeInv(54)
                // key
                .itemKey(Item.builder()
                        .material(Material.TRIPWIRE_HOOK)
                        .displayname("#77ff00&lKlucz "+caseName)
                        .lore(new ArrayList<>(List.of("&7")))
                        .glow(true)
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


    public void saveCaseFile() {
        fileManager.getCaseConfig().save();
    }

    public void deleteAllHolograms() {
        for (CaseLocation caseLocation : fileManager.getCaseLocationConfig().getCaseLocationData()) {
            CaseHologram caseHologram = caseLocation.getCaseHologram();
            if(caseHologram.isEnable())
                caseHologram.deleteHologram();
        }
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
        file.delete();
    }


    public void givePlayerKey(Player target, LootBox lootBox, int amount) {
        ItemStack itemStack = lootBox.getKeyItemStack().clone();
        itemStack.setAmount(amount);
        // give key
        target.getInventory().addItem(itemStack);
    }

    public void giveAllKey(LootBox lootBox, int amount) {
        ItemStack itemStack = lootBox.getKeyItemStack().clone();
        itemStack.setAmount(amount);
        // give all key
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(itemStack));

        return;
    }

    public boolean checkIsKey(ItemStack itemInMainHand, ItemStack offHand) {
        for (LootBox caseDatum : allCases.values()) {
            ItemStack keyItem = caseDatum.getKeyItemStack();
            if(keyItem.isSimilar(itemInMainHand) || keyItem.isSimilar(offHand))
                return true;
        }
        return false;
    }

    public List<LootBox> getAllCases() {
        return allCases.values().stream().toList();
    }


    public RewardsManager getRewardsManager() {
        return rewardsManager;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public EditLootBoxManager getEditLootBoxManager() {
        return editLootBoxManager;
    }

    public LocationCaseManager getLocationCaseManager() {
        return locationCaseManager;
    }
}
