package dev.gether.getcase.config.domain.chest;

import dev.gether.getconfig.GetConfig;
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
    @Comment("row number where animation work (0-5)")
    private int rowIndex = 1;
    private Set<ItemDecoration> itemDecorations;

}