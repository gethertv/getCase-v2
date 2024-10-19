package dev.gether.getcase.lootbox.inv.preview;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.domain.inv.preview.MultiCaseWinItemsPreviewConfig;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MultiWinItemPreviewHolder extends AbstractInventoryHolder {

    LootBox lootBox;
    FileManager fileManager;
    Map<Integer, ItemStack> winnerItems;
    LootBoxManager lootBoxManager;

    public MultiWinItemPreviewHolder(JavaPlugin plugin, Player player, Map<Integer, ItemStack> winnerItems, LootBoxManager lootBoxManager, FileManager fileManager, LootBox lootBox) {
        super(plugin, player, fileManager.getMultiCaseWinItemsPreviewConfig().getMultiCasePreviewInv());
        this.lootBox = lootBox;
        this.fileManager = fileManager;
        this.winnerItems = winnerItems;
        this.lootBoxManager = lootBoxManager;

        initializeItems();
    }

    @Override
    protected void initializeItems() {
        fillWinnerItems();
        fillActionItems();
    }

    private void fillWinnerItems() {
        int[] winnerItemSlots = fileManager.getMultiCaseWinItemsPreviewConfig().getWinnerItemSlots();
        int slotIndex = 0;
        for (ItemStack winItem : winnerItems.values()) {
            if (slotIndex < winnerItemSlots.length) {
                setItem(winnerItemSlots[slotIndex], winItem);
                slotIndex++;
            } else {
                break; // In case there are more winner items than slots
            }
        }
    }

    private void fillActionItems() {
        MultiCaseWinItemsPreviewConfig multiCaseWinItemsPreviewConfig = fileManager.getMultiCaseWinItemsPreviewConfig();
        CaseConfig caseConfig = fileManager.getCaseConfig();

        // Set item to open another case with animation
        multiCaseWinItemsPreviewConfig.getAnimationSlots().forEach(slot -> {
            setItem(
                    slot,
                    caseConfig.getAnimatedOpenItem().getItemStack(),
                    event -> {
                        lootBoxManager.openCase(player, lootBox, AnimationType.SPIN);
                    }
            );
        });

        // Set item to open another case without animation (quick open)
        multiCaseWinItemsPreviewConfig.getQuickOpenSlots().forEach(slot -> {
            setItem(
                    slot,
                    caseConfig.getQuickOpenItem().getItemStack(),
                    event -> {
                        lootBoxManager.openCase(player, lootBox, AnimationType.QUICK);
                    }
            );
        });

        // multi-case opening | if enabled
        if (caseConfig.isMultiCaseEnabled()) {
            multiCaseWinItemsPreviewConfig.getMultiCaseSlots().forEach(slot -> {
                setItem(
                        slot,
                        caseConfig.getMultiCaseOpening().getItemStack(),
                        event -> {
                            lootBoxManager.openMultiCaseInv(player, lootBox);
                        }
                );
            });
        }
    }
}