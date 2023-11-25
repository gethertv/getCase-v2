package dev.gether.getcase.listener;

import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.inv.EditCaseInvHandler;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.inv.SpinInvHolder;
import dev.gether.getcase.manager.AdminEditManager;
import dev.gether.getcase.manager.CaseManager;
import dev.gether.getcase.manager.OpenCaseManager;
import dev.gether.getcase.type.OpenType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class InventoryClickListener implements Listener {


    private final CaseManager caseManager;
    private final AdminEditManager adminEditManager;
    private final OpenCaseManager openCaseManager;


    public InventoryClickListener(CaseManager caseManager, AdminEditManager adminEditManager, OpenCaseManager openCaseManager) {
        this.caseManager = caseManager;
        this.adminEditManager = adminEditManager;
        this.openCaseManager = openCaseManager;
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        // main inv
        Inventory inventory = event.getInventory();

        // clicked inv
        //Inventory clickedInventory = event.getClickedInventory();

        // clicked slot
        final int slot = event.getRawSlot();
        // cast to player
        Player player = (Player) event.getWhoClicked();

        boolean isSpinInv = handleSpinInv(event, inventory);
        // if it's spin inventory then cancel next instruction
        if(isSpinInv)
            return;

        boolean isPreviewCase = handlePreviewCaseInv(event, player, inventory, slot);
        // if this handle is current then do not check next instruction
        if(isPreviewCase)
            return;

        boolean isPreviewWinItem = handlePreviewWinItemInv(event, player, inventory, slot);
        // if it's the inventory with preview win item / cancel next instruction
        if(isPreviewWinItem)
            return;


        handleAdminEditInv(event, inventory, slot);

    }

    private boolean handleSpinInv(InventoryClickEvent event, Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if(holder==null || !(holder instanceof SpinInvHolder))
            return false;

        event.setCancelled(true);
        return true;
    }

    // call the clicked inventory is the same what admin edit (EditCaseInvHandler)
    // return false if is not this inventory
    // true means that is admin inv
    private boolean handleAdminEditInv(InventoryClickEvent event, Inventory inventory, int slot) {
        InventoryHolder holder = inventory.getHolder();
        if(holder==null || !(holder instanceof EditCaseInvHandler))
            return false;

        EditCaseInvHandler editCaseInvHandler = (EditCaseInvHandler) holder;

        // if clicked slot is last = save item
        if(slot==editCaseInvHandler.getInventory().getSize()-1) {
            event.setCancelled(true);
            adminEditManager.saveAllItems(editCaseInvHandler);
        }

        // if click SHIFT + RIGHT CLICK then run edit inv (anvil api)
        if(event.getClick() == ClickType.SHIFT_RIGHT) {
            ItemStack item = inventory.getItem(slot);
            // if slot is empty = cannot edit the chance of item
            if(item==null)
                return true;

            event.setCancelled(true);
            // add all items to list
            adminEditManager.saveAllItems(editCaseInvHandler);
            // create anvil gui
            adminEditManager.editItem(editCaseInvHandler, slot, item);
        }
        return true;
    }

    private boolean handlePreviewWinItemInv(InventoryClickEvent event, Player player, Inventory inventory, int slot) {
        InventoryHolder holder = inventory.getHolder();
        if(holder==null || !(holder instanceof PreviewWinInvHandler previewWinInvHandler))
            return false;

        event.setCancelled(true);
        CaseObject caseObject = previewWinInvHandler.getCaseObject();
        // open case with animation
        if(previewWinInvHandler.isAnimationSlot(slot)) {
            openCaseManager.openCase(player, caseObject, OpenType.ANIMATION);
            return true;
        }
        // open case without the animation
        if(previewWinInvHandler.isNoAnimationSlot(slot)) {
            openCaseManager.openCase(player, caseObject, OpenType.NORMAL);
            return true;
        }
        return true;
    }

    // this call the inventory is this same what preview from case
    private boolean handlePreviewCaseInv(InventoryClickEvent event, Player player, Inventory inventory, int slot) {
        // find inventory
        Optional<CaseObject> caseByInv = caseManager.findCaseByInv(inventory);
        // if inventory is this same what the case then return Case object
        if(caseByInv.isPresent()) {
            // cancel event
            event.setCancelled(true);

            // get object of case
            CaseObject caseObject = caseByInv.get();

//            // check the clicked inventory is not null AND the same what returned from method
//            // IF IS NULL OR NOT THIS SAME - RETURN
//            if(clickedInventory==null || clickedInventory.equals(caseObject.getInventory()))
//                return;

            boolean animationSlot = caseManager.isAnimationSlot(slot, caseObject);
            // if is animation slot than open case with animation
            if(animationSlot) {
                openCaseManager.openCase(player, caseObject, OpenType.ANIMATION);
                return true;
            }

            boolean noAnimation = caseManager.isNoAnimationSlot(slot, caseObject);
            if(noAnimation) {
                openCaseManager.openCase(player, caseObject, OpenType.NORMAL);
                return true;
            }

            return true;
        }
        return false;
    }
}
