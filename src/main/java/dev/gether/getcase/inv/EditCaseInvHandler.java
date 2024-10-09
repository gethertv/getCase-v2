package dev.gether.getcase.inv;

import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EditCaseInvHandler implements InventoryHolder {

    private final Player player;
    private final LootBox lootBox;
    private final Inventory inventory;

    public EditCaseInvHandler(Player player, LootBox lootBox) {
        this.player = player;
        this.lootBox = lootBox;

        // create edit inv
        inventory = Bukkit.createInventory(
                this,
                lootBox.getSizeInv(),
                ColorFixer.addColors("&0Edit "+ lootBox.getName()));

        // fill with items
        fillInvByItems();

    }

    public void fillInvByItems() {
        // fill items in case
        lootBox.getItems().forEach(item -> {
            // clone item and add to lore chance
            ItemStack itemStack = prepareItemWithChance(item);
            inventory.setItem(item.getSlot(), itemStack);
        });

        inventory.setItem(inventory.getSize()-1, ItemBuilder.of(Material.LIME_DYE).name("&aSave").build());
        // save button/item
    }

    private ItemStack prepareItemWithChance(ItemCase item) {
        ItemStack itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> tempLore = itemMeta.getLore();
        List<String> lore = new ArrayList<>();
        if(tempLore!=null)
            lore.addAll(tempLore);

        lore.add("&c× Chance: &f"+item.getChance());
        lore.add("&c× Shift + Right click &f- Edit drop");
        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    public LootBox getCaseObject() {
        return lootBox;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
