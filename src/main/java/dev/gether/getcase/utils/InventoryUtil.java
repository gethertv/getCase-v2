package dev.gether.getcase.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    private InventoryUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static void giveItem(Player player, ItemStack itemStack) {
        if(isInventoryFull(player)) {
            Location location = player.getLocation();
            location.getWorld().dropItemNaturally(location, itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }
    }

    private static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
}
