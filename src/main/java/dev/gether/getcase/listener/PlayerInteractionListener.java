package dev.gether.getcase.listener;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.event.DecentHologramsEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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

//        // check player hand case
//        if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
//            handleKeyItemInteract(event, player);
//            return;
//        }
        // check is case
        handleCasePreview(event, player, action);

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


        // cannot be null  (right/left click block action)
        Block clickedBlock = event.getClickedBlock();
        // get location clicked block
        if(clickedBlock==null)
            return;

        Location location = clickedBlock.getLocation();

        Optional<CaseLocation> caseByLocation = lootBoxManager.getLocationCaseManager().findCaseByLocation(location);
        // if not found then return
        if(caseByLocation.isEmpty())
            return;

        // disable off-hand click
        if(event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        CaseLocation caseLocation = caseByLocation.get();

        ItemStack item = event.getItem();
        Optional<LootBox> caseByUUID = lootBoxManager.findCaseByUUID(caseLocation.getCaseId());

        if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock()==null)
                return;

            event.setCancelled(true);
            if(item!=null) {
                // checking, the hand item is a key


                // if key item then cancel
                caseByUUID.ifPresent(lootBox -> {

                    if(!item.isSimilar(lootBox.getKey())) {
                        push(player, lootBox);
                        return;
                    }

                    event.setCancelled(true);
                    if(fileManager.getCaseConfig().isQuickOpenCase() &&
                            player.isSneaking() &&
                            event.getAction().toString().contains("RIGHT")) {

                        lootBoxManager.openCaseQuick(player,
                                location.clone().add(0.5, 0, 0.5),
                                lootBox,
                                fileManager.getCaseConfig().getMaxQuickOpen());
                        return;
                    }
                    lootBoxManager.openCase(player, lootBox, location.clone().add(0.5, 0, 0.5), lootBox.getAnimation().getAnimationType());


                });
            } else {
                caseByUUID.ifPresent(lootBox -> push(player, lootBox));
            }
            return;
        }

        // find case by ID
        Optional<LootBox> caseByID = lootBoxManager.findCaseByID(caseLocation.getCaseId());
        // if not found / then return
        if(caseByID.isEmpty())
            return;

        event.setCancelled(true);
        // get case object
        LootBox lootBox = caseByID.get();
        if(!lootBox.isPreviewEnable()) {
            MessageUtil.sendMessage(player, fileManager.getCaseConfig().getPreviewIsDisabled());
            return;
        }
        // play sound
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getPreviewCaseSound(), 1F, 1F);
        // open inventory with preview drop
        player.openInventory(lootBox.getInventory());
    }

    private void push(Player player, LootBox lootBox) {
        String noKey = fileManager.getCaseConfig().getNoKey();
        MessageUtil.sendMessage(player,
                noKey.replace("{key}", ItemUtil.getItemName(lootBox.getKey()))
                        .replace("{case}", lootBox.getName()));

        Vector direction = player.getLocation().getDirection().multiply(fileManager.getCaseConfig().getMultiply());
        direction.setY(fileManager.getCaseConfig().getHeightY());
        player.setVelocity(direction);
    }
}
