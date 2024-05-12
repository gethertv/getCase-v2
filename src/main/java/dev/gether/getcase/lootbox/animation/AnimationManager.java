package dev.gether.getcase.lootbox.animation;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.inv.SpinInvHolder;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimationManager {

    private final GetCase plugin;
    private final RewardsManager rewardsManager;
    private final FileManager fileManager;
    private static final int MAX_TICKS = 100;
    private static final long DELAY_AFTER_FINISH = 20L;
    private static final int SPEED_CHANGE_TICK = 86;
    private static final double SPEED_MULTIPLIER_AFTER_86 = 1.6;
    private static final double SPEED_MULTIPLIER = 1.01;

    public AnimationManager(GetCase plugin, RewardsManager rewardsManager, FileManager fileManager) {
        this.plugin = plugin;
        this.rewardsManager = rewardsManager;
        this.fileManager = fileManager;
    }

    // prepare inventory with spin
    public void startSpin(Player player, LootBox lootBox) {
        // get random item
        ItemStack[] itemStacks = new ItemStack[201];
        for (int i = 0; i < itemStacks.length; i++) {
            itemStacks[i] = rewardsManager.getRandomItem(lootBox).getItemStack();
        }
        SpinInvHolder spinInventory = new SpinInvHolder(lootBox, fileManager.getCaseConfig().getSpinData(), itemStacks);
        player.openInventory(spinInventory.getInventory());

        // start animation
        spin(player, spinInventory, 1, 1, 0);
    }
    public void spin(Player player, SpinInvHolder spinInventory, int ticksPassed, double speedCopy, int index) {
        if (spinInventory.isCancel() || spinInventory.isFinish()) {
            if (spinInventory.isFinish()) {
                rewardsManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getWinItem());
            }
            return;
        }

        if (ticksPassed >= MAX_TICKS) {
            spinInventory.cancel();
            new BukkitRunnable() {
                @Override
                public void run() {
                    rewardsManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getWinItem());
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
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getSpinSound(), 1F, 1F);
    }


    private void updateInventory(Inventory inventory, SpinInvHolder spinInvHolder, int index) {
        int[] animationSlots = spinInvHolder.getSpinData().getAnimationSlots();
        for (int i = 0; i < animationSlots.length; i++) {
            int slot = animationSlots[i];
            //ItemStack itemStack = spinInvHolder.getItemStacks()[animationSlots.length * index + i];
            ItemStack itemStack = spinInvHolder.getItemStacks()[index + i];
            inventory.setItem(slot, itemStack);
        }
    }



}
