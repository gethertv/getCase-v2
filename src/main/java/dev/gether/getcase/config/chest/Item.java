package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@Builder
public class Item extends OkaeriConfig {
    private int slot;
    private double chance;
    private ItemStack itemStack;
    private List<String> extraLore;

    public Item(int slot, double chance, ItemStack itemStack, List<String> extraLore) {
        this.slot = slot;
        this.chance = chance;
        this.itemStack = itemStack;
        this.extraLore = extraLore;
    }
}