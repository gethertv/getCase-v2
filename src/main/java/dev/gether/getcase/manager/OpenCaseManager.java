package dev.gether.getcase.manager;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.CaseConfig;
import dev.gether.getcase.config.LangConfig;
import dev.gether.getcase.config.chest.BroadcastCase;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.config.chest.Item;
import dev.gether.getcase.inv.PreviewWinInvHandler;
import dev.gether.getcase.utils.ConsoleColor;
import dev.gether.getcase.utils.InventoryUtil;
import dev.gether.getcase.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;

public class OpenCaseManager {


    private final Random random;
    private final CaseConfig caseConfig;
    private final LangConfig langConfig;
    private final GetCase plugin;

    public OpenCaseManager(GetCase plugin, CaseConfig caseConfig, LangConfig langConfig) {
        this.plugin = plugin;
        this.random = new Random();
        this.caseConfig = caseConfig;
        this.langConfig = langConfig;
    }

    // open case with animation
    public void openCaseAnimation(Player player, CaseObject caseObject) {
        // check requirements like CASE is not empty and player has key for this case
        boolean hasRequirements  = checkRequirements(player, caseObject);
        // is not meets then return
        if(!hasRequirements)
            return;

        // take key
        removeKey(player, caseObject.getKeyItem());
        // start animation
        plugin.getSpinCaseManager().startSpin(player, caseObject);

    }

    // open case without the animation
    public void openCaseWithoutAnimation(Player player, CaseObject caseObject) {
        // check requirements like CASE is not empty and player has key for this case
        boolean hasRequirements  = checkRequirements(player, caseObject);
        // is not meets then return
        if(!hasRequirements)
            return;

        // if has key, then take it
        // method remove only 1 item
        removeKey(player, caseObject.getKeyItem());

        // next, random the reward
        Item winItem = getRandomItem(caseObject);

        // give reward
        giveReward(player, caseObject, winItem.getItemStack());

    }

    public void giveReward(Player player, CaseObject caseObject, ItemStack itemStack) {
        // create inventory holder with preview win item
        PreviewWinInvHandler previewWinInvHandler = new PreviewWinInvHandler(itemStack, caseConfig, caseObject);
        // open this inv
        player.openInventory(previewWinInvHandler.getInventory());
        // give winner item to player
        InventoryUtil.giveItem(player, itemStack);
        // broadcast
        broadcast(caseObject);
    }

    public void broadcast(CaseObject caseObject) {
        BroadcastCase broadcastCase = caseObject.getBroadcastCase();
        if(!broadcastCase.isEnable())
            return;

        // check message is not empty
        if(broadcastCase.getMessages().isEmpty())
            return;

        MessageUtil.broadcast(String.join("\n", broadcastCase.getMessages()));
    }

    private boolean checkRequirements(Player player, CaseObject caseObject) {
        // check case is not empty
        if(caseObject.getItems().isEmpty()) {
            MessageUtil.logMessage(ConsoleColor.RED, "Blad! Skrzynia nie posiada przedmiotow!");
            return false;
        }
        // check user has key
        if(!haskey(player, caseObject.getKeyItem())) {
            // send a message informing the user has not key
            MessageUtil.sendMessage(player, langConfig.getNoKey());
            player.playSound(player.getLocation(), caseConfig.getNoKeySound(), 1F, 1F);
            player.closeInventory();
            return false;
        }
        return true;
    }
    public Item getRandomItem(CaseObject caseObject) {
        Set<Item> items = caseObject.getItems();
        double totalWeight = items.stream().mapToDouble(Item::getChance).sum();

        // win ticket
        double randomValue = random.nextDouble() * totalWeight;

        double currentWeight = 0.0;
        for (Item item : items) {
            currentWeight += item.getChance();
            if (currentWeight >= randomValue) {
                return item;
            }
        }

       throw new RuntimeException("Nie mozna wylosowac przedmiotu!");
    }

    private boolean haskey(Player player, ItemStack itemStack)
    {
        for(ItemStack item : player.getInventory())
        {
            if(item==null)
                continue;

            if(item.isSimilar(itemStack))
                return true;
        }

        return false;
    }

    private void removeKey(Player player, ItemStack itemStack) {
        int remove = 1;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);
            if (current == null) {
                continue;
            }

            if (current.isSimilar(itemStack)) {
                int currentAmount = current.getAmount();
                if (currentAmount >= remove) {
                    current.setAmount(currentAmount - remove);
                    break;
                } else {
                    player.getInventory().setItem(i, null);
                    remove -= currentAmount;
                }
            }
        }
    }


}
