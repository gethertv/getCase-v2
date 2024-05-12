package dev.gether.getcase.config.domain.chest;

import dev.gether.getconfig.annotation.Comment;
import dev.gether.getconfig.domain.config.ItemDecoration;
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

    private Set<ItemDecoration> itemDecorations;

}