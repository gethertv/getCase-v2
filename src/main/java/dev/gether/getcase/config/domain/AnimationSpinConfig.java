package dev.gether.getcase.config.domain;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimationSpinConfig extends GetConfig {


    @Comment("Delay in ticks after the winning item is selected. 20 ticks = 1 second")
    long DELAY_AFTER_FINISH = 20L;

    @Comment("Maximum number of ticks the animation will run")
    int MAX_TICKS = 99;

    @Comment("Tick at which the animation speed changes")
    int SPEED_CHANGE_TICK = 86;

    @Comment("Speed multiplier applied after SPEED_CHANGE_TICK is reached")
    double SPEED_MULTIPLIER_AFTER_86 = 1.6;

    @Comment("Speed multiplier applied on each tick before SPEED_CHANGE_TICK")
    double SPEED_MULTIPLIER = 1.01;



}

