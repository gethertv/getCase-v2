package dev.gether.getcase.config.domain.chest;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.lootbox.animation.Animation;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getutils.GetConfig;
import dev.gether.getutils.models.inventory.DynamicItem;
import dev.gether.getutils.shaded.jackson.annotation.JsonIgnore;
import dev.gether.getutils.utils.ColorFixer;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

// class represent object CASE
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LootBox extends GetConfig {

    @JsonIgnore
    @Getter(AccessLevel.NONE) // Lombok nie wygeneruje gettera
    @Setter(AccessLevel.NONE) // Lombok nie wygeneruje settera
    private transient Inventory inv;

    // type case/lootbox may be a luckblock or normal case
    private LootboxType lootboxType;

    private boolean enable;
    private UUID caseId;
    private int sizeInv;
    private String titleInv;
    private String name;
    // key section
    private ItemStack key;
    // item key
    private Set<ItemCase> items;
    // background decoration
    private Set<DynamicItem> decorations;
    // animation data
    private Animation animation;
    // broadcast
    private BroadcastCase broadcastCase;



    public void createInv() {
        inv = Bukkit.createInventory(null, sizeInv, ColorFixer.addColors(titleInv));
        // fill inv with items
        fillInventory();
    }

    public void fillInventory() {
        // clear inv
        inv.clear();
        // fill [ background items]
        fillBackground();
        // fill [ animation and no animation items ]
        fillAnimationItems();
        // fill [ items case ]
        fillItemCase();
    }

    private void fillItemCase() {
        // ignore if list items is empty
        if(items.isEmpty())
            return;

        // sum chance from all case
        double totalWeight = items.stream().mapToDouble(ItemCase::getChance).sum();
        // 100%
        double remainingWeight = 100.00;

        // adjusted chance
        Map<ItemCase, Double> adjustedChances = new HashMap<>();
        for (ItemCase item : items) {
            double chance = (item.getChance() / totalWeight) * 100;
            double roundedChance = Math.round(chance * 100.0) / 100.0;
            // remove chance from 100% because finally must be 100%
            // so when after rounded  sum of chance sometime is 99.99%
            remainingWeight -= roundedChance;
            adjustedChances.put(item, roundedChance);
        }

        // sorted to lowest chance
        List<Map.Entry<ItemCase, Double>> sortedItems = adjustedChances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        // correct last item chance to fix the sum chance (100%)
        Map.Entry<ItemCase, Double> lastItemEntry = sortedItems.get(sortedItems.size() - 1);
        double lastChance = lastItemEntry.getValue() + remainingWeight;
        adjustedChances.put(lastItemEntry.getKey(), lastChance);

        // foreach and set item to inv
        for (Map.Entry<ItemCase, Double> entry : adjustedChances.entrySet()) {
            ItemCase item = entry.getKey();
            double chance = entry.getValue();
            inv.setItem(item.getSlot(), addExtraLore(item, String.format("%.2f", chance)));
        }

    }

    public ItemStack addExtraLore(ItemCase item, String chance) {
        final ItemStack itemStack = item.getItemStack().clone();
        final ItemMeta itemMeta = Optional.ofNullable(itemStack.getItemMeta()).orElseThrow(() -> new IllegalStateException("ItemMeta cannot be null"));

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
        if(lootboxType == LootboxType.LUCKY_BLOCK)
            return;

        // get instance caseConfig
        CaseConfig caseConfig = GetCase.getInstance().getFileManager().getCaseConfig();
        // animation
        for (Integer slot : animation.getAnimationSlots()) {
            // set animation item
            inv.setItem(slot, caseConfig.getAnimationItem().clone());
        }
        // no animation
        for (Integer slot : animation.getNoAnimationSlots()) {
            // set no animation item
            inv.setItem(slot, caseConfig.getNoAnimationItem().clone());
        }
    }

    private void fillBackground() {
        for (DynamicItem dynamicItem : decorations) {
            ItemStack itemStack = dynamicItem.getItemStack().clone();
            for (Integer slot : dynamicItem.getSlots()) {
                inv.setItem(slot, itemStack);
            }
        }
    }

    @JsonIgnore
    public Inventory getInventory() {
        return inv;
    }

}
