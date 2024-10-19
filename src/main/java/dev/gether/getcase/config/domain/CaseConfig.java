package dev.gether.getcase.config.domain;

import dev.gether.getutils.GetConfig;
import dev.gether.getutils.annotation.Comment;
import dev.gether.getutils.models.Item;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CaseConfig extends GetConfig {

    @Comment({"Sound when player doesn't have a key",
            "List of sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html"})
    private Sound noKeySound = Sound.ENTITY_VILLAGER_NO;

    @Comment("Sound when opening a preview menu")
    private Sound previewMenuSound = Sound.UI_BUTTON_CLICK;

    @Comment("Sound during spin animation")
    private Sound spinAnimationSound = Sound.UI_BUTTON_CLICK;

    @Comment("Sound when receiving the reward")
    private Sound rewardSound = Sound.ENTITY_PLAYER_LEVELUP;

    @Comment("Enables opening all loot boxes with SHIFT + RIGHT_CLICK")
    private boolean quickOpenEnabled = false;

    @Comment("multi-case opening")
    private boolean multiCaseEnabled = true;

    @Comment("Use gradual key distribution to reduce lag (true) or distribute all keys at once (false)")
    private boolean useGradualKeyDistribution = true;

    @Comment("Number of players to receive keys per tick during gradual distribution")
    private int playersPerTickForKeyDistribution = 5;

    private Item multiCaseOpening = Item.builder()
            .material(Material.CHEST)
            .name("&7Multi-case Opening")
            .lore(new ArrayList<>())
            .glow(true)
            .build();

    private Item quickOpenItem = Item.builder()
            .material(Material.LIME_DYE)
            .name("&7Open without animation")
            .lore(new ArrayList<>())
            .glow(true)
            .build();

    private Item animatedOpenItem = Item.builder()
            .material(Material.PURPLE_DYE)
            .name("&7Open with spin animation")
            .lore(new ArrayList<>())
            .glow(true)
            .build();



}