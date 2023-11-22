package dev.gether.getcase.config.chest;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BroadcastCase {
    private boolean enable;
    private List<String> messages;


}
