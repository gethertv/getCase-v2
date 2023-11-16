package dev.gether.getcase.manager;

import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.config.CaseLocationConfig;
import dev.gether.getcase.config.chest.*;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.utils.ItemBuilder;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CaseManager {

    private final CaseConfig caseConfig;
    private final HookManager hookManager;

    public CaseManager(CaseConfig caseConfig, HookManager hookManager) {
        this.caseConfig = caseConfig;
        this.hookManager = hookManager;
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
                .keyItem(
                        ItemBuilder.create(Material.TRIPWIRE_HOOK, "#77ff00&lKlucz "+caseName, true)
                )
                // create hologram
                .caseHologram(
                        CaseHologram.builder()
                                // check hook hologram plugin
                                .enable(hookManager.isDecentHologramsEnable())
                                .lines(List.of("&7-----------------", "#eaff4fSkrzynia " + caseName, "&7-----------------"))
                                .heightY(2.1)
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
                                .itemStack(ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, "&7", false))
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

        // implement inv
        caseObject.createInv();
        // add case to map
        caseConfig.getCaseData().add(caseObject);
        // save to config
        caseConfig.save();
        return true;
    }

    public void createAllInv() {
        caseConfig.getCaseData().forEach(CaseObject::createInv);
    }

    // find case by ID
    public Optional<CaseObject> findCaseByID(UUID caseId) {
        return caseConfig.getCaseData().stream().filter(caseObject -> caseObject.getCaseId().equals(caseId)).findFirst();
    }

    public Optional<Item> findItemByCaseAndSlot(CaseObject caseObject, int slot) {
        return caseObject.getItems().stream().filter(item -> item.getSlot()==slot).findFirst();
    }
    public boolean isAnimationSlot(int slot, CaseObject caseObject) {
       return caseObject.getAnimationSlots().contains(slot);
    }

    public boolean isNoAnimationSlot(int slot, CaseObject caseObject) {
        return caseObject.getNoAnimationSlots().contains(slot);
    }
    public Optional<CaseObject> findCaseByName(String caseName) {
        return caseConfig.getCaseData().stream().filter(caseObject -> caseObject.getName().equalsIgnoreCase(caseName)).findFirst();
    }


    public List<Suggestion> getAllNameSuggestionOfCase() {
        return caseConfig.getCaseData().stream().map(CaseObject::getName).map(Suggestion::of).toList();
    }

    public Optional<CaseObject> findCaseByInv(Inventory inventory) {
        return caseConfig.getCaseData().stream().filter(caseObject -> caseObject.getThis$inv().equals(inventory)).findFirst();
    }


    public void saveCaseFile() {
        caseConfig.save();
    }

    public void deleteAllHolograms() {
        for (CaseObject caseDatum : caseConfig.getCaseData()) {
            CaseHologram caseHologram = caseDatum.getCaseHologram();
            if(caseHologram.isEnable())
                caseHologram.deleteHologram();
        }
    }
    public boolean deleteCase(CaseObject caseName, LocationCaseManager locationCaseManager) {
        // delete all hologram/action with preview case
        List<CaseLocationConfig.CaseLocation> listCaseLocation = locationCaseManager.findCaseLocationById(caseName.getCaseId());
        if(!listCaseLocation.isEmpty()) {
            for (CaseLocationConfig.CaseLocation caseLocation : listCaseLocation) {
                // delete case preview [hologram and action]
                locationCaseManager.removeCaseLocation(caseLocation);
            }
            // save file
            locationCaseManager.saveFile();
        }

        // delete object case
        caseConfig.getCaseData().remove(caseName);
        // save file
        caseConfig.save();

        return true;
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
        for (CaseObject caseDatum : caseConfig.getCaseData()) {
            ItemStack keyItem = caseDatum.getKeyItem();
            if(keyItem.isSimilar(itemInMainHand) || keyItem.isSimilar(offHand))
                return true;
        }
        return false;
    }


}
