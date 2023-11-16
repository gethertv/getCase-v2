package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
// preview inventory settings
public class PreviewWinItem extends OkaeriConfig {
    private int size;
    private String title;
    private Set<ItemDecoration> itemDecorations;
    private Set<Integer> animationSlots;
    private Set<Integer> noAnimationSlots;
    private int slotWinItem;
}

