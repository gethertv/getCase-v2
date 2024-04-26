package dev.gether.getcase.config.domain.chest;

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

}