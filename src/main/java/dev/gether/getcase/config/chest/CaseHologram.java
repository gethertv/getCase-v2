package dev.gether.getcase.config.chest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gether.getcase.GetCase;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.*;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CaseHologram {
    @JsonIgnore
    private Hologram hologram;
    private boolean enable;
    private List<String> lines;
    private double heightY;

    @JsonIgnore
    public void createHologram(String caseName, Location location) {

        // check hook decent holograms
        if(!GetCase.getInstance().getHookManager().isDecentHologramsEnable())
            return;
        // hologram is enable
        if(!enable)
            return;


        // create hologram
        hologram = DHAPI.createHologram(
                "case_" + caseName.toLowerCase() + UUID.randomUUID(),
                location.clone().add(0.5, heightY, 0.5),
                lines);
    }

    @JsonIgnore
    public void deleteHologram() {
        if(hologram != null) {
            hologram.destroy();
        }
    }


}