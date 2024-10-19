package dev.gether.getcase.lootbox.inv.spin;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.AnimationSpinConfig;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpinAnimationHolder extends AbstractInventoryHolder {

    final LootBox lootBox;
    final FileManager fileManager;
    final RewardsManager rewardsManager;

    final ItemStack[] itemStacks;
    ItemStack winItem;

    boolean finished;
    boolean cancelled;
    final int winningSlotIndex;
    int currentIndex;

    public SpinAnimationHolder(GetCase plugin, Player player, LootBox lootBox, FileManager fileManager, RewardsManager rewardsManager, ItemStack[] itemStacks) {
        super(plugin, player, fileManager.getSpinningInvConfig().getDrawingInv());
        this.lootBox = lootBox;
        this.fileManager = fileManager;
        this.rewardsManager = rewardsManager;
        this.itemStacks = Arrays.copyOf(itemStacks, itemStacks.length);
        this.finished = false;
        this.cancelled = false;
        this.winningSlotIndex = calculateWinningSlotIndex();
        this.currentIndex = 0;

        int numberWin = 0;
        for (int i = 0; i < fileManager.getSpinningInvConfig().getAnimationSlots().length; i++) {
            int animationSlot = fileManager.getSpinningInvConfig().getAnimationSlots()[i];
            if(fileManager.getSpinningInvConfig().getWinningSlot() == animationSlot) {
                numberWin = i;
                break;
            }
        }
        int i = (fileManager.getAnimationSpinConfig().getMAX_TICKS() / 2) - 1;
        this.winItem = itemStacks[i + numberWin];

        initializeItems();
    }

    @Override
    protected void initializeItems() {

    }

    private void spin(Player player, int ticksPassed, double speed) {
        if (isSpinFinished(ticksPassed)) {
            return;
        }

        updateInventory();
        playSound(player);

        scheduleNextSpin(player, ticksPassed, speed);
    }

    private boolean isSpinFinished(int ticksPassed) {
        if (isCancelled() || isFinished()) {
            if (isFinished()) {
                giveRewardSafely(player, this);
            }
            return true;
        }

        AnimationSpinConfig config = fileManager.getAnimationSpinConfig();
        if (ticksPassed >= config.getMAX_TICKS()) {
            cancel();
            scheduleFinalReward(player, this);
            return true;
        }

        return false;
    }

    private void scheduleFinalReward(Player player, SpinAnimationHolder spinInventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                giveRewardSafely(player, spinInventory);
            }
        }.runTaskLater(plugin, fileManager.getAnimationSpinConfig().getDELAY_AFTER_FINISH());
    }

    private void scheduleNextSpin(Player player, int ticksPassed, double speed) {
        int newTicks = ticksPassed + 2;
        double newSpeed = calculateNewSpeed(newTicks, speed);

        new BukkitRunnable() {
            @Override
            public void run() {
                spin(player, newTicks, newSpeed);
            }
        }.runTaskLater(plugin, Math.max(1, (long) newSpeed));
    }

    private double calculateNewSpeed(int newTicks, double speed) {
        AnimationSpinConfig config = fileManager.getAnimationSpinConfig();
        return (newTicks > config.getSPEED_CHANGE_TICK())
                ? speed * config.getSPEED_MULTIPLIER_AFTER_86()
                : speed * config.getSPEED_MULTIPLIER();
    }

    private void giveRewardSafely(Player player, SpinAnimationHolder spinInventory) {
        rewardsManager.giveReward(player, spinInventory.getCaseObject(), spinInventory.getWinItem());
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getSpinAnimationSound(), 1F, 1F);
    }

    private void updateInventory() {
        int[] animationSlots = fileManager.getSpinningInvConfig().getAnimationSlots();
        int itemStacksLength = itemStacks.length;

        for (int i = 0; i < animationSlots.length; i++) {
            int slot = animationSlots[i];
            int itemIndex = (currentIndex + i) % itemStacksLength;
            ItemStack itemStack = itemStacks[itemIndex];
            inventory.setItem(slot, itemStack);
        }

        setCurrentIndex((currentIndex + 1) % itemStacksLength);
    }



    private int calculateWinningSlotIndex() {
        int[] animationSlots = fileManager.getSpinningInvConfig().getAnimationSlots();
        return animationSlots.length / 2;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public void finish() {
        this.finished = true;
    }

    public LootBox getCaseObject() {
        return lootBox;
    }

    public void startSpin() {
        player.openInventory(inventory);
        spin(player,  1, 1);
    }
}