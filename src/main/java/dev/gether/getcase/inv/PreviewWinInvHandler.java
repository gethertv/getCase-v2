package dev.gether.getcase.inv;

import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.config.chest.PreviewWinItem;
import dev.gether.getcase.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class PreviewWinInvHandler implements InventoryHolder {

    private final Inventory inventory;
    private final CaseObject caseObject;
    private final PreviewWinItem previewWinItem;
    public PreviewWinInvHandler(ItemStack itemStack, CaseConfig caseConfig, CaseObject caseObject) {
        this.caseObject = caseObject;

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
        inventory.setItem(previewWinItem.getSlotWinItem(), itemStack);

    }

    private void fillAnimationItems(CaseConfig caseConfig, PreviewWinItem previewWinItem) {
        // set item with animation
        previewWinItem.getAnimationSlots().forEach(slot -> inventory.setItem(slot, caseConfig.getAnimationItem()));
        // set item without the animation
        previewWinItem.getNoAnimationSlots().forEach(slot -> inventory.setItem(slot, caseConfig.getNoAnimationItem()));
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

    public CaseObject getCaseObject() {
        return caseObject;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
