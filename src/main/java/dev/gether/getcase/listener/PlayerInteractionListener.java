package dev.gether.getcase.listener;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getcase.lootbox.animation.AnimationType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerInteractionListener implements Listener {

    GetCase plugin;
    LootBoxManager lootBoxManager;
    FileManager fileManager;

    public PlayerInteractionListener(GetCase plugin, LootBoxManager lootBoxManager, FileManager fileManager) {
        this.plugin = plugin;
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

        Optional<LootBox> lootBoxOpt = lootBoxManager.checkIsKey(itemInMainHand, offHand);
        // if key item then cancel
        lootBoxOpt.ifPresent(lootBox -> {
            event.setCancelled(true);

            // open all cases by one click
            if (fileManager.getCaseConfig().isQuickOpenEnabled() &&
                    player.isSneaking() &&
                    event.getAction().toString().contains("RIGHT")) {

                lootBoxManager.openAllCase(player, lootBox);
                return;
            }

            // if is not the luckblock then ignore otherwise just open case
            if (lootBox.getLootboxType() != LootboxType.LUCKY_BLOCK)
                return;

            lootBoxManager.openCase(player, lootBox, getTypeAnimation(event.getAction()));
        });
    }

    public AnimationType getTypeAnimation(Action action) {
        switch (action) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                return AnimationType.QUICK;
            }
            default -> {
                return AnimationType.SPIN;
            }
        }
    }

    private void handleCasePreview(PlayerInteractEvent event, Player player, Action action) {
        // if not RIGHT-CLICK BLOCK AND not LEFT-CLICK BLOCK THEN RETURN
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK)
            return;

        // cannot be null  (right/left click block action)
        Block clickedBlock = event.getClickedBlock();
        // get location clicked block
        Location location = clickedBlock.getLocation();

        Optional<LootBox> lootBoxOptional = lootBoxManager.getLocationCaseManager().findCaseByLocation(location);
        // if not found then return
        if (lootBoxOptional.isEmpty())
            return;

        // disable off-hand click
        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        event.setCancelled(true);
        LootBox lootBox = lootBoxOptional.get();
        lootBoxManager.openPreview(player, lootBox);
    }
}
