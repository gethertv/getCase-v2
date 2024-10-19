package dev.gether.getcase.lootbox.model;

import lombok.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCase {

    private int slot;
    private double chance;
    private ItemStack itemStack;
    private List<String> extraLore;
    private boolean needUpdate;


    public void setChance(double chance) {
        this.chance = chance;
        this.needUpdate = true;
    }
}