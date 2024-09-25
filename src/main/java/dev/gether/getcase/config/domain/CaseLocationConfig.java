package dev.gether.getcase.config.domain;

import dev.gether.getutils.GetConfig;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseLocationConfig extends GetConfig {

    private Set<CaseLocation> caseLocationData = new HashSet<>();


}
