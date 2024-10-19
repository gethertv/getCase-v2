package dev.gether.getcase.listener;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.lootbox.animation.MultiCaseAnimationManager;
import dev.gether.getcase.lootbox.inv.spin.SpinAnimationHolder;
import dev.gether.getcase.lootbox.inv.spin.MultiSpinAnimationHolder;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryCloseListener implements Listener {

    private final RewardsManager rewardsManager;
    private final MultiCaseAnimationManager multiCaseAnimationManager;

    public InventoryCloseListener(LootBoxManager lootBoxManager) {
        this.rewardsManager = lootBoxManager.getRewardsManager();
        this.multiCaseAnimationManager = lootBoxManager.getMultiCaseAnimationManager();
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof SpinAnimationHolder spinAnimationHolder) {
            handleSpinAnimationClose(player, spinAnimationHolder);
        } else if (holder instanceof MultiSpinAnimationHolder multiSpinAnimationHolder) {
            handleMultiSpinAnimationClose(player, multiSpinAnimationHolder);
        }
    }

    private void handleSpinAnimationClose(Player player, SpinAnimationHolder spinAnimationHolder) {
        if (!spinAnimationHolder.isFinished() && !spinAnimationHolder.isCancelled()) {
            spinAnimationHolder.cancel();
            // give reward
            new BukkitRunnable() {
                @Override
                public void run() {
                    rewardsManager.giveReward(player, spinAnimationHolder.getLootBox(), spinAnimationHolder.getWinItem());
                }
            }.runTaskLater(GetCase.getInstance(), 1L);
        }
    }

    private void handleMultiSpinAnimationClose(Player player, MultiSpinAnimationHolder multiSpinAnimationHolder) {
        if (!multiSpinAnimationHolder.isFinished() && !multiSpinAnimationHolder.isCancelled()) {
            multiSpinAnimationHolder.cancel();
            // give rewards
            new BukkitRunnable() {
                @Override
                public void run() {
                    multiSpinAnimationHolder.openWinPreviewInventory(player);
                }
            }.runTaskLater(GetCase.getInstance(), 1L);
        }
    }
}