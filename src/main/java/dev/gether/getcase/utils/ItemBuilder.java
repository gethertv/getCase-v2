package dev.gether.getcase.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    private ItemBuilder() {
        throw new IllegalStateException("Utility class");
    }
    public static ItemStack create(Material material, String name, boolean glow) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(name));

        if(glow) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);

            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        return itemStack;
    }
}
