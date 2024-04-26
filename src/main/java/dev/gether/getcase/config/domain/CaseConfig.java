package dev.gether.getcase.config.domain;

import dev.gether.getcase.config.domain.chest.PreviewWinItem;
import dev.gether.getcase.config.domain.chest.SpinData;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.annotation.Comment;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemDecoration;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CaseConfig extends GetConfig {

    @Comment({"sound when you don't have a key",
            "list of sound https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html"})
    private Sound noKeySound = Sound.ENTITY_VILLAGER_NO;
    @Comment("sound when you open a preview menu")
    private Sound previewCaseSound = Sound.UI_BUTTON_CLICK;
    @Comment("sound while spin animation")
    private Sound spinSound = Sound.UI_BUTTON_CLICK;
    @Comment("sound when you got the reward")
    private Sound winItemSound = Sound.ENTITY_PLAYER_LEVELUP;

    @Comment("it's responsible for open all loot box by SHIFT + RIGHT_CLICK")
    private boolean quickOpenCase = false;
    // drawing inventory data
    private SpinData spinData = SpinData.builder()
            .size(27)
            .title("Drawing...")
            .itemDecorations(Set.of(
                    ItemDecoration.builder()
                            .item(Item.builder()
                                    .material(Material.BLACK_STAINED_GLASS_PANE)
                                    .displayname(" ")
                                    .lore(new ArrayList<>())
                                    .build())
                            .slots(Set.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26))
                            .build()
            ))
            .build();

    // preview win item
    private PreviewWinItem previewWinItem = PreviewWinItem.builder()
            .title("&0You won...")
            .size(27)
            .itemDecorations(
                    Set.of(
                            ItemDecoration.builder()
                                    .item(Item.builder()
                                            .material(Material.BLACK_STAINED_GLASS_PANE)
                                            .displayname(" ")
                                            .lore(new ArrayList<>())
                                            .build())
                                    .slots(Set.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26))
                                    .build()
                    )
            )
            .slotWinItem(13)
            .animationSlots(Set.of(16))
            .noAnimationSlots(Set.of(15))
            .build();
    private Item noAnimationItem = Item.builder()
            .material(Material.LIME_DYE)
            .displayname("&7Open without the animation")
            .lore(new ArrayList<>(List.of("&7")))
            .unbreakable(true)
            .glow(true)
            .build();

    private Item animationItem = Item.builder()
            .material(Material.PURPLE_DYE)
            .displayname("&7Open with spin animation")
            .lore(new ArrayList<>(List.of("&7")))
            .unbreakable(true)
            .glow(false)
            .build();


}
