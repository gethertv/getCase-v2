package dev.gether.getcase.config.domain.chest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.lootbox.animation.Animation;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getconfig.GetConfig;
import dev.gether.getconfig.domain.Item;
import dev.gether.getconfig.domain.config.ItemDecoration;
import dev.gether.getconfig.utils.ColorFixer;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

// class represent object CASE
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LootBox extends GetConfig {
    @JsonIgnore
    private transient Inventory inv;
    // type case/lootbox may be a luckblock or normal case
    private LootboxType lootboxType;
    private boolean enable;
    private boolean previewEnable;
    private UUID caseId;
    private int sizeInv;
    private String titleInv;
    private String name;
//    // key section
//    private ItemStack itemKey;
    private ItemStack key;
    // item key
    private Set<ItemCase> items;
    // background decoration
    private Set<ItemDecoration> decorations;
    // animation data
    private Animation animation;
    // broadcast
    private BroadcastCase broadcastCase;

    @JsonIgnore
    public void createInv() {
        inv = Bukkit.createInventory(null, sizeInv, ColorFixer.addColors(titleInv));
        // fill inv with items
        fillInventory();
    }

    @JsonIgnore
    public void fillInventory() {
        // clear inv
        inv.clear();
        // fill [ background items]
        fillBackground();
        // fill [ animation and no animation items ]
        //fillAnimationItems(); // TODO: HERE IGNORE
        // fill [ items case ]
        fillItemCase();

        // set key
        //ItemMeta itemMeta = key.getItemMeta();
        //itemMeta.getPersistentDataContainer().set(GetCase.NAMESPACED_KEY, PersistentDataType.STRING, caseId.toString());
        //this.key.setItemMeta(itemMeta);
    }

    @JsonIgnore
    private void fillItemCase() {
        // ignore if list items is empty
        if(items.isEmpty())
            return;



        // foreach and set item to inv
        for (Map.Entry<ItemCase, Double> entry : getChance().entrySet()) {
            ItemCase item = entry.getKey();
            double chance = entry.getValue();
            inv.setItem(item.getSlot(), addExtraLore(item, String.format("%.2f", chance)));
        }

    }
    @JsonIgnore
    public Map<ItemCase, Double> getChance() {
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
        return adjustedChances;
    }


    @JsonIgnore
    public ItemStack addExtraLore(ItemCase item, String chance) {
        final ItemStack itemStack = item.getItem().getItemStack();
        final ItemMeta itemMeta = Optional.ofNullable(itemStack.getItemMeta()).orElseThrow(() -> new IllegalStateException("ItemMeta cannot be null"));

        List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace("{chance}", String.valueOf(chance)));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @JsonIgnore
    private void fillAnimationItems() {
        if(lootboxType == LootboxType.LUCKBLOCK)
            return;

        // get instance caseConfig
        CaseConfig caseConfig = GetCase.getInstance().getFileManager().getCaseConfig();
        // animation
        for (Integer slot : animation.getAnimationSlots()) {
            // set animation item
            inv.setItem(slot, caseConfig.getAnimationItem().getItemStack());
        }
        // no animation
        for (Integer slot : animation.getNoAnimationSlots()) {
            // set no animation item
            inv.setItem(slot, caseConfig.getNoAnimationItem().getItemStack());
        }
    }


    @JsonIgnore
    private void fillBackground() {
        for (ItemDecoration decoration : decorations) {
            for (Integer slot : decoration.getSlots()) {
                inv.setItem(slot, decoration.getItemStack());
            }
        }
    }

    @JsonIgnore
    public Inventory getInventory() {
        return inv;
    }

}
