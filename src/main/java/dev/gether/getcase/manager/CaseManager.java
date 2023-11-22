package dev.gether.getcase.manager;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.config.CaseLocation;
import dev.gether.getcase.config.CaseLocationConfig;
import dev.gether.getcase.config.chest.*;
import dev.gether.getconfig.ConfigManager;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemDecoration;
import dev.gether.getconfig.utils.ConsoleColor;
import dev.gether.getconfig.utils.ItemBuilder;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class CaseManager {

    private final CaseConfig caseConfig;
    private final CaseLocationConfig caseLocationConfig;
    // key: name file (name case) | value: CaseObject
    private final HashMap<String, CaseObject> allCases = new HashMap<>();

    public CaseManager(CaseConfig caseConfig, CaseLocationConfig caseLocationConfig) {
        this.caseConfig = caseConfig;
        this.caseLocationConfig = caseLocationConfig;

        implementsAllCases();
    }

    public void implementsAllCases() {
        allCases.clear();
        File[] files = GetCase.FILE_PATH_CASES.listFiles();
        if(files == null)
            return;

        for (File file : files) {
            if(file.isDirectory())
                continue;

            String caseName = file.getName().replace(".yml", "");
            CaseObject caseObject = ConfigManager.create(CaseObject.class, it -> {});
            implementCase(caseName, caseObject);
            MessageUtil.logMessage(ConsoleColor.GREEN, "Wczytano skrzynie "+caseName);
        }
    }

    public boolean createCase(String caseName) {

        CaseObject caseObject = CaseObject.builder()
                // generate random ID
                .caseId(UUID.randomUUID())
                .name(caseName)
                // title inv
                .titleInv("&0Skrzynia "+caseName)
                // size inv
                .sizeInv(54)
                // key
                .itemKey(Item.builder()
                        .itemStack(ItemBuilder.create(Material.TRIPWIRE_HOOK, "#77ff00&lKlucz "+caseName, new ArrayList<>(List.of("&7")), true))
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
                                        .itemStack(ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, "&7", false))
                                        .build())
                                .slots(Set.of(0,1,2,3,4,5,6,7,8))
                                .build()
                ))
                // no animation
                .noAnimationSlots(
                        Set.of(50,51,52)
                )
                // animation
                .animationSlots(
                        Set.of(46,47,48)
                )
                .build();

        implementCase(caseName, caseObject);
        return true;
    }

    private void implementCase(String caseName, CaseObject caseObject) {
        caseObject.file(new File(GetCase.FILE_PATH_CASES, "{name}.yml".replace("{name}", caseName)));
        caseObject.load();
        // create inv (preview drop)
        caseObject.createInv();
        // add case to map
        allCases.put(caseName, caseObject);
    }

    // find case by ID
    public Optional<CaseObject> findCaseByID(UUID caseId) {
        return allCases.values().stream().filter(caseObject -> caseObject.getCaseId().equals(caseId)).findFirst();
    }

    public Optional<ItemCase> findItemByCaseAndSlot(CaseObject caseObject, int slot) {
        return caseObject.getItems().stream().filter(item -> item.getSlot()==slot).findFirst();
    }
    public boolean isAnimationSlot(int slot, CaseObject caseObject) {
       return caseObject.getAnimationSlots().contains(slot);
    }

    public boolean isNoAnimationSlot(int slot, CaseObject caseObject) {
        return caseObject.getNoAnimationSlots().contains(slot);
    }
    public Optional<CaseObject> findCaseByName(String caseName) {
        return Optional.ofNullable(allCases.get(caseName));
    }


    public List<Suggestion> getAllNameSuggestionOfCase() {
        return allCases.values().stream().map(CaseObject::getName).map(Suggestion::of).toList();
    }

    public Optional<CaseObject> findCaseByInv(Inventory inventory) {
        return allCases.values().stream().filter(caseObject -> caseObject.getInventory().equals(inventory)).findFirst();
    }


    public void saveCaseFile() {
        caseConfig.save();
    }

    public void deleteAllHolograms() {
        for (CaseLocation caseLocation : caseLocationConfig.getCaseLocationData()) {
            CaseHologram caseHologram = caseLocation.getCaseHologram();
            if(caseHologram.isEnable())
                caseHologram.deleteHologram();
        }
    }
    public boolean deleteCase(CaseObject caseObject, LocationCaseManager locationCaseManager) {
        // delete all hologram/action with preview case
        List<CaseLocation> listCaseLocation = locationCaseManager.findCaseLocationById(caseObject.getCaseId());
        if(!listCaseLocation.isEmpty()) {
            for (CaseLocation caseLocation : listCaseLocation) {
                // delete case preview [hologram and action]
                locationCaseManager.removeCaseLocation(caseLocation);
            }
            // save file
            locationCaseManager.saveFile();
        }

        // delete object case
        allCases.remove(caseObject.getName());
        // delete file
        File file = new File(GetCase.FILE_PATH_CASES, caseObject.getName()+".yml");
        return file.delete();
    }


    public boolean givePlayerKey(Player target, CaseObject caseObject, int amount) {
        ItemStack itemStack = caseObject.getKeyItem().clone();
        itemStack.setAmount(amount);
        // give key
        target.getInventory().addItem(itemStack);
        return true;
    }

    public boolean giveAllKey(CaseObject caseObject, int amount) {
        ItemStack itemStack = caseObject.getKeyItem().clone();
        itemStack.setAmount(amount);
        // give all key
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(itemStack));

        return true;
    }

    public boolean checkIsKey(ItemStack itemInMainHand, ItemStack offHand) {
        for (CaseObject caseDatum : allCases.values()) {
            ItemStack keyItem = caseDatum.getKeyItem();
            if(keyItem.isSimilar(itemInMainHand) || keyItem.isSimilar(offHand))
                return true;
        }
        return false;
    }

    public void initCaseInv() {
        allCases.values().forEach(CaseObject::createInv);
    }

    public List<CaseObject> getAllCases() {
        return allCases.values().stream().toList();
    }
}
