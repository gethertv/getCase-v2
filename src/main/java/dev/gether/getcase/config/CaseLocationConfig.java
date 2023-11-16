package dev.gether.getcase.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CaseLocationConfig extends OkaeriConfig {

    private Set<CaseLocation> caseLocationData = new HashSet<>();

    @Getter
    @Setter
    @Builder
    public static class CaseLocation extends OkaeriConfig {
        private Location location;
        private UUID caseId;

    }


}
