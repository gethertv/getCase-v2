package dev.gether.getcase.config;

import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.config.chest.ItemDecoration;
import dev.gether.getcase.config.chest.PreviewWinItem;
import dev.gether.getcase.config.chest.SpinData;
import dev.gether.getcase.utils.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class CaseConfig extends OkaeriConfig {

    @Comment("dzwiek gdy nie posiadasz kluczyka")
    @Comment("lista dzwiekow https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html")
    private Sound noKeySound = Sound.ENTITY_VILLAGER_NO;
    @Comment("dzwiek gdy otwierasz podglad skrzynki")
    private Sound previewCaseSound = Sound.UI_BUTTON_CLICK;

    // drawing inventory data
    private SpinData spinData = SpinData.builder()
            .size(27)
            .title("Losowanie...")
            .itemDecorations(Set.of(
                    ItemDecoration.builder()
                            .itemStack(ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, "&7", false))
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
                                    .itemStack(ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, "&7", false))
                                    .slots(Set.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26))
                                    .build()
                    )
            )
            .slotWinItem(13)
            .animationSlots(Set.of(16))
            .noAnimationSlots(Set.of(15))
            .build();
    private ItemStack noAnimationItem = ItemBuilder.create(Material.LIME_DYE, "&7Otworz bez animacji", true);
    private ItemStack animationItem = ItemBuilder.create(Material.PURPLE_DYE, "&7Otworz z animacji", true);
    // set of case
    private Set<CaseObject> caseData = new HashSet<>();


}
