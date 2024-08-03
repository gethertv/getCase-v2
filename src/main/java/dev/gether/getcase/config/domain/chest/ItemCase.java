package dev.gether.getcase.config.domain.chest;

import dev.gether.getconfig.domain.Item;
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
    private Item item;
    private List<String> commands;

}