package dev.gether.getcase.config.domain.chest;

import lombok.*;

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

}