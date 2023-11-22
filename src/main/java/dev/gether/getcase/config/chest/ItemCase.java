package dev.gether.getcase.config.chest;

import lombok.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemCase {
    private int slot;
    private double chance;
    private ItemStack itemStack;
    private List<String> extraLore;

}