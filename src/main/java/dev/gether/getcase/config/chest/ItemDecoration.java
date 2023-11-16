package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@Getter
@Setter
@Builder
public class ItemDecoration extends OkaeriConfig {
    private Set<Integer> slots;
    private ItemStack itemStack;

    public ItemDecoration(Set<Integer> slots, ItemStack itemStack) {
        this.slots = slots;
        this.itemStack = itemStack;
    }
}
