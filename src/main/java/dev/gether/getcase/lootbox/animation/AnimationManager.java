package dev.gether.getcase.lootbox.animation;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.inv.SpinInvHolder;
import dev.gether.getcase.lootbox.open.OpenCaseManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimationManager {

    private final GetCase plugin;
    private static final int MAX_TICKS = 100;
    private static final long DELAY_AFTER_FINISH = 20L;
    private static final int SPEED_CHANGE_TICK = 86;
    private static final double SPEED_MULTIPLIER_AFTER_86 = 1.6;
    private static final double SPEED_MULTIPLIER = 1.01;
    private final OpenCaseManager openCaseManager;

    public AnimationManager(GetCase plugin, OpenCaseManager openCaseManager) {
        this.plugin = plugin;
        this.openCaseManager = openCaseManager;
    }

    // prepare inventory with spin
    public void startSpin(Player player, LootBox lootBox) {
        // get random item
        // slot 13 - win
        ItemStack[] itemStacks = new ItemStack[101];
        for (int i = 0; i < 100; i++) {
            itemStacks[i] = openCaseManager.getRandomItem(lootBox).getItemStack();
        }

        SpinInvHolder spinInventory = new SpinInvHolder(lootBox, plugin.getCaseConfig().getSpinData(), itemStacks);
        player.openInventory(spinInventory.getInventory());

        // start animation
        spin(player, spinInventory, 1, 1, 1);
    }
    public void spin(Player player, SpinInvHolder spinInventory, int ticksPassed, double speedCopy, int index) {
        if (spinInventory.isCancel() || spinInventory.isFinish()) {
            if (spinInventory.isFinish()) {
                openCaseManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getWinItem());
            }
            return;
        }

        if (ticksPassed >= MAX_TICKS) {
            spinInventory.cancel();
            new BukkitRunnable() {
                @Override
                public void run() {
                    openCaseManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getWinItem());
                }
            }.runTaskLater(plugin, DELAY_AFTER_FINISH);
            return;
        }

        // update item
        updateInventory(spinInventory.getInventory(), spinInventory, index);
        playSound(player);
        int newTicks = ticksPassed + 2;
        double newSpeed = (newTicks > SPEED_CHANGE_TICK) ? speedCopy * SPEED_MULTIPLIER_AFTER_86 : speedCopy * SPEED_MULTIPLIER;

        new BukkitRunnable() {
            @Override
            public void run() {
                int nextIndex = index + 1;
                spin(player, spinInventory, newTicks, newSpeed, nextIndex);
            }
        }.runTaskLater(plugin, (long) newSpeed);
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), plugin.getCaseConfig().getSpinSound(), 1F, 1F);
    }


    private void updateInventory(Inventory inventory, SpinInvHolder spinInvHolder, int index) {
        for (int i = 9; i < 18; i++) {
            ItemStack itemStack = spinInvHolder.getItemStacks()[i + index - 9];
            inventory.setItem(i, itemStack);
        }
        //
        //ItemCase item = openCaseManager.getRandomItem(caseObject);
        //inventory.setItem(17, item.getItemStack());
    }

}
