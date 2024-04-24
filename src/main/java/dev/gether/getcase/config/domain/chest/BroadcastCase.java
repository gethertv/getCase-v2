package dev.gether.getcase.config.domain.chest;

import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
public class BroadcastCase {
    private boolean enable;
    private List<String> messages;

    public BroadcastCase() {}
}
