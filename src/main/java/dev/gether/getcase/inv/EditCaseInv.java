package dev.gether.getcase.inv;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getutils.builder.ItemBuilder;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import dev.gether.getutils.models.inventory.InventoryConfig;
import dev.gether.getutils.utils.ColorFixer;
import dev.gether.getutils.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EditCaseInv extends AbstractInventoryHolder {

    @Getter LootBox lootBox;
    GetCase plugin;
    @Getter Set<ItemCase> itemCase;

    public EditCaseInv(GetCase plugin, Player player, LootBox lootBox) {
        super(plugin, player, createConfig(lootBox));
        this.lootBox = lootBox;
        this.plugin = plugin;

        itemCase = new HashSet<>(lootBox.getItems());
        initializeItems();
    }

    private static InventoryConfig createConfig(LootBox lootBox) {
        return InventoryConfig.builder()
                .title("Edit " + lootBox.getName())
                .size(lootBox.getSizeInv())
                .refreshInterval(0)
                .build();
    }

    @JsonSerialize
    public void handleClick(InventoryClickEvent event) {
        fillInvByItems();

        int slot = event.getSlot();
        if (slotActions.containsKey(slot)) {
            slotActions.get(slot).accept(event);
        }
    }

    @Override
    protected void initializeItems() {
        refreshBaseItem();

        // Save button/item
        ItemStack saveItem = ItemBuilder.of(Material.LIME_DYE)
                .name("&aSave")
                .glow(true)
                .build();

        setItem(getInventory().getSize() - 1, saveItem, event -> {
            // Handle save action
            plugin.getLootBoxManager().getEditLootBoxManager().saveAllItems(this);
            MessageUtil.sendMessage(player, "#a1ff54 Successfully saved!");
            event.setCancelled(true);
        });
    }

    public void fillInvByItems() {
        refreshBaseItem();
        for (int i = 0; i < inventory.getSize() - 1; i++) {
            int slot = i;
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                itemCase.removeIf(itemCase -> itemCase.getSlot() == slot);
                continue;
            }
            itemCase.add(ItemCase.builder()
                    .slot(i)
                    .chance(0)
                    .itemStack(itemStack)
                    .extraLore(List.of("&7", "&7Chance: &f{chance}%", "&7"))
                    .build()
            );
        }
        refreshBaseItem();

    }

    private void refreshBaseItem() {
        itemCase.forEach(item -> {
            ItemStack itemStack = prepareItemWithChance(item);
            setItem(item.getSlot(), itemStack, event -> {
                if(event.isRightClick() && event.isShiftClick()) {
                    plugin.getLootBoxManager().getEditLootBoxManager().editItem(this, item);
                }
            });
        });
    }


    private ItemStack prepareItemWithChance(ItemCase item) {
        ItemStack itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> tempLore = itemMeta.getLore();
        List<String> lore = new ArrayList<>();
        if (tempLore != null) {
            lore.addAll(tempLore);
        }
        lore.removeIf(loreLine -> loreLine.contains("× Chance:") || loreLine.contains("× Shift + Right click"));
        lore.add("&c× Chance: &f" + item.getChance());
        lore.add("&c× Shift + Right click &f- Edit drop");
        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


}
