package dev.gether.getcase.listener;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerInteractionListener implements Listener {

    private final LootBoxManager lootBoxManager;
    private final FileManager fileManager;

    public PlayerInteractionListener(LootBoxManager lootBoxManager, FileManager fileManager) {
        this.lootBoxManager = lootBoxManager;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Action action = event.getAction();
        // check is case
        handleCasePreview(event, player, action);
        // check player hand case
        handleKeyItemInteract(event, player);

    }

    private void handleKeyItemInteract(PlayerInteractEvent event, Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        // checking, the hand item is a key
        boolean isKey = lootBoxManager.checkIsKey(itemInMainHand, offHand);
        // if key item then cancel
        if(isKey)
            event.setCancelled(true);
    }

    private void handleCasePreview(PlayerInteractEvent event, Player player, Action action) {
        // if not RIGHT-CLICK BLOCK AND not LEFT-CLICK BLOCK THEN RETURN
        if(action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK)
            return;

        // cannot be null  (right/left click block action)
        Block clickedBlock = event.getClickedBlock();
        // get location clicked block
        Location location = clickedBlock.getLocation();

        Optional<CaseLocation> caseByLocation = lootBoxManager.getLocationCaseManager().findCaseByLocation(location);
        // if not found then return
        if(caseByLocation.isEmpty())
            return;

        // disable off-hand click
        if(event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        CaseLocation caseLocation = caseByLocation.get();
        // find case by ID
        Optional<LootBox> caseByID = lootBoxManager.findCaseByID(caseLocation.getCaseId());
        // if not found / then return
        if(caseByID.isEmpty())
            return;

        event.setCancelled(true);
        // get case object
        LootBox lootBox = caseByID.get();
        // play sound
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getPreviewCaseSound(), 1F, 1F);
        // open inventory with preview drop
        player.openInventory(lootBox.getInventory());
    }
}
