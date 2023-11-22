package dev.gether.getcase.config;

import dev.gether.getcase.config.chest.CaseHologram;
import lombok.*;
import org.bukkit.Location;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CaseLocation {

    private CaseHologram caseHologram;
    private Location location;
    private UUID caseId;

}
