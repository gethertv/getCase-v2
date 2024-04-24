package dev.gether.getcase.config.domain.chest;

import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.config.ItemDecoration;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@Builder
public class SpinData {

    private int size;
    private String title;
    private Set<ItemDecoration> itemDecorations;

    public SpinData() {}
}