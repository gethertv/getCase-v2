package dev.gether.getcase.lootbox.edit;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.lootbox.model.ItemCase;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.inv.AdminEditCaseHolder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EditLootBoxManager {

    String CHANCE_TITLE = "Chance";
    String DEFAULT_CHANCE = "0.00";
    String CHANCE_LORE_PREFIX = "× Chance:";
    String EDIT_LORE = "× Shift + Right click";

    private final LootBoxManager lootBoxManager;
    private final GetCase plugin;

    public EditLootBoxManager(GetCase plugin, LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
        this.plugin = plugin;
    }

    public void editCase(Player player, LootBox lootBox) {
        new AdminEditCaseHolder(plugin, player, lootBox).open();
    }

    public void editItem(AdminEditCaseHolder editCaseInv, ItemCase itemCase) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> handleAnvilClick(slot, stateSnapshot, itemCase, editCaseInv))
                .title(CHANCE_TITLE)
                .text(String.valueOf(itemCase.getChance()))
                .itemLeft(new ItemStack(Material.PAPER))
                .plugin(plugin)
                .open(editCaseInv.getPlayer());
    }

    private List<AnvilGUI.ResponseAction> handleAnvilClick(int slot, AnvilGUI.StateSnapshot stateSnapshot,
                                                           ItemCase itemCase, AdminEditCaseHolder editCaseInv) {
        if (slot != AnvilGUI.Slot.OUTPUT) {
            return Collections.emptyList();
        }

        String text = stateSnapshot.getText();
        if (!isValidChance(text)) {
            return List.of(AnvilGUI.ResponseAction.replaceInputText(DEFAULT_CHANCE));
        }

        return List.of(
                AnvilGUI.ResponseAction.close(),
                AnvilGUI.ResponseAction.run(() -> {
                    updateItemChance(itemCase, text);
                    refreshEditInv(editCaseInv);
                })
        );
    }

    private boolean isValidChance(String input) {
        try {
            return Double.parseDouble(input) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateItemChance(ItemCase itemCase, String chanceText) {
        itemCase.setChance(Double.parseDouble(chanceText));
    }

    private void refreshEditInv(AdminEditCaseHolder editCaseInv) {
        editCaseInv.fillInventoryWithItems();
        editCaseInv.getPlayer().openInventory(editCaseInv.getInventory());
    }

    public void saveAllItems(AdminEditCaseHolder editCaseInv) {
        LootBox lootBox = editCaseInv.getLootBox();
        lootBox.getItems().clear();
        editCaseInv.getItemCases().stream()
                .map(this::cleanItemCase)
                .forEach(lootBox.getItems()::add);
        lootBox.save();
        editCaseInv.getPlayer().closeInventory();
    }

    private ItemCase cleanItemCase(ItemCase itemCase) {
        itemCase.setItemStack(cleanItem(itemCase.getItemStack()));
        return itemCase;
    }

    public ItemStack cleanItem(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            lore.removeIf(this::isRemovableLoreLine);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private boolean isRemovableLoreLine(String loreLine) {
        return loreLine.contains(CHANCE_LORE_PREFIX) || loreLine.contains(EDIT_LORE);
    }
}