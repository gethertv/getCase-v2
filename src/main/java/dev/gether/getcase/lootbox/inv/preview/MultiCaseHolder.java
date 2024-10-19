package dev.gether.getcase.lootbox.inv.preview;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getutils.models.inventory.AbstractInventoryHolder;
import dev.gether.getutils.models.inventory.InventoryConfig;
import org.bukkit.entity.Player;

public class MultiCaseHolder extends AbstractInventoryHolder {

    final FileManager fileManager;
    final LootBox lootBox;
    final GetCase plugin;

    public MultiCaseHolder(GetCase plugin, Player player, InventoryConfig inventoryConfig, FileManager fileManager, LootBox lootBox) {
        super(plugin, player, inventoryConfig);

        this.fileManager = fileManager;
        this.lootBox = lootBox;
        this.plugin = plugin;

        initializeItems();
    }


    @Override
    protected void initializeItems() {
        fileManager.getMultiCaseOpeningConfig().getSelectionCase().forEach((index, multiCase) -> {
            multiCase.getSlots().forEach(slot -> {
                setItem(slot, multiCase.getItem().getItemStack(), event -> {
                    //
                    plugin.getLootBoxManager().openMultiCase(player, lootBox, index);
                });
            });
        });
    }
}