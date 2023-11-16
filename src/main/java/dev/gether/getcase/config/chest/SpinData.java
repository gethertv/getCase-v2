package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class SpinData extends OkaeriConfig {
    private int size;
    private String title;
    private Set<ItemDecoration> itemDecorations;
}