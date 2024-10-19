package dev.gether.getcase.config.domain.inv.preview;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.models.inventory.InventoryConfig;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultiCaseWinItemsPreviewConfig extends GetConfig {

    InventoryConfig multiCasePreviewInv = InventoryConfig.builder()
            .title("&0Preview Win")
            .size(36)
            .cancelClicks(true)
            .decorations(new ArrayList<>(List.of(
                    DynamicItem.builder()
                            .item(Item.builder().material(Material.BLACK_STAINED_GLASS_PANE).name("&7").build())
                            .slots(List.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35))
                            .build()
            )))
            .build();

    int[] winnerItemSlots = new int[] {10,11,12,19,20,21};

    Set<Integer> animationSlots = new HashSet<>(List.of(16));
    Set<Integer> quickOpenSlots = new HashSet<>(List.of(15));
    Set<Integer> multiCaseSlots = new HashSet<>(List.of(14));



    @Getter
    @Setter
    @NoArgsConstructor
    public static class AnimationRow {
        int[] slots;
        int winnerSlot;

        public AnimationRow(int[] slots, int winnerSlot) {
            this.slots = slots;
            this.winnerSlot = winnerSlot;
        }
    }


}

