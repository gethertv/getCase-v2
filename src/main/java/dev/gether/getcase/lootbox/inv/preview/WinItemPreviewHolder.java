package dev.gether.getcase.lootbox.inv.preview;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.domain.inv.preview.WinItemPreviewConfig;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WinItemPreviewHolder extends AbstractInventoryHolder {

    LootBox lootBox;
    FileManager fileManager;
    ItemStack itemStack;
    LootBoxManager lootBoxManager;

    public WinItemPreviewHolder(JavaPlugin plugin, Player player, ItemStack itemStack, LootBoxManager lootBoxManager, FileManager fileManager, LootBox lootBox) {
        super(plugin, player, fileManager.getWinItemPreviewConfig().getPreviewInvConfig());
        this.lootBox = lootBox;
        this.fileManager = fileManager;
        this.itemStack = itemStack;
        this.lootBoxManager = lootBoxManager;

        initializeItems();
    }


    @Override
    protected void initializeItems() {
        // set open case item with and without the animation
        fillAnimationItems();
        // set winner item
        setItem(fileManager.getWinItemPreviewConfig().getWinnerItemSlot(), itemStack);
    }

    private void fillAnimationItems() {
        // config
        WinItemPreviewConfig winItemPreviewConfig = fileManager.getWinItemPreviewConfig();
        CaseConfig caseConfig = fileManager.getCaseConfig();

        // set item with animation
        winItemPreviewConfig.getAnimationSlots().forEach(slot -> {
            setItem(
                    slot,
                    caseConfig.getAnimatedOpenItem().getItemStack(),
                    event -> {
                        lootBoxManager.openCase(player, lootBox, AnimationType.SPIN);
                    }
            );
        });
        // set item without the animation/quick open
        winItemPreviewConfig.getQuickOpenSlots().forEach(slot -> {
            setItem(
                    slot,
                    caseConfig.getQuickOpenItem().getItemStack(),
                    event -> {
                        lootBoxManager.openCase(player, lootBox, AnimationType.QUICK);
                    }
            );
        });
    }

}
