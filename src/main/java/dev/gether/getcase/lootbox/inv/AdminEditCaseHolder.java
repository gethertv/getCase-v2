package dev.gether.getcase.lootbox.inv;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.gether.getcase.GetCase;
import dev.gether.getcase.lootbox.model.ItemCase;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import dev.gether.getutils.models.inventory.InventoryConfig;
import dev.gether.getutils.utils.ColorFixer;
import dev.gether.getutils.utils.MessageUtil;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@FieldDefaults(makeFinal = true)
public class AdminEditCaseHolder extends AbstractInventoryHolder {

    @Getter private LootBox lootBox;
    private GetCase plugin;
    @Getter private Set<ItemCase> itemCases;

    public AdminEditCaseHolder(GetCase plugin, Player player, LootBox lootBox) {
        super(plugin, player, createConfig(lootBox));
        this.lootBox = lootBox;
        this.plugin = plugin;
        this.itemCases = new HashSet<>(lootBox.getItems());
        initializeItems();
    }

    private static InventoryConfig createConfig(LootBox lootBox) {
        return InventoryConfig.builder()
                .title("Edit " + lootBox.getCaseName())
                .size(lootBox.getPreviewInventoryConfig().getSize())
                .decorations(new ArrayList<>())
                .refreshInterval(0)
                .build();
    }

    @JsonSerialize
    public void handleClick(InventoryClickEvent event) {
        fillInventoryWithItems();
        int slot = event.getSlot();
        slotActions.getOrDefault(slot, e -> {}).accept(event);
    }

    @Override
    protected void initializeItems() {
        fillBaseItems();
        addSaveButton();
    }

    private void fillBaseItems() {
        lootBox.getItems().forEach(this::addItemToInventory);
    }

    private void addItemToInventory(ItemCase item) {
        ItemStack itemStack = prepareItemWithChance(item);
        setItem(item.getSlot(), itemStack, event -> {
            if (event.isRightClick() && event.isShiftClick()) {
                plugin.getLootBoxManager().getEditLootBoxManager().editItem(this, item);
            }
        });
        itemCases.add(item);
    }

    private void addSaveButton() {
        Item saveItem = Item.builder()
                .material(Material.LIME_DYE)
                .name("&aSave")
                .glow(true)
                .build();

        setItem(getInventory().getSize() - 1, saveItem.getItemStack(), event -> {
            plugin.getLootBoxManager().getEditLootBoxManager().saveAllItems(this);
            MessageUtil.sendMessage(player, "#a1ff54 Successfully saved!");
            event.setCancelled(true);
        });
    }

    public void fillInventoryWithItems() {
        for (int slot = 0; slot < inventory.getSize() - 1; slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                removeItemFromSlot(slot);
                continue;
            }
            updateOrAddItem(slot, itemStack);
        }
    }

    private void removeItemFromSlot(int slot) {
        itemCases.removeIf(itemCase -> itemCase.getSlot() == slot);
        slotActions.remove(slot);
    }

    private void updateOrAddItem(int slot, ItemStack itemStack) {
        ItemCase itemCase = createOrUpdateItemCase(slot, itemStack);
        ItemStack updatedItemStack = prepareItemWithChance(itemCase);
        setItem(slot, updatedItemStack, event -> {
            if (event.isRightClick() && event.isShiftClick()) {
                plugin.getLootBoxManager().getEditLootBoxManager().editItem(this, itemCase);
            }
        });
    }

    private ItemCase createOrUpdateItemCase(int slot, ItemStack itemStack) {
        Optional<ItemCase> existingItemCase = findExistingItemCase(slot, itemStack);
        double chance = existingItemCase.map(ItemCase::getChance).orElse(0.0);

        ItemCase newItemCase = ItemCase.builder()
                .slot(slot)
                .chance(chance)
                .itemStack(itemStack)
                .extraLore(List.of("&7", "&7Chance: &f{chance}%", "&7"))
                .build();

        itemCases.removeIf(iCase -> iCase.getSlot() == slot);
        itemCases.add(newItemCase);

        return newItemCase;
    }

    private Optional<ItemCase> findExistingItemCase(int slot, ItemStack itemStack) {
        return itemCases.stream()
                .filter(iCase -> plugin.getLootBoxManager().getEditLootBoxManager().cleanItem(itemStack).isSimilar(iCase.getItemStack()) && iCase.getSlot() == slot)
                .findFirst();
    }

    private ItemStack prepareItemWithChance(ItemCase item) {
        ItemStack itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = Optional.ofNullable(itemMeta.getLore()).orElse(new ArrayList<>());

        lore.removeIf(loreLine -> loreLine.contains("× Chance:") || loreLine.contains("× Shift + Right click"));
        lore.add("&c× Chance: &f" + item.getChance());
        lore.add("&c× Shift + Right click &f- Edit drop");

        itemMeta.setLore(ColorFixer.addColors(lore));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}