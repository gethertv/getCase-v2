package dev.gether.getcase.lootbox.reward;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.chest.BroadcastCase;
import dev.gether.getcase.config.domain.chest.ItemCase;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.location.LocationCaseManager;
import dev.gether.getconfig.utils.ColorFixer;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import dev.gether.getconfig.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class RewardsManager {

    private final Random random = new Random(System.currentTimeMillis());
    private final FileManager fileManager;
    private Map<UUID, Item> lastOpen = new HashMap<>();
    private final LootBoxManager lootBoxManager;
    public RewardsManager(FileManager fileManager, LootBoxManager lootBoxManager) {
        this.fileManager = fileManager;
        this.lootBoxManager = lootBoxManager;
    }

    public ItemCase giveReward(Player player, Location location, LootBox lootBox) {
        ItemCase itemCase = getRandomItem(lootBox);

        ItemStack showItem = itemCase.getItem().getItemStack();
        // give winner item to player
        if(itemCase.getCommands().isEmpty()) {
            ItemStack itemStack = itemCase.getItemStack().clone();
            final int amount = itemStack.getAmount();
            PlayerUtil.giveItem(player, itemStack);
            // broadcast
            broadcast(player, itemCase.getItem().getDisplayname(), amount, lootBox);
        } else {
            itemCase.getCommands().forEach(cmd -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
            });
            // broadcast
            broadcast(player, itemCase.getItem().getDisplayname(), itemCase.getItem().getAmount(), lootBox);
        }

        player.playSound(player.getLocation(), fileManager.getCaseConfig().getWinItemSound(), 1F, 1F);

        Item item = lastOpen.get(lootBox.getCaseId());
        if(item!=null && !item.isDead())
            item.remove();

        lootBoxManager.getLocationCaseManager().hideHologram(lootBox.getCaseId());

        Location displayLocation = location.clone().add(0, 1, 0);
        Item droppedItem = player.getWorld().dropItem(displayLocation, showItem);
        droppedItem.setPickupDelay(Integer.MAX_VALUE);
        droppedItem.setVelocity(new Vector(0, 0.25, 0));
        droppedItem.setCustomName(ColorFixer.addColors(itemCase.getItem().getDisplayname()));
        droppedItem.setCustomNameVisible(true);

        lastOpen.put(lootBox.getCaseId(), droppedItem);

        new BukkitRunnable() {
            @Override
            public void run() {
                droppedItem.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(GetCase.getInstance(), 5L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!droppedItem.isDead()) {
                    droppedItem.remove();
                    lootBoxManager.getLocationCaseManager().showHologram(lootBox.getCaseId());
                }
            }
        }.runTaskLater(GetCase.getInstance(), 40L);

        return itemCase;
    }

    public ItemCase giveRewardWithoutPreview(Player player, LootBox lootBox) {
        ItemCase itemCase = getRandomItem(lootBox);
        // give winner item to player
        ItemStack itemStack = itemCase.getItemStack().clone();
        final int amount = itemStack.getAmount();

        PlayerUtil.giveItem(player, itemStack);
        // broadcast
        broadcast(player, itemCase.getItem().getDisplayname(), amount, lootBox);
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getWinItemSound(), 1F, 1F);
        return itemCase;
    }

    public void giveReward(Player player, LootBox lootBox, ItemCase itemCase) {
        if(itemCase.getCommands().isEmpty()) {
            ItemStack itemStack = itemCase.getItemStack().clone();
            final int amount = itemStack.getAmount();
            PlayerUtil.giveItem(player, itemStack);
            // broadcast
            broadcast(player, itemCase.getItem().getDisplayname(), amount, lootBox);
        } else {
            itemCase.getCommands().forEach(cmd -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
            });
            // broadcast
            broadcast(player, itemCase.getItem().getDisplayname(), itemCase.getItem().getAmount(), lootBox);
        }
        player.playSound(player.getLocation(), fileManager.getCaseConfig().getWinItemSound(), 1F, 1F);
        player.closeInventory();
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



    public void broadcast(Player player, String displayName, final int amount, LootBox lootBox) {
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
                .replace("{item}", displayName);
        MessageUtil.broadcast(message);
    }


}
