package dev.gether.getcase.config.domain;

import dev.gether.getcase.config.domain.chest.PreviewWinItem;
import dev.gether.getcase.config.domain.chest.SpinData;
import dev.gether.getutils.GetConfig;
import dev.gether.getutils.annotation.Comment;
import dev.gether.getutils.builder.ItemBuilder;
import dev.gether.getutils.models.inventory.DynamicItem;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

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
            .animationSlots(new int[] {11,12,13,14,15})
            .itemDecorations(Set.of(
                    DynamicItem.builder()
                            .itemStack(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name("&7").build())
                            .slots(List.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26))
                            .build()
            ))
            .build();

    // preview win item
    private PreviewWinItem previewWinItem = PreviewWinItem.builder()
            .title("&0You won...")
            .size(27)
            .itemDecorations(
                    Set.of(
                            DynamicItem.builder()
                                    .itemStack(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name("&7").build())
                                    .slots(List.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26))
                                    .build()
                    )
            )
            .slotWinItem(13)
            .animationSlots(Set.of(16))
            .noAnimationSlots(Set.of(15))
            .build();

    private ItemStack noAnimationItem = ItemBuilder
            .of(Material.LIME_DYE)
            .name("&7Open without the animation")
            .lore(new ArrayList<>())
            .glow(true)
            .build();

    private ItemStack animationItem = ItemBuilder
            .of(Material.PURPLE_DYE)
            .name("&7Open with spin animation")
            .lore(new ArrayList<>())
            .glow(true)
            .build();


}
