package dev.gether.getcase.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {


    public static String getItemName(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return itemStack.getType().name();
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta.hasDisplayName() ? meta.getDisplayName() : itemStack.getType().name();
    }
}