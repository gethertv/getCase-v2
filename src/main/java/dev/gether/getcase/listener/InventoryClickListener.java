package dev.gether.getcase.listener;

import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.inv.EditCaseInv;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.inv.SpinInvHolder;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getcase.lootbox.animation.AnimationType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryClickListener implements Listener {


    LootBoxManager lootBoxManager;

    public InventoryClickListener(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
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

    }

    private boolean handleSpinInv(InventoryClickEvent event, Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof SpinInvHolder))
            return false;

        event.setCancelled(true);
        return true;
    }


    private boolean handlePreviewWinItemInv(InventoryClickEvent event, Player player, Inventory inventory, int slot) {
        InventoryHolder holder = inventory.getHolder();
        if(holder==null || !(holder instanceof PreviewWinInvHandler previewWinInvHandler))
            return false;

        event.setCancelled(true);
        LootBox lootBox = previewWinInvHandler.getCaseObject();

        // open case with animation
        if(previewWinInvHandler.isAnimationSlot(slot)) {
            lootBoxManager.openCase(player, lootBox, AnimationType.SPIN);
            return true;
        }
        // open case without the animation
        if(previewWinInvHandler.isNoAnimationSlot(slot)) {
            lootBoxManager.openCase(player, lootBox, AnimationType.QUICK);
            return true;
        }
        return true;
    }

    public void editItem(EditCaseInv editCaseInv, int slot, ItemStack itemStack) {

        // check itemstack exits in list with items
        Optional<ItemCase> itemByCaseAndItemStack = lootBoxManager.findItemByCaseAndSlot(editCaseInv.getLootBox(), slot);
        // create default object if item will not exist
        ItemCase itemCase = ItemCase.builder()
                .slot(slot)
                .chance(0)
                .itemStack(itemStack)
                .extraLore(List.of("&7", "&7Chance: &f{chance}%", "&7"))
                .build();

        // if exists then not create new object
        if(itemByCaseAndItemStack.isPresent()) {
            itemCase = itemByCaseAndItemStack.get();
        } else {
            // if item not exists in list then add
            editCaseInv.getLootBox().getItems().add(itemCase);
        }

        // create anvil builder
        AnvilGUI.Builder builder = new AnvilGUI.Builder();

        // final object with item
        final ItemCase finalItem = itemCase;
        // builder onClick event
        builder.onClick((anvilSlot, stateSnapshot) -> {
            if (anvilSlot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            String text = stateSnapshot.getText();
            if(!isDouble(text)) {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Number"));
            }
            // parse chance from text to double
            double chance = Double.parseDouble(text);
            // set new chance
            finalItem.setChance(chance);
            // [!] IGNORE THIS SAVE
            // because the saving method exists in main GUI/INV with button/item 'SAVE'
            // save items
            //saveCase(editCaseInvHandler.getCaseObject());
            // open preview inv
            return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(editCaseInv::fillInvByItems)
            );
        });
        // title gui
        builder.title("Chance");
        // left item text
        builder.text("0.00");
        // left item
        builder.itemLeft(new ItemStack(Material.PAPER));
        // set instance from main plugin
        builder.plugin(editCaseInv.getPlugin());
        // open anvil inv
        builder.open(editCaseInv.getPlayer());
    }

    private boolean isDouble(String input) {
        try {
            double chance = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException ignored) {}
        return false;
    }

    // this call the inventory is this same what preview from case
    private boolean handlePreviewCaseInv(InventoryClickEvent event, Player player, Inventory inventory, int slot) {
        // find inventory
        Optional<LootBox> caseByInv = lootBoxManager.findCaseByInv(inventory);
        // if inventory is this same what the case then return Case object
        if(caseByInv.isPresent()) {
            // cancel event
            event.setCancelled(true);

            // get object of case
            LootBox lootBox = caseByInv.get();

            // check is the luckblock case
            if(lootBox.getLootboxType() == LootboxType.LUCKY_BLOCK)
                return true;

            boolean animationSlot = lootBoxManager.isAnimationSlot(slot, lootBox);
            // if is animation slot than open case with animation
            if(animationSlot) {
                lootBoxManager.openCase(player, lootBox, AnimationType.SPIN);
                return true;
            }

            boolean noAnimation = lootBoxManager.isNoAnimationSlot(slot, lootBox);
            if(noAnimation) {
                lootBoxManager.openCase(player, lootBox, AnimationType.QUICK);
                return true;
            }

            return true;
        }
        return false;
    }
}
