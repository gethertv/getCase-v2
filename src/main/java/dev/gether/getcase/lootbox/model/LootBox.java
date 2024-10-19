package dev.gether.getcase.lootbox.model;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.lootbox.LootboxType;
import dev.gether.getcase.lootbox.animation.Animation;
import dev.gether.getutils.GetConfig;
import dev.gether.getutils.models.Item;
import dev.gether.getutils.models.inventory.InventoryConfig;
import dev.gether.getutils.shaded.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// class represent object CASE
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LootBox extends GetConfig {

    transient ItemStack itemKey;
    // type case/lootbox may be a luckblock or normal case
    LootboxType lootboxType;

    Item item;
    boolean enable;
    UUID caseId;
    String caseName;

    // item key
    Set<ItemCase> items;

    // animation data
    Animation animation;
    // broadcast
    BroadcastCase broadcastCase;

    InventoryConfig previewInventoryConfig;

    @JsonIgnore
    transient NamespacedKey namespacedKey;

    @JsonIgnore
    transient CaseLocation caseLocation;

    @Override
    public void save() {
        super.save();
        caseLocation.save();
    }

    @JsonIgnore
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @JsonIgnore
    public CaseLocation getCaseLocation() {
        return caseLocation;
    }

    @Override
    public void load() {
        super.load();
        namespacedKey = new NamespacedKey(GetCase.getInstance(), caseId.toString());

        itemKey = item.getItemStack();
        ItemMeta itemMeta = itemKey.getItemMeta();
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, caseId.toString());
        itemKey.setItemMeta(itemMeta);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BroadcastCase {
        private boolean enable;
        private List<String> messages;
    }

}
