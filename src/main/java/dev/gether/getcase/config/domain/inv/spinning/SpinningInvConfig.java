package dev.gether.getcase.config.domain.inv.spinning;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.models.inventory.InventoryConfig;
import lombok.*;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpinningInvConfig extends GetConfig {

    InventoryConfig drawingInv = InventoryConfig.builder()
            .decorations(List.of(
                    DynamicItem.builder()
                            .item(Item.builder().material(Material.BLACK_STAINED_GLASS_PANE).name("&7").build())
                            .slots(List.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26))
                            .build()
            ))
            .refreshInterval(0)
            .cancelClicks(true)
            .size(27)
            .title("&0Drawing...")
            .build();

    int[] animationSlots = new int[] {11,12,13,14,15};
    int winningSlot = 13;


}
