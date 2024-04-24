package dev.gether.getcase.config.chest;

import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.config.ItemDecoration;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// preview inventory settings
public class PreviewWinItem extends GetConfig {
    private int size;
    private String title;
    private Set<ItemDecoration> itemDecorations;
    private Set<Integer> animationSlots;
    private Set<Integer> noAnimationSlots;
    private int slotWinItem;
}

