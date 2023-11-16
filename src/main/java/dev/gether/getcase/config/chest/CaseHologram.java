package dev.gether.getcase.config.chest;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CaseHologram extends OkaeriConfig {
    private Hologram this$hologram;
    private boolean enable;
    private List<String> lines;
    private double heightY;

    public void createHologram(String caseName, Location location) {
        // create hologram
        this$hologram = DHAPI.createHologram(
                "case_" + caseName.toLowerCase() + UUID.randomUUID(),
                location.clone().add(0.5, heightY, 0.5),
                lines);
    }

    public void deleteHologram() {
        if(this$hologram != null) {
            this$hologram.destroy();
        }
    }


}