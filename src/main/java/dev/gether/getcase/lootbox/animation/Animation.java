package dev.gether.getcase.lootbox.animation;

import dev.gether.getcase.lootbox.animation.AnimationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class Animation {

    private AnimationType animationType;

    // animation slots
    private Set<Integer> animationSlots;
    // no animation slots
    private Set<Integer> quickOpenSlots;

    private Set<Integer> multiCaseSlots;

    public Animation() {}

    public Animation(AnimationType animationType, Set<Integer> animationSlots, Set<Integer> quickOpenSlots, Set<Integer> multiCaseSlots) {
        this.animationType = animationType;
        this.animationSlots = animationSlots;
        this.quickOpenSlots = quickOpenSlots;
        this.multiCaseSlots = multiCaseSlots;
    }
}
