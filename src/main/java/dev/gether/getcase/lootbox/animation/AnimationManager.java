package dev.gether.getcase.lootbox.animation;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.inv.spin.SpinAnimationHolder;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimationManager {
    static final int WINNING_SLOT = 11;
    static final int ITEM_STACK_SIZE = 101;

    final GetCase plugin;
    final RewardsManager rewardsManager;
    final FileManager fileManager;
    final MultiCaseAnimationManager multiCaseAnimationManager;

    public AnimationManager(GetCase plugin, RewardsManager rewardsManager, FileManager fileManager, MultiCaseAnimationManager multiCaseAnimationManager) {
        this.plugin = plugin;
        this.rewardsManager = rewardsManager;
        this.fileManager = fileManager;
        this.multiCaseAnimationManager = multiCaseAnimationManager;
    }

    public void startSpin(Player player, LootBox lootBox) {
        ItemStack[] itemStacks = generateRandomItems(lootBox);
        SpinAnimationHolder spinInventory = new SpinAnimationHolder(plugin, player, lootBox, fileManager, rewardsManager, itemStacks);
        spinInventory.startSpin();
    }


    public void startMultiCaseSpin(Player player, LootBox lootBox, int numberOfCases) {
        multiCaseAnimationManager.startMultiSpin(player, lootBox, numberOfCases);
    }


    private ItemStack[] generateRandomItems(LootBox lootBox) {
        ItemStack[] itemStacks = new ItemStack[ITEM_STACK_SIZE];
        for (int i = 0; i < itemStacks.length; i++) {
            itemStacks[i] = rewardsManager.getRandomItem(lootBox).getItemStack();
        }
        return itemStacks;
    }

}