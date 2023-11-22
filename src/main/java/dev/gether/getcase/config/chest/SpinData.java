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
public class SpinData extends GetConfig {
    private int size;
    private String title;
    private Set<ItemDecoration> itemDecorations;
}