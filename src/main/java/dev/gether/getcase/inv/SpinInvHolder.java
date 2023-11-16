package dev.gether.getcase.inv;

import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.config.chest.SpinData;
import dev.gether.getcase.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SpinInvHolder implements InventoryHolder {

    private final Inventory inventory;
    private boolean finish;
    private boolean cancel;
    private final CaseObject caseObject;
    public SpinInvHolder(CaseObject caseObject, SpinData spinData) {
        this.caseObject = caseObject;

        // create edit inv
        inventory = Bukkit.createInventory(
                this,
                spinData.getSize(),
                ColorFixer.addColors(spinData.getTitle()));

        // fill with items
        fillDecorationItems(spinData);

        finish = false;
        cancel = false;

    }

    public void fillDecorationItems(SpinData spinData) {
        // fill background
        spinData.getItemDecorations().forEach(decoration ->
                decoration.getSlots().forEach(slot ->
                        inventory.setItem(slot, decoration.getItemStack())
                )
        );
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public CaseObject getCaseObject() {
        return caseObject;
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
}
