package dev.gether.getcase.inv;

import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.config.domain.chest.SpinData;
import dev.gether.getconfig.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SpinInvHolder implements InventoryHolder {

    private final Inventory inventory;
    private boolean finish;
    private boolean cancel;
    private final LootBox lootBox;
    private final SpinData spinData;
    private final ItemStack[] itemStacks;
    public SpinInvHolder(LootBox lootBox, SpinData spinData, ItemStack[] itemStacks) {
        this.lootBox = lootBox;
        this.spinData = spinData;

        // create edit inv
        inventory = Bukkit.createInventory(
                this,
                spinData.getSize(),
                ColorFixer.addColors(spinData.getTitle()));

        // fill with items
        fillDecorationItems(spinData);

        finish = false;
        cancel = false;

        // set win items
        this.itemStacks = itemStacks;

    }

    public void fillDecorationItems(SpinData spinData) {
        // fill background
        spinData.getItemDecorations().forEach(decoration ->
                decoration.getSlots().forEach(slot ->
                        inventory.setItem(slot, decoration.getItemStack())
                )
        );
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public LootBox getCaseObject() {
        return lootBox;
    }

    public ItemStack getWinItem() {
        return itemStacks[54];
    }
    public void cancel() {
        this.cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    public boolean isFinish() {
        return finish;
    }

    public SpinData getSpinData() {
        return spinData;
    }
}
