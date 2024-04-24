package dev.gether.getcase.config.domain;

import dev.gether.getconfig.GetConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LangConfig extends GetConfig {
    private String noKey = "&cNie posiadasz klucza!";
    private String caseIsDisable = "&cTa skrzynia jest wyłączona!";
}
