package dev.gether.getcase.config.domain.inv.spinning;

import dev.gether.getcase.config.domain.inv.preview.MultiCaseWinItemsPreviewConfig;
import dev.gether.getutils.GetConfig;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.models.inventory.InventoryConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiCaseSpinningInvConfig extends GetConfig {

    InventoryConfig multiOpenCaseInv = InventoryConfig.builder()
            .title("&0Multi-Case Opening")
            .cancelClicks(true)
            .size(54)
            .refreshInterval(0)
            .decorations(new ArrayList<>(Set.of(
                    DynamicItem.builder()
                            .item(Item.builder()
                                    .material(Material.BLACK_STAINED_GLASS_PANE)
                                    .name("&7")
                                    .build())
                            .slots(new ArrayList<>(List.of(0,8)))
                            .build()
            )))
            .build();


    @Getter
    Map<Integer, MultiCaseWinItemsPreviewConfig.AnimationRow> animationRows = new HashMap<>(Map.of(
            1, new MultiCaseWinItemsPreviewConfig.AnimationRow(new int[] {1,2,3,4,5,6,7}, 4),
            2, new MultiCaseWinItemsPreviewConfig.AnimationRow(new int[] {10,11,12,13,14,15,16}, 13),
            3, new MultiCaseWinItemsPreviewConfig.AnimationRow(new int[] {19,20,21,22,23,24,25}, 22),
            4, new MultiCaseWinItemsPreviewConfig.AnimationRow(new int[] {28,29,30,31,32,33,34}, 31),
            5, new MultiCaseWinItemsPreviewConfig.AnimationRow(new int[] {37,38,39,40,41,42,43}, 40),
            6, new MultiCaseWinItemsPreviewConfig.AnimationRow(new int[] {46,47,48,49,50,51,52}, 49)
    ));

}
