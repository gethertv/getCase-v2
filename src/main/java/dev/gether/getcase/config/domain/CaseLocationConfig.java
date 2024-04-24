package dev.gether.getcase.config.domain;

import dev.gether.getconfig.GetConfig;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CaseLocationConfig extends GetConfig {

    private Set<CaseLocation> caseLocationData = new HashSet<>();

    public CaseLocationConfig() {}

}
