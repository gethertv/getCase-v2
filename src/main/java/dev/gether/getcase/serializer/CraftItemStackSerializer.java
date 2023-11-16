package dev.gether.getcase.serializer;

import dev.gether.getcase.utils.ConsoleColor;
import dev.gether.getcase.utils.MessageUtil;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;

public class CraftItemStackSerializer implements ObjectSerializer<ItemStack> {

    private static final Yaml YAML = new Yaml();

    @Override
    public boolean supports(Class<? super ItemStack> type) {
        return ItemStack.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ItemStack stack, SerializationData data, GenericsDeclaration generics) {

        YamlConfiguration craftConfig = new YamlConfiguration();
        craftConfig.set("_", stack);

        Map<String, Map<String, Object>> root = YAML.load(craftConfig.saveToString());
        Map<String, Object> itemMap = root.get("_");

        itemMap.remove("==");
        itemMap.forEach(data::add);
    }

    @Override
    public ItemStack deserialize(DeserializationData data, GenericsDeclaration generics) {

        Map<String, Object> itemMap = new LinkedHashMap<>();
        itemMap.put("==", "org.bukkit.inventory.ItemStack");
        itemMap.putAll(data.asMap());

        YamlConfiguration craftConfig = new YamlConfiguration();
        craftConfig.set("_", itemMap);
        try {
            craftConfig.loadFromString(craftConfig.saveToString());
        } catch (InvalidConfigurationException e) {
            MessageUtil.logMessage(ConsoleColor.RED, e.getMessage());
        }

        return craftConfig.getItemStack("_");
    }
}