package dev.gether.getcase.listener;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.inv.SpinInvHolder;
import dev.gether.getcase.manager.OpenCaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryCloseListener implements Listener {

    private final OpenCaseManager openCaseManager;

    public InventoryCloseListener(OpenCaseManager openCaseManager) {
        this.openCaseManager = openCaseManager;
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof SpinInvHolder))
            return;

        // check the closen inventory it's the same like spin
        SpinInvHolder spinInvHolder = (SpinInvHolder) holder;
        if (!spinInvHolder.isFinish() && !spinInvHolder.isCancel()) {
            spinInvHolder.cancel();
            // give reward
            new BukkitRunnable() {

                @Override
                public void run() {
                    openCaseManager.giveReward(player, spinInvHolder.getCaseObject(), spinInvHolder.getWinItem());

                }
            }.runTaskLater(GetCase.getInstance(), 1L);
        }

    }
}
