package dev.gether.getcase.lootbox.edit;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.inv.EditCaseInv;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getutils.utils.MessageUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EditLootBoxManager {

    private final LootBoxManager lootBoxManager;
    private final GetCase plugin;

    public EditLootBoxManager(GetCase plugin, LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
        this.plugin = plugin;
    }

    public void editCase(Player player, LootBox lootBox) {
        EditCaseInv editCaseInv = new EditCaseInv(plugin, player, lootBox);
        // open inv
        player.openInventory(editCaseInv.getInventory());
    }


    public void editItem(EditCaseInv editCaseInv, ItemCase itemCase) {

        AnvilGUI.Builder builder = new AnvilGUI.Builder();

        // builder onClick event
        builder.onClick((anvilSlot, stateSnapshot) -> {
            if (anvilSlot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            String text = stateSnapshot.getText();
            if(!isDouble(text)) {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Number"));
            }
            // parse chance from text to double
            double chance = Double.parseDouble(text);
            // set new chance
            itemCase.setChance(chance);
            // [!] IGNORE THIS SAVE
            // because the saving method exists in main GUI/INV with button/item 'SAVE'
            // save items
            //saveCase(editCaseInvHandler.getCaseObject());
            // open preview inv
            return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(() -> refreshEditInv(editCaseInv))
            );
        });
        // title gui
        builder.title("Chance");
        // left item text
        builder.text("0.00");
        // left item
        builder.itemLeft(new ItemStack(Material.PAPER));
        // set instance from main plugin
        builder.plugin(plugin);
        // open anvil inv
        builder.open(editCaseInv.getPlayer());
    }

    // clear the old items
    // fill with new
    // and open the inv
    private void refreshEditInv(EditCaseInv editCaseInv) {
        editCaseInv.fillInvByItems();
        // open the inv
        editCaseInv.getPlayer().openInventory(editCaseInv.getInventory());
    }

    private boolean isDouble(String input) {
        try {
            double chance = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException ignored) {}
        return false;
    }

    public void saveAllItems(EditCaseInv editCaseInv) {
        editCaseInv.getLootBox().getItems().clear();
        editCaseInv.getItemCase().forEach(itemCase -> {
            itemCase.setItemStack(cleanItem(itemCase.getItemStack()));
            editCaseInv.getLootBox().getItems().add(itemCase);
        });
        editCaseInv.getLootBox().save();
        editCaseInv.getPlayer().closeInventory();
    }


    private ItemStack cleanItem(ItemStack itemStack) {

        if (itemStack == null)
            return null;

        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore != null && !lore.isEmpty()) {
            lore.removeIf(loreLine -> loreLine.contains("× Chance:") || loreLine.contains("× Shift + Right click"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }


}
