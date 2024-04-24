package dev.gether.getcase.config.domain;

import dev.gether.getcase.config.domain.chest.CaseHologram;
import lombok.*;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CaseLocation {

    private CaseHologram caseHologram;
    private Location location;
    private UUID caseId;

    public CaseLocation() {}
}
