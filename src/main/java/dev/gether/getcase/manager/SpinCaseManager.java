package dev.gether.getcase.manager;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.config.chest.Item;
import dev.gether.getcase.inv.SpinInvHolder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class SpinCaseManager {

    private final GetCase plugin;
    private static final int MAX_TICKS = 100;
    private static final long DELAY_AFTER_FINISH = 20L;
    private static final int SPEED_CHANGE_TICK = 86;
    private static final double SPEED_MULTIPLIER_AFTER_86 = 1.6;
    private static final double SPEED_MULTIPLIER = 1.01;
    private final OpenCaseManager openCaseManager;

    public SpinCaseManager(GetCase plugin, OpenCaseManager openCaseManager) {
        this.plugin = plugin;
        this.openCaseManager = openCaseManager;
    }

    // prepare inventory with spin
    public void startSpin(Player player, CaseObject caseObject) {
        SpinInvHolder spinInventory = new SpinInvHolder(caseObject, plugin.getCaseConfig().getSpinData());
        player.openInventory(spinInventory.getInventory());

        // start animation
        spin(player, caseObject, spinInventory, 1, 1);
    }
    public void spin(Player player, CaseObject caseObject, SpinInvHolder spinInventory, int ticksPassed, double speedCopy) {

        if (spinInventory.isCancel() || spinInventory.isFinish()) {
            if (spinInventory.isFinish()) {
                openCaseManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getInventory().getItem(13));
            }
            return;
        }

        if (ticksPassed >= MAX_TICKS) {
            spinInventory.cancel();
            new BukkitRunnable() {
                @Override
                public void run() {
                    openCaseManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getInventory().getItem(13));
                }
            }.runTaskLater(plugin, DELAY_AFTER_FINISH);
            return;
        }

        updateInventory(spinInventory.getInventory(), caseObject);

        int newTicks = ticksPassed + 2;
        double newSpeed = (newTicks > SPEED_CHANGE_TICK) ? speedCopy * SPEED_MULTIPLIER_AFTER_86 : speedCopy * SPEED_MULTIPLIER;

        new BukkitRunnable() {
            @Override
            public void run() {
                spin(player, caseObject, spinInventory, newTicks, newSpeed);
            }
        }.runTaskLater(plugin, (long) newSpeed);
    }



    private void updateInventory(Inventory inventory, CaseObject caseObject) {
        for (int i = 9; i < 17; i++) {
            inventory.setItem(i, inventory.getItem(i + 1));
        }
        //
        Item item = openCaseManager.getRandomItem(caseObject);
        inventory.setItem(17, item.getItemStack());
    }

}
