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

    @Comment({"dzwiek gdy nie posiadasz kluczyka",
            "lista dzwiekow https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html"})
    private Sound noKeySound = Sound.ENTITY_VILLAGER_NO;
    @Comment("dzwiek gdy otwierasz podglad skrzynki")
    private Sound previewCaseSound = Sound.UI_BUTTON_CLICK;
    @Comment("dzwiek podczas przesuwania animacji")
    private Sound spinSound = Sound.UI_BUTTON_CLICK;
    @Comment("dzwiek przy otrzymanej nagrodzie")
    private Sound winItemSound = Sound.ENTITY_PLAYER_LEVELUP;

    // drawing inventory data
    private SpinData spinData = SpinData.builder()
            .size(27)
            .title("Losowanie...")
            .itemDecorations(Set.of(
                    ItemDecoration.builder()
                            .item(Item.builder()
                                    .material(Material.BLACK_STAINED_GLASS_PANE)
                                    .build())
                            .slots(Set.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26))
                            .build()
            ))
            .build();

    // preview win item
    private PreviewWinItem previewWinItem = PreviewWinItem.builder()
            .title("&0Wygrales...")
            .size(27)
            .itemDecorations(
                    Set.of(
                            ItemDecoration.builder()
                                    .item(Item.builder()
                                            .material(Material.BLACK_STAINED_GLASS_PANE)
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
            .displayname("&7Otworz bez animacji")
            .unbreakable(true)
            .glow(true)
            .build();

    private Item animationItem = Item.builder()
            .material(Material.PURPLE_DYE)
            .displayname("&7Otworz z animacji")
            .lore(new ArrayList<>(List.of("test")))
            .unbreakable(true)
            .glow(false)
            .build();



}
