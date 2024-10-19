package dev.gether.getcase.config.domain;

import dev.gether.getcase.config.domain.inv.preview.WinItemPreviewConfig;
import dev.gether.getutils.GetConfig;
import dev.gether.getutils.annotation.Comment;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.models.inventory.InventoryConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiCaseOpeningConfig extends GetConfig {

    InventoryConfig inventorySelection = InventoryConfig.builder()
            .title("&0selection multi-case")
            .cancelClicks(true)
            .size(27)
            .refreshInterval(0)
            .decorations(new ArrayList<>())
            .build();

    Map<Integer, DynamicItem> selectionCase = new HashMap<>(Map.of(
            1, DynamicItem.builder()
                    .item(Item.builder()
                            .material(Material.CHEST)
                            .amount(1)
                            .lore(new ArrayList<>())
                            .name("Open 1")
                            .build())
                            .slots(new ArrayList<>(List.of(10)))
                    .build(),
            2, DynamicItem.builder()
                    .item(Item.builder()
                            .material(Material.CHEST)
                            .amount(1)
                            .lore(new ArrayList<>())
                            .name("Open 2")
                            .build())
                            .slots(new ArrayList<>(List.of(11)))
                    .build(),
            3, DynamicItem.builder()
                    .item(Item.builder()
                            .material(Material.CHEST)
                            .amount(1)
                            .lore(new ArrayList<>())
                            .name("Open 3")
                            .build())
                            .slots(new ArrayList<>(List.of(12)))
                    .build(),
            4, DynamicItem.builder()
                    .item(Item.builder()
                            .material(Material.CHEST)
                            .amount(1)
                            .lore(new ArrayList<>())
                            .name("Open 4")
                            .build())
                            .slots(new ArrayList<>(List.of(14)))
                    .build(),
            5, DynamicItem.builder()
                    .item(Item.builder()
                            .material(Material.CHEST)
                            .amount(1)
                            .lore(new ArrayList<>())
                            .name("Open 5")
                            .build())
                            .slots(new ArrayList<>(List.of(15)))
                    .build(),
            6, DynamicItem.builder()
                    .item(Item.builder()
                            .material(Material.CHEST)
                            .amount(1)
                            .lore(new ArrayList<>())
                            .name("Open 6")
                            .build())
                            .slots(new ArrayList<>(List.of(16)))
                    .build()
    ));


}
