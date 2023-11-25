package dev.gether.getcase.config.chest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.CaseConfig;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemDecoration;
import dev.gether.getconfig.utils.ColorFixer;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

// class represent object CASE
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CaseObject extends GetConfig {

    // this$ - ignore implement okaeri config
    @JsonIgnore
    private Inventory inv;

    private boolean enable;
    private UUID caseId;
    private int sizeInv;
    private String titleInv;
    private String name;
    // key section
    private Item itemKey;
    // item key
    private Set<ItemCase> items;
    // background decoration
    private Set<ItemDecoration> decorations;

    // animation slots
    private Set<Integer> animationSlots;
    // no animation slots
    private Set<Integer> noAnimationSlots;

    // broadcast
    private BroadcastCase broadcastCase;

    public void createInv() {

        inv = Bukkit.createInventory(null, sizeInv, ColorFixer.addColors(titleInv));
        // fill inv with items
        fillItems();
    }

    public void fillItems() {
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
                .sorted(Map.Entry.<ItemCase, Double>comparingByValue())
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

    private ItemStack addExtraLore(ItemCase item, String chance) {
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
            inv.setItem(slot, caseConfig.getAnimationItem().getItemStack());
        }
        // no animation
        for (Integer slot : noAnimationSlots) {
            // set no animation item
            inv.setItem(slot, caseConfig.getNoAnimationItem().getItemStack());
        }
    }

    private void fillBackground() {
        for (ItemDecoration decoration : decorations) {
            for (Integer slot : decoration.getSlots()) {
                inv.setItem(slot, decoration.getItemStack());
            }
        }
    }


    @JsonIgnore
    public ItemStack getKeyItem() {
        return itemKey.getItemStack();
    }

    @JsonIgnore
    public Inventory getInventory() {
        return inv;
    }

}
