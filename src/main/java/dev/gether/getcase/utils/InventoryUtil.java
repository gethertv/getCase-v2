package dev.gether.getcase.utils;

import org.bukkit.entity.Player;

public class InventoryUtil {
    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
}
