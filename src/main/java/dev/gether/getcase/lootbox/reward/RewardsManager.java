package dev.gether.getcase.lootbox.reward;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.chest.BroadcastCase;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.utils.ItemUtil;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.getutils.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;

public class RewardsManager {

    private final Random random = new Random(System.currentTimeMillis());
    private final FileManager fileManager;
    public RewardsManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public ItemCase giveReward(Player player, LootBox lootBox) {
        ItemCase itemCase = getRandomItem(lootBox);

        // give winner item to player
        ItemStack itemStack = itemCase.getItemStack().clone();
        final int amount = itemStack.getAmount();

        player.playSound(player.getLocation(), fileManager.getCaseConfig().getWinItemSound(), 1F, 1F);
        // create inventory holder with preview win item
        PreviewWinInvHandler previewWinInvHandler = new PreviewWinInvHandler(itemStack, fileManager.getCaseConfig(), lootBox);
        // open this inv
        player.openInventory(previewWinInvHandler.getInventory());

        PlayerUtil.addItems(player, itemStack);
        // broadcast
        broadcast(player, itemStack, amount, lootBox);
        return itemCase;
    }

    public ItemCase giveRewardWithoutPreview(Player player, LootBox lootBox) {
        ItemCase itemCase = getRandomItem(lootBox);
        // give winner item to player
        ItemStack itemStack = itemCase.getItemStack().clone();
        final int amount = itemStack.getAmount();

        PlayerUtil.addItems(player, itemStack);
        // broadcast
        broadcast(player, itemStack, amount, lootBox);
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getWinItemSound(), 1F, 1F);
        return itemCase;
    }

    public ItemStack giveReward(Player player, LootBox lootBox, ItemStack itemStack) {
        ItemStack item = itemStack.clone();
        final int amount = item.getAmount();
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getWinItemSound(), 1F, 1F);
        // create inventory holder with preview win item
        PreviewWinInvHandler previewWinInvHandler = new PreviewWinInvHandler(item, fileManager.getCaseConfig(), lootBox);
        // open this inv
        player.openInventory(previewWinInvHandler.getInventory());
        // give winner item to player
        PlayerUtil.addItems(player, item);
        // broadcast
        broadcast(player, item, amount, lootBox);
        return item;
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

        throw new RuntimeException("Item cannot be draw. Probably loot box is empty!");
    }



    public void broadcast(Player player, ItemStack itemStack, final int amount, LootBox lootBox) {
        BroadcastCase broadcastCase = lootBox.getBroadcastCase();
        if(!broadcastCase.isEnable())
            return;

        // check message is not empty
        if(broadcastCase.getMessages().isEmpty())
            return;

        String message = String.join("\n", broadcastCase.getMessages());
        message = message
                .replace("{amount}", String.valueOf(amount))
                .replace("{player}", player.getName())
                .replace("{item}", ItemUtil.getItemName(itemStack));
        MessageUtil.broadcast(message);
    }


}
