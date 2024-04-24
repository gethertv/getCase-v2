package dev.gether.getcase.lootbox.domain;

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
    private Set<Integer> noAnimationSlots;

    public Animation() {}

    public Animation(AnimationType animationType, Set<Integer> animationSlots, Set<Integer> noAnimationSlots) {
        this.animationType = animationType;
        this.animationSlots = animationSlots;
        this.noAnimationSlots = noAnimationSlots;
    }
}
