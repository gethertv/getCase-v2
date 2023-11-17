package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Getter
@Setter
@Builder
public class KeySection extends OkaeriConfig {

    private Material material;
    private String displayname;
    private List<String> lore;
    private boolean glow;

}
