package dev.gether.getcase.manager;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.config.chest.ItemCase;
import dev.gether.getcase.inv.EditCaseInvHandler;
import net.md_5.bungee.api.ChatColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AdminEditManager {

    private final CaseManager caseManager;
    private final GetCase plugin;

    public AdminEditManager(CaseManager caseManager, GetCase plugin) {
        this.caseManager = caseManager;
        this.plugin = plugin;
    }

    public void editCase(Player player, CaseObject caseObject) {
        EditCaseInvHandler editCaseInventory = new EditCaseInvHandler(player, caseObject);
        // this inv
        Inventory inventory = editCaseInventory.getInventory();
        // open inv
        player.openInventory(inventory);
    }

    public void saveCase() {
        caseManager.saveCaseFile();
    }

    public void editItem(EditCaseInvHandler editCaseInvHandler, int slot, ItemStack itemStack) {

        // check itemstack exits in list with items
        Optional<ItemCase> itemByCaseAndItemStack = caseManager.findItemByCaseAndSlot(editCaseInvHandler.getCaseObject(), slot);
        // create default object if item will not exist
        ItemCase item = new ItemCase(slot, 0, itemStack, List.of("&7", "&7Szansa: &f{chance}%", "&7"));
        // if exists then not create new object
        if(itemByCaseAndItemStack.isPresent()) {
            item = itemByCaseAndItemStack.get();
        } else {
            // if item not exists in list then add
            editCaseInvHandler.getCaseObject().getItems().add(item);
        }

        // create anvil builder
        AnvilGUI.Builder builder = new AnvilGUI.Builder();

        // final object with item
        final ItemCase finalItem = item;
        // builder onClick event
        builder.onClick((anvilSlot, stateSnapshot) -> {
            if (anvilSlot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            String text = stateSnapshot.getText();
            if(!isDouble(text)) {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Podaj liczbe"));
            }
            // parse chance from text to double
            double chance = Double.parseDouble(text);
            // set new chance
            finalItem.setChance(chance);
            // [!] IGNORE THIS SAVE
            // because the saving method exists in main GUI/INV with button/item 'SAVE'
            // save items
            //saveCase(editCaseInvHandler.getCaseObject());
            // open preview inv
            return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run( () -> refreshEditInv(editCaseInvHandler))
            );
        });
        // title gui
        builder.title("Podaj szanse");
        // left item text
        builder.text("0.00");
        // left item
        builder.itemLeft(new ItemStack(Material.PAPER));
        // set instance from main plugin
        builder.plugin(plugin);
        // open anvil inv
        builder.open(editCaseInvHandler.getPlayer());
    }

    // clear the old items
    // fill with new
    // and open the inv
    private void refreshEditInv(EditCaseInvHandler editCaseInvHandler) {
        // get edit inv
        Inventory inventory = editCaseInvHandler.getInventory();
        // clear
        inventory.clear();
        // fill with actually items
        editCaseInvHandler.fillInvByItems();
        // open the inv
        editCaseInvHandler.getPlayer().openInventory(inventory);
    }

    private boolean isDouble(String input) {
        try {
            double chance = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException ignored) {}
        return false;
    }

    public void saveAllItems(EditCaseInvHandler editCaseInvHandler) {
        Inventory inv = editCaseInvHandler.getInventory();
        // foreach items and check they are exists in set<>
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack itemStack = cleanItem(inv.getItem(slot));
            Optional<ItemCase> itemByCaseAndItemStack = caseManager.findItemByCaseAndSlot(editCaseInvHandler.getCaseObject(), slot);
            if(itemByCaseAndItemStack.isEmpty()) {
                if(itemStack==null || slot == inv.getSize()-1)
                    continue;

                ItemCase item = new ItemCase(slot, 0, itemStack, List.of("&7", "&7Szansa: &f{chance}%", "&7"));
                editCaseInvHandler.getCaseObject().getItems().add(item);
            } else {
                ItemCase item = itemByCaseAndItemStack.get();
                // if list of items contains this slot check the actually item is null
                // null = delete
                // exists = update
                if(itemStack==null) {
                    // remove item
                    editCaseInvHandler.getCaseObject().getItems().remove(item);
                } else {
                    // update item
                    item.setItemStack(itemStack);
                }
            }
        }
         // save
        editCaseInvHandler.getCaseObject().save();
    }


    private ItemStack cleanItem(ItemStack itemStack) {

        if (itemStack == null)
            return null;

        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore != null && !lore.isEmpty()) {
            lore.removeIf(loreLine -> loreLine.contains("× Szansa:") || loreLine.contains("× Shift + Prawy przycisk"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public static String deserializeLore(String serializedLore) {
        return ChatColor.translateAlternateColorCodes('§', serializedLore);
    }

}
