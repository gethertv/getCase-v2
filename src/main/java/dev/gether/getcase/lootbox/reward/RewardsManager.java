package dev.gether.getcase.lootbox.reward;

import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.domain.chest.BroadcastCase;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;

public class RewardsManager {

    private final Random random = new Random(System.currentTimeMillis());
    private final CaseConfig caseConfig;

    public RewardsManager(CaseConfig caseConfig) {
        this.caseConfig = caseConfig;
    }

    public ItemCase giveReward(Player player, LootBox lootBox) {
        ItemCase itemCase = getRandomItem(lootBox);

        player.playSound(player.getLocation(), caseConfig.getWinItemSound(), 1F, 1F);
        // create inventory holder with preview win item
        PreviewWinInvHandler previewWinInvHandler = new PreviewWinInvHandler(itemCase, caseConfig, lootBox);
        // open this inv
        player.openInventory(previewWinInvHandler.getInventory());
        // give winner item to player
        ItemStack itemStack = itemCase.getItemStack().clone();
        PlayerUtil.giveItem(player, itemStack);
        // broadcast
        broadcast(player, itemStack, lootBox);
        return itemCase;
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



    public void broadcast(Player player, ItemStack itemStack, LootBox lootBox) {
        BroadcastCase broadcastCase = lootBox.getBroadcastCase();
        if(!broadcastCase.isEnable())
            return;

        // check message is not empty
        if(broadcastCase.getMessages().isEmpty())
            return;

        String message = String.join("\n", broadcastCase.getMessages());
        message = message
                .replace("{amount}", String.valueOf(itemStack.getAmount()))
                .replace("{player}", player.getName())
                .replace("{item}", ItemUtil.getItemName(itemStack));
        MessageUtil.broadcast(message);
    }


}
