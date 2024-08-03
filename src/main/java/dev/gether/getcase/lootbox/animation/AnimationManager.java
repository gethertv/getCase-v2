package dev.gether.getcase.lootbox.animation;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.inv.SpinInvHolder;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimationManager {

    private final GetCase plugin;
    private final RewardsManager rewardsManager;
    private final FileManager fileManager;
    private static final int MAX_TICKS = 80;
    private static final long DELAY_AFTER_FINISH = 20L;
    private static final int SPEED_CHANGE_TICK = 86;
    private static final double SPEED_MULTIPLIER_AFTER_86 = 1.6;
    private static final double SPEED_MULTIPLIER = 1.05;

    public AnimationManager(GetCase plugin, RewardsManager rewardsManager, FileManager fileManager) {
        this.plugin = plugin;
        this.rewardsManager = rewardsManager;
        this.fileManager = fileManager;
    }

    // prepare inventory with spin
    public void startSpin(Player player, LootBox lootBox) {
        // get random item
        ItemCase[] itemCases = new ItemCase[201];
        for (int i = 0; i < itemCases.length; i++) {
            itemCases[i] = rewardsManager.getRandomItem(lootBox);
        }
        SpinInvHolder spinInventory = new SpinInvHolder(lootBox, fileManager.getCaseConfig().getSpinData(), itemCases);
        player.openInventory(spinInventory.getInventory());

        // start animation
        spin(player, spinInventory, 1, 1, 100);
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
                int nextIndex = index - 1;
                spin(player, spinInventory, newTicks, newSpeed, nextIndex);
            }
        }.runTaskLater(plugin, (long) newSpeed);
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getSpinSound(), 1F, 1F);
    }


    private void updateInventory(Inventory inventory, SpinInvHolder spinInvHolder, int index) {
        int[] animationSlots = spinInvHolder.getSpinData().getAnimationSlots();
        for (int i = animationSlots.length-1; i >= 0; i--) {
            int slot = animationSlots[i];
            //ItemStack itemStack = spinInvHolder.getItemStacks()[animationSlots.length * index + i];
            ItemCase itemCase = spinInvHolder.getItemStacks()[index + i];
            Double chance = spinInvHolder.getCaseObject().getChance().get(itemCase);
            ItemStack itemStack = itemCase.getItem().getItemStack();
            if(itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();

                List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
                for (int z = 0; z < lore.size(); z++) {
                    lore.set(z, lore.get(z).replace("{chance}", String.valueOf(chance)));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(slot, itemCase.getItem().getItemStack());
        }
    }



}
