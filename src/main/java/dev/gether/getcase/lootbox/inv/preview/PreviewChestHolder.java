package dev.gether.getcase.lootbox.inv.preview;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.model.ItemCase;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getutils.Valid;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import dev.gether.getutils.utils.ColorFixer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PreviewChestHolder extends AbstractInventoryHolder {

    LootBoxManager lootBoxManager;
    LootBox lootBox;
    FileManager fileManager;

    public PreviewChestHolder(Plugin plugin, Player player, LootBoxManager lootBoxManager, LootBox lootBox, FileManager fileManager) {
        super(plugin, player, lootBox.getPreviewInventoryConfig());
        this.lootBoxManager = lootBoxManager;
        this.lootBox = lootBox;
        this.fileManager = fileManager;

        initializeItems();
    }

    @Override
    protected void initializeItems() {
        fillItemCase();
        fillAnimationItems();
    }

    private void fillItemCase() {
        if (lootBox.getItems().isEmpty()) return;

        Map<ItemCase, Double> adjustedChances = calculateAdjustedChances();
        adjustedChances.forEach((item, chance) -> setItem(item.getSlot(), addExtraLore(item, String.format("%.2f", chance))));
    }

    private Map<ItemCase, Double> calculateAdjustedChances() {
        Set<ItemCase> items = lootBox.getItems();
        double totalWeight = items.stream().mapToDouble(ItemCase::getChance).sum();
        double remainingWeight = 100.00;

        Map<ItemCase, Double> adjustedChances = new HashMap<>();

        // correlation chance
        for (ItemCase item : items) {
            double chance = (item.getChance() / totalWeight) * 100;
            double roundedChance = Math.round(chance * 100.0) / 100.0;
            remainingWeight -= roundedChance;
            adjustedChances.put(item, roundedChance);
        }

        // lowest chance
        ItemCase lowestChanceItem = adjustedChances.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No items found in loot box"));

        double finalRemainingWeight = remainingWeight;
        adjustedChances.compute(lowestChanceItem, (item, chance) -> chance + finalRemainingWeight);

        return adjustedChances;
    }

    public ItemStack addExtraLore(ItemCase item, String chance) {
        Valid.checkNotNull(item.getItemStack(), "ItemStack cannot be null");
        final ItemStack itemStack = item.getItemStack().clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        Valid.checkNotNull(itemMeta, "ItemMeta cannot be null");

        List<String> lore = Optional.ofNullable(itemMeta.getLore()).orElse(new ArrayList<>());
        addFormattedExtraLore(lore, item.getExtraLore(), chance);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void addFormattedExtraLore(List<String> lore, List<String> extraLore, String chance) {
        if (extraLore != null) {
            extraLore.stream()
                    .map(line -> line.replace("{chance}", chance))
                    .map(ColorFixer::addColors)
                    .forEach(lore::add);
        }
    }

    private void fillAnimationItems() {
        if (lootBox.getLootboxType() == LootboxType.LUCKY_BLOCK)
            return;

        // multi-case opening
        if(fileManager.getCaseConfig().isMultiCaseEnabled()) {
            for (Integer slot : lootBox.getAnimation().getMultiCaseSlots()) {
                setItem(slot, fileManager.getCaseConfig().getMultiCaseOpening().getItemStack(), event -> lootBoxManager.openMultiCaseInv(player, lootBox));
            }
        }


        // animation
        for (Integer slot : lootBox.getAnimation().getAnimationSlots()) {
            // set animation item
            setItem(slot, fileManager.getCaseConfig().getAnimatedOpenItem().getItemStack(), event -> lootBoxManager.openCase(player, lootBox, AnimationType.SPIN));
        }
        // quick open
        for (Integer slot : lootBox.getAnimation().getQuickOpenSlots()) {
            setItem(slot, fileManager.getCaseConfig().getQuickOpenItem().getItemStack(), event -> lootBoxManager.openCase(player, lootBox, AnimationType.QUICK));
        }
    }
}
