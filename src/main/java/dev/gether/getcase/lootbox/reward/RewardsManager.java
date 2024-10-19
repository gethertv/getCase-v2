package dev.gether.getcase.lootbox.reward;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.inv.preview.MultiWinItemPreviewHolder;
import dev.gether.getcase.lootbox.inv.spin.MultiSpinAnimationHolder;
import dev.gether.getcase.lootbox.model.ItemCase;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.inv.preview.WinItemPreviewHolder;
import dev.gether.getcase.utils.ItemUtil;
import dev.gether.getutils.utils.MessageUtil;
import dev.gether.getutils.utils.PlayerUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RewardsManager {

    JavaPlugin plugin;
    LootBoxManager lootBoxManager;
    FileManager fileManager;
    Random random = new Random();

    public ItemCase giveReward(Player player, LootBox lootBox, boolean showPreview) {
        ItemCase itemCase = getRandomItem(lootBox);
        ItemStack itemStack = itemCase.getItemStack().clone();

        giveRewardToPlayer(player, itemStack, lootBox, showPreview);

        return itemCase;
    }

    public ItemStack giveReward(Player player, LootBox lootBox, ItemStack itemStack) {
        ItemStack clonedItem = itemStack.clone();
        giveRewardToPlayer(player, clonedItem, lootBox, true);
        return clonedItem;
    }

    private void giveRewardToPlayer(Player player, ItemStack itemStack, LootBox lootBox, boolean showPreview) {
        // sound
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getRewardSound(), 1F, 1F);
        // preview inv
        if (showPreview) {
            showRewardPreview(player, itemStack, lootBox);
        }
        // add items
        PlayerUtil.addItems(player, itemStack);
        // broadcast
        broadcastReward(player, itemStack, lootBox);
    }

    private void giveRewardMultiCaseToPlayer(Player player, MultiSpinAnimationHolder multiSpinAnimationHolder, boolean showPreview) {
        // sound
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getRewardSound(), 1F, 1F);
        // preview inv
        if (showPreview) {
            showMultiCaseRewardPreview(player, multiSpinAnimationHolder.getWinnerItems(), multiSpinAnimationHolder.getLootBox());
        }
        // add items
        ItemStack[] items = multiSpinAnimationHolder.getWinnerItems().values().toArray(ItemStack[]::new);

        PlayerUtil.addItems(player, items);
        // broadcast
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            broadcastReward(player, item, multiSpinAnimationHolder.getLootBox());
        }

    }


    private void showRewardPreview(Player player, ItemStack itemStack, LootBox lootBox) {
        WinItemPreviewHolder previewWinInvHandler = new WinItemPreviewHolder(plugin, player, itemStack, lootBoxManager, fileManager, lootBox);
        player.openInventory(previewWinInvHandler.getInventory());
    }

    private void showMultiCaseRewardPreview(Player player, Map<Integer, ItemStack> winnerItems, LootBox lootBox) {
        MultiWinItemPreviewHolder multiWinItemPreviewHolder = new MultiWinItemPreviewHolder(plugin, player, winnerItems, lootBoxManager, fileManager, lootBox);
        player.openInventory(multiWinItemPreviewHolder.getInventory());
    }

    public ItemCase getRandomItem(LootBox lootBox) {
        Set<ItemCase> items = lootBox.getItems();
        double totalWeight = items.stream().mapToDouble(ItemCase::getChance).sum();
        double randomValue = random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;
        for (ItemCase item : items) {
            cumulativeWeight += item.getChance();
            if (randomValue <= cumulativeWeight) {
                return item;
            }
        }
        throw new RuntimeException("[getCase] Probably loot box is empty");
    }

    private void broadcastReward(Player player, ItemStack itemStack, LootBox lootBox) {
        Optional.ofNullable(lootBox.getBroadcastCase())
                .filter(LootBox.BroadcastCase::isEnable)
                .ifPresent(broadcastCase -> broadcastRewardMessage(player, itemStack, broadcastCase));
    }

    private void broadcastRewardMessage(Player player, ItemStack itemStack, LootBox.BroadcastCase broadcastCase) {
        if (broadcastCase.getMessages().isEmpty()) {
            return;
        }

        String message = String.join("\n", broadcastCase.getMessages());
        message = message
                .replace("{amount}", String.valueOf(itemStack.getAmount()))
                .replace("{player}", player.getName())
                .replace("{item}", ItemUtil.getItemName(itemStack));

        MessageUtil.broadcast(message);
    }
}