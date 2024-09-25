package dev.gether.getcase.config.domain.chest;

import dev.gether.getutils.models.inventory.DynamicItem;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// preview inventory settings
public class PreviewWinItem {

    private int size;
    private String title;
    private Set<DynamicItem> itemDecorations;
    private Set<Integer> animationSlots;
    private Set<Integer> noAnimationSlots;
    private int slotWinItem;


}

