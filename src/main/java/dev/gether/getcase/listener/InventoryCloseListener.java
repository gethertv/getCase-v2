package dev.gether.getcase.listener;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.inv.SpinInvHolder;
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

    public InventoryCloseListener(LootBoxManager lootBoxManager) {
        this.rewardsManager = lootBoxManager.getRewardsManager();
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof SpinInvHolder spinInvHolder))
            return;

        // check the closed inventory it's the same that the spin inv
        if (!spinInvHolder.isFinish() && !spinInvHolder.isCancel()) {
            spinInvHolder.cancel();
            // give reward
            new BukkitRunnable() {

                @Override
                public void run() {
                    rewardsManager.giveReward(player, spinInvHolder.getCaseObject(), spinInvHolder.getWinItem());

                }
            }.runTaskLater(GetCase.getInstance(), 1L);
        }

    }
}
