package dev.gether.getcase.config.domain.chest;

import dev.gether.getconfig.domain.config.ItemDecoration;
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
    private Set<ItemDecoration> itemDecorations;
    private Set<Integer> animationSlots;
    private Set<Integer> noAnimationSlots;
    private int slotWinItem;


}

