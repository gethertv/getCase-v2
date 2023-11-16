package dev.gether.getcase.config.chest;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BroadcastCase extends OkaeriConfig {
    private boolean enable;
    private List<String> messages;

    public BroadcastCase(boolean enable, List<String> messages) {
        this.enable = enable;
        this.messages = messages;
    }
}
