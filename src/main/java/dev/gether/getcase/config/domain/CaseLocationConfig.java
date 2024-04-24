package dev.gether.getcase.config;

import dev.gether.getconfig.GetConfig;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CaseLocationConfig extends GetConfig {

    private Set<CaseLocation> caseLocationData = new HashSet<>();

}
