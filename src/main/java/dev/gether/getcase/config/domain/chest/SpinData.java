package dev.gether.getcase.config.domain.chest;

import dev.gether.getutils.annotation.Comment;
import dev.gether.getutils.models.inventory.DynamicItem;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpinData {

    private int size;
    private String title;
    @Comment("slots where the item will rolls")
    private int[] animationSlots;

    private Set<DynamicItem> itemDecorations;

}