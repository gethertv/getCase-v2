package dev.gether.getcase.lootbox;

import dev.gether.getcase.config.domain.chest.LootBox;
import org.bukkit.entity.Player;

public interface ILootboxManager {

    void openCase(Player player, LootBox lootBox);
    void editCase(LootBox lootBox);

}
