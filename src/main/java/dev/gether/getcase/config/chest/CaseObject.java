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

import java.util.Set;
import java.util.UUID;

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
    private CaseConfig.CaseHologram caseHologram;
    // item key
    private ItemStack keyItem;

    // item in case
    private Set<CaseConfig.Item> items;
    // background decoration
    private Set<CaseConfig.ItemDecoration> decorations;

    // animation slots
    private Set<Integer> animationSlots;
    // no animation slots
    private Set<Integer> noAnimationSlots;

    // broadcast
    private CaseConfig.BroadcastCase broadcastCase;

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
        for (CaseConfig.Item item : items) {
            this$inv.setItem(item.getSlot(), item.getItemStack());
        }
    }

    private void fillAnimationItems() {
        // get instance caseConfig
        CaseConfig caseConfig = GetCase.getInstance().getCaseConfig();
        // animation
        for (Integer slot : animationSlots) {
            // set animation item
            this$inv.setItem(slot, caseConfig.getNoAnimationItem());
        }
        // no animation
        for (Integer slot : noAnimationSlots) {
            // set no animation item
            this$inv.setItem(slot, caseConfig.getNoAnimationItem());
        }
    }

    private void fillBackground() {
        for (CaseConfig.ItemDecoration decoration : decorations) {
            for (Integer slot : decoration.getSlots()) {
                this$inv.setItem(slot, decoration.getItemStack());
            }
        }
    }

    public Inventory getInventory() {
        return this$inv;
    }
}
