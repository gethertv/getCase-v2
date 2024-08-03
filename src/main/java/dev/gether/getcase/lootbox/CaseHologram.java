package dev.gether.getcase.lootbox;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseHologram {
    private Object hologram;
    private UUID caseUUID;

}
