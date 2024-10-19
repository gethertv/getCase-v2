package dev.gether.getcase.lootbox.animation;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.MultiCaseOpeningConfig;
import dev.gether.getcase.config.domain.inv.spinning.MultiCaseSpinningInvConfig;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.inv.spin.MultiSpinAnimationHolder;
import dev.gether.getcase.lootbox.reward.RewardsManager;
import dev.gether.getutils.ConfigManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultiCaseAnimationManager {

    final GetCase plugin;
    final RewardsManager rewardsManager;
    final FileManager fileManager;

    public MultiCaseAnimationManager(GetCase plugin, RewardsManager rewardsManager, FileManager fileManager) {
        this.plugin = plugin;
        this.rewardsManager = rewardsManager;
        this.fileManager = fileManager;
    }

    public void startMultiSpin(Player player, LootBox lootBox, int numberOfCases) {
        Map<Integer, ItemStack[]> itemStacksPerRow = generateRandomItemsPerRow(lootBox, numberOfCases);


        File file = new File(plugin.getDataFolder()+"/spinning/", numberOfCases+".yml");
        MultiCaseSpinningInvConfig multiCaseSpinningInvConfig = fileManager.getMultiCaseSpinningInvConfig();
        if(file.exists()) {
            multiCaseSpinningInvConfig = ConfigManager.create(MultiCaseSpinningInvConfig.class, it -> {
                it.setFile(file);
                it.load();
            });
        }

        MultiSpinAnimationHolder spinInventory = new MultiSpinAnimationHolder(plugin, player, lootBox, fileManager, multiCaseSpinningInvConfig, rewardsManager, itemStacksPerRow, numberOfCases);
        spinInventory.startSpin();
    }


    private Map<Integer, ItemStack[]> generateRandomItemsPerRow(LootBox lootBox, int numberOfCases) {
        Map<Integer, ItemStack[]> itemStacksPerRow = new HashMap<>();
        for (int i = 1; i <= numberOfCases; i++) {
            ItemStack[] itemStacks = new ItemStack[101];  // Assuming 101 is the size we need
            for (int j = 0; j < itemStacks.length; j++) {
                itemStacks[j] = rewardsManager.getRandomItem(lootBox).getItemStack();
            }
            itemStacksPerRow.put(i, itemStacks);
        }
        return itemStacksPerRow;
    }



}