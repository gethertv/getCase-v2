package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@Builder
public class Item extends OkaeriConfig {
    private int slot;
    private double chance;
    private ItemStack itemStack;

    public Item(int slot, double chance, ItemStack itemStack) {
        this.slot = slot;
        this.chance = chance;
        this.itemStack = itemStack;
    }
}