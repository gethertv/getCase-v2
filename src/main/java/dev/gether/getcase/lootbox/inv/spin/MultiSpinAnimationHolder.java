package dev.gether.getcase.lootbox.inv.spin;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.AnimationSpinConfig;
import dev.gether.getcase.config.domain.inv.preview.MultiCaseWinItemsPreviewConfig;
import dev.gether.getcase.config.domain.inv.spinning.MultiCaseSpinningInvConfig;
import dev.gether.getcase.lootbox.inv.preview.MultiWinItemPreviewHolder;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class MultiSpinAnimationHolder extends AbstractInventoryHolder {

    LootBox lootBox;
    ItemStack winItem;
    FileManager fileManager;
    RewardsManager rewardsManager;
    MultiCaseSpinningInvConfig multiCaseSpinningInvConfig;

    boolean finished;
    boolean cancelled;
    int currentIndex;
    Map<Integer, Integer> winnerSlot = new HashMap<>();
    Map<Integer, ItemStack> winnerItems = new HashMap<>();
    Map<Integer, ItemStack[]> itemStacksPerRow;
    GetCase plugin;
    int size;

    public MultiSpinAnimationHolder(GetCase plugin, Player player, LootBox lootBox, FileManager fileManager, MultiCaseSpinningInvConfig multiCaseSpinningInvConfig, RewardsManager rewardsManager, Map<Integer, ItemStack[]> itemStacksPerRow, int size) {
        super(plugin, player, multiCaseSpinningInvConfig.getMultiOpenCaseInv());
        this.multiCaseSpinningInvConfig = multiCaseSpinningInvConfig;
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.rewardsManager = rewardsManager;
        this.lootBox = lootBox;
        this.finished = false;
        this.cancelled = false;
        this.currentIndex = 0;
        this.itemStacksPerRow = itemStacksPerRow;
        this.size = size;

        for (int i = 1; i <= size; i++) {
            MultiCaseWinItemsPreviewConfig.AnimationRow animationRow = multiCaseSpinningInvConfig.getAnimationRows().get(i);
            if(animationRow==null) continue;

            winnerItems.put(i, getWinnerItem(i, animationRow));
        }

        initializeItems();
    }

    private ItemStack getWinnerItem(int index, MultiCaseWinItemsPreviewConfig.AnimationRow animationRow) {
        int numberWin = 0;
        int[] slots = animationRow.getSlots();
        for (int j = 0; j < slots.length; j++) {
            int animationSlot = slots[j];
            if(animationRow.getWinnerSlot() == animationSlot) {
                numberWin = j;
                break;
            }
        }
        int i = (fileManager.getAnimationSpinConfig().getMAX_TICKS() / 2) - 1;
        return itemStacksPerRow.get(index)[i + numberWin];
    }

    @Override
    protected void initializeItems() {

    }


    private void multiSpin(Player player, int ticksPassed, double speed) {
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
                openWinPreviewInventory(player);
            }
            return true;
        }


        AnimationSpinConfig animationSpinConfig = fileManager.getAnimationSpinConfig();
        if (ticksPassed >= animationSpinConfig.getMAX_TICKS()) {
            cancel();
            openWinPreviewInventory(player);
            return true;
        }

        return false;
    }

    public void openWinPreviewInventory(Player player) {
        MultiWinItemPreviewHolder previewHolder = new MultiWinItemPreviewHolder(
                plugin,
                player,
                getWinnerItems(),
                plugin.getLootBoxManager(),
                fileManager,
                getCaseObject()
        );
        player.openInventory(previewHolder.getInventory());
    }

    private void scheduleNextSpin(Player player, int ticksPassed, double speed) {
        int newTicks = ticksPassed + 2;
        double newSpeed = calculateNewSpeed(newTicks, speed);

        new BukkitRunnable() {
            @Override
            public void run() {
                multiSpin(player, newTicks, newSpeed);
            }
        }.runTaskLater(plugin, Math.max(1, (long) newSpeed));
    }

    private double calculateNewSpeed(int newTicks, double speed) {
        AnimationSpinConfig animationSpinConfig = fileManager.getAnimationSpinConfig();
        return (newTicks > animationSpinConfig.getSPEED_CHANGE_TICK())
                ? speed * animationSpinConfig.getSPEED_MULTIPLIER_AFTER_86()
                : speed * animationSpinConfig.getSPEED_MULTIPLIER();
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getSpinAnimationSound(), 1F, 1F);
    }

    private void updateInventory() {

        for (Map.Entry<Integer, ItemStack[]> entry : itemStacksPerRow.entrySet()) {
            int rowIndex = entry.getKey();
            ItemStack[] itemStacks = entry.getValue();

            MultiCaseWinItemsPreviewConfig.AnimationRow animationRow = multiCaseSpinningInvConfig.getAnimationRows().get(rowIndex);
            if(animationRow==null) continue;

            int[] slots = animationRow.getSlots();

            for (int i = 0; i < slots.length; i++) {
                int slot = slots[i];
                int itemIndex = (currentIndex + i) % itemStacks.length;
                inventory.setItem(slot, itemStacks[itemIndex]);
            }
        }

        setCurrentIndex((currentIndex + 1) % 101);  // Assuming 101 is the size of each itemStacks array
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
        multiSpin(player, 1, 1);
    }
}