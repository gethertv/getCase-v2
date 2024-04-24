package dev.gether.getcase.lootbox.reward;

import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.config.chest.ItemCase;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.lootbox.lootbox.LootBox;
import dev.gether.getconfig.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class RewardManager {

    private final Random random = new Random(System.currentTimeMillis());
    private final CaseConfig caseConfig;

    public ItemCase giveReward(Player player, LootBox lootBox) {
        ItemCase randomItem = getRandomItem(lootBox);

        player.playSound(player.getLocation(), caseConfig.getWinItemSound(), 1F, 1F);
        // create inventory holder with preview win item
        PreviewWinInvHandler previewWinInvHandler = new PreviewWinInvHandler(randomItem, caseConfig, lootBox);
        // open this inv
        player.openInventory(previewWinInvHandler.getInventory());
        // give winner item to player
        PlayerUtil.giveItem(player, itemStack);
        // broadcast
        broadcast(player, itemStack, lootBox);
    }

    public ItemCase getRandomItem(LootBox lootBox) {
        Set<ItemCase> items = lootBox.getItems();
        double totalWeight = items.stream().mapToDouble(ItemCase::getChance).sum();

        // win ticket
        double randomValue = random.nextDouble() * totalWeight;

        double currentWeight = 0.0;
        for (ItemCase item : items) {
            currentWeight += item.getChance();
            if (currentWeight >= randomValue) {
                return item;
            }
        }

        throw new RuntimeException("Nie mozna wylosowac przedmiotu!");
    }


}
