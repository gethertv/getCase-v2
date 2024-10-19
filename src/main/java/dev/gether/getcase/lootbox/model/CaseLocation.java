package dev.gether.getcase.lootbox.model;

import dev.gether.getutils.GetConfig;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseLocation extends GetConfig {

    UUID caseId;
    List<CaseHologram> caseHolograms = new ArrayList<>();

    public CaseLocation(UUID caseId) {
        this.caseId = caseId;
        caseHolograms = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CaseHologram {

        boolean enable;
        Location location;
        String hologramKey;
        List<String> lines;
        double heightY;

    }
}
