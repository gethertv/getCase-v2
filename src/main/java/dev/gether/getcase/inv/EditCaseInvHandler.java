package dev.gether.getcase.inv;

import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.utils.ColorFixer;
import dev.gether.getcase.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EditCaseInventory implements InventoryHolder {

    private final Player player;
    private CaseConfig.Case caseObject;
    private Inventory inventory;

    public EditCaseInventory(Player player, CaseConfig.Case caseObject) {
        this.player = player;
        this.caseObject = caseObject;

        // create edit inv
        inventory = Bukkit.createInventory(this, caseObject.getSizeInv(), "&0Edytowanie "+caseObject.getName());

        // fill with items
        fillInvByItems();
        
        // open inv
        player.openInventory(inventory);
    }

    private void fillInvByItems() {
        // fill items in case
        caseObject.getItems().forEach(item -> {
            // clone item and add to lore chance
            ItemStack itemStack = prepareItemWithChance(item);
            inventory.setItem(item.getSlot(), itemStack);
        });

        // save button/item
        inventory.setItem(inventory.getSize()-1, ItemBuilder.create(Material.LIME_DYE, "&aZapisz", true));
    }

    private ItemStack prepareItemWithChance(CaseConfig.Item item) {
        ItemStack itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(itemMeta.getLore());
        lore.add("&7");
        lore.add("&7Szansa: "+item.getChance());
        lore.add("&7");
        itemMeta.setLore(ColorFixer.addColors(lore));
        return itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
