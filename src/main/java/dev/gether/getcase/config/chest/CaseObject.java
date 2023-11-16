package dev.gether.getcase.config.chest;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.utils.ColorFixer;
import eu.okaeri.configs.OkaeriConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

// class represent object CASE
@Getter
@Setter
@Builder
public class CaseObject extends OkaeriConfig {

    // this$ - ignore implement okaeri config
    private Inventory this$inv;
    private UUID caseId;
    private int sizeInv;
    private String titleInv;
    private String name;
    // hologram above the case
    private CaseHologram caseHologram;
    // item key
    private ItemStack keyItem;

    // item in case
    private Set<Item> items;
    // background decoration
    private Set<ItemDecoration> decorations;

    // animation slots
    private Set<Integer> animationSlots;
    // no animation slots
    private Set<Integer> noAnimationSlots;

    // broadcast
    private BroadcastCase broadcastCase;

    public void createInv() {
        this$inv = Bukkit.createInventory(null, sizeInv, ColorFixer.addColors(titleInv));

        // fill inv with items
        fillItems();
    }

    public void fillItems() {
        // clear inv
        this$inv.clear();
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
        double totalWeight = items.stream().mapToDouble(Item::getChance).sum();
        // 100%
        double remainingWeight = 100.00;

        // adjusted chance
        Map<Item, Double> adjustedChances = new HashMap<>();
        for (Item item : items) {
            double chance = (item.getChance() / totalWeight) * 100;
            double roundedChance = Math.round(chance * 100.0) / 100.0;
            // remove chance from 100% because finally must be 100%
            // so when after rounded  sum of chance sometime is 99.99%
            remainingWeight -= roundedChance;
            adjustedChances.put(item, roundedChance);
        }

        // sorted to lowest chance
        List<Map.Entry<Item, Double>> sortedItems = adjustedChances.entrySet().stream()
                .sorted(Map.Entry.<Item, Double>comparingByValue())
                .toList();

        // correct last item chance to fix the sum chance (100%)
        Map.Entry<Item, Double> lastItemEntry = sortedItems.get(sortedItems.size() - 1);
        double lastChance = lastItemEntry.getValue() + remainingWeight;
        adjustedChances.put(lastItemEntry.getKey(), lastChance);

        // foreach and set item to inv
        for (Map.Entry<Item, Double> entry : adjustedChances.entrySet()) {
            Item item = entry.getKey();
            double chance = entry.getValue();
            this$inv.setItem(item.getSlot(), addExtraLore(item, String.format("%.2f", chance)));
        }
    }

    private ItemStack addExtraLore(Item item, String chance) {
        ItemStack itemStack = item.getItemStack().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return itemStack;
        }

        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        if (item.getExtraLore() != null) {
            for (String line : item.getExtraLore()) {
                line = line.replace("{chance}", chance);
                lore.add(ColorFixer.addColors(line));
            }
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void fillAnimationItems() {
        // get instance caseConfig
        CaseConfig caseConfig = GetCase.getInstance().getCaseConfig();
        // animation
        for (Integer slot : animationSlots) {
            // set animation item
            this$inv.setItem(slot, caseConfig.getAnimationItem());
        }
        // no animation
        for (Integer slot : noAnimationSlots) {
            // set no animation item
            this$inv.setItem(slot, caseConfig.getNoAnimationItem());
        }
    }

    private void fillBackground() {
        for (ItemDecoration decoration : decorations) {
            for (Integer slot : decoration.getSlots()) {
                this$inv.setItem(slot, decoration.getItemStack());
            }
        }
    }

    public Inventory getInventory() {
        return this$inv;
    }
}
