package dev.gether.getcase.config.domain.inv.preview;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.models.inventory.InventoryConfig;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WinItemPreviewConfig extends GetConfig {

    InventoryConfig previewInvConfig = InventoryConfig.builder()
            .title("&0Preview Win")
            .size(27)
            .cancelClicks(true)
            .decorations(new ArrayList<>(List.of(
                    DynamicItem.builder()
                            .item(Item.builder().material(Material.BLACK_STAINED_GLASS_PANE).name("&7").build())
                            .slots(List.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26))
                            .build()
            )))
            .build();

    Set<Integer> animationSlots = new HashSet<>(List.of(16));
    Set<Integer> quickOpenSlots = new HashSet<>(List.of(15));


    int winnerItemSlot = 12;



}

