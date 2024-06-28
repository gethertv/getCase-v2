package dev.gether.getcase.config.domain.chest;

import dev.gether.getcase.lootbox.addons.FancyBillboardType;
import lombok.*;
import org.bukkit.Color;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseHologram {

    private String hologramKey;
    private boolean enable;
    private List<String> lines;
    private double heightY;
    private boolean textShadow;
    private float scale;
    private int visibilityDistance;
    private boolean transparentBackground;
    private Color color;
    private FancyBillboardType fancyBillboardType;

}