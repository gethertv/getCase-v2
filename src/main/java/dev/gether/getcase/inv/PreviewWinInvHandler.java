package dev.gether.getcase.inv;

import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.config.domain.chest.PreviewWinItem;
import dev.gether.getconfig.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PreviewWinInvHandler implements InventoryHolder {

    private final Inventory inventory;
    private final LootBox lootBox;
    private final PreviewWinItem previewWinItem;
    public PreviewWinInvHandler(ItemCase itemCase, CaseConfig caseConfig, LootBox lootBox) {
        this.lootBox = lootBox;

        // preview schemat
        previewWinItem = caseConfig.getPreviewWinItem();

        // create edit inv
        inventory = Bukkit.createInventory(
                this,
                previewWinItem.getSize(),
                ColorFixer.addColors(previewWinItem.getTitle()));

        // fill decoration items
        fillDecorationItems(previewWinItem);
        // set open case item with and without the animation
        fillAnimationItems(caseConfig, previewWinItem);
        // set winner item
        inventory.setItem(previewWinItem.getSlotWinItem(), itemCase.getItemStack());

    }

    private void fillAnimationItems(CaseConfig caseConfig, PreviewWinItem previewWinItem) {
        // set item with animation
        previewWinItem.getAnimationSlots().forEach(slot -> inventory.setItem(slot, caseConfig.getAnimationItem().getItemStack()));
        // set item without the animation
        previewWinItem.getNoAnimationSlots().forEach(slot -> inventory.setItem(slot, caseConfig.getNoAnimationItem().getItemStack()));
    }

    // fill background/decoration
    private void fillDecorationItems(PreviewWinItem previewWinItem) {
        previewWinItem.getItemDecorations().forEach(itemDecoration ->
                itemDecoration.getSlots().forEach(slot -> inventory.setItem(slot, itemDecoration.getItemStack()))
        );
    }

    public boolean isAnimationSlot(int slot) {
        return previewWinItem.getAnimationSlots().contains(slot);
    }

    public boolean isNoAnimationSlot(int slot) {
        return previewWinItem.getNoAnimationSlots().contains(slot);
    }

    public LootBox getCaseObject() {
        return lootBox;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
