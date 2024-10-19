package dev.gether.getcase.lootbox.location;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.model.CaseLocation;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getutils.utils.MessageUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationCaseManager {

    FileManager fileManager;
    LootBoxManager lootBoxManager;
    HookManager hookManager;
    Map<String, Hologram> holograms = new HashMap<>();

    public LocationCaseManager(FileManager fileManager, LootBoxManager lootBoxManager, HookManager hookManager) {
        this.fileManager = fileManager;
        this.lootBoxManager = lootBoxManager;
        this.hookManager = hookManager;
    }

    // find object by location
    public Optional<LootBox> findCaseByLocation(Location location) {
        return lootBoxManager.findCaseByLocation(location);
    }

    public void createLocationCase(Player player, LootBox lootBox) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock.getType() == Material.AIR) {
            MessageUtil.sendMessage(player, "&cYou have to look at the block");
            return;
        }

        // location block where player at looking
        Location location = targetBlock.getLocation();
        // check the location is in use
        Optional<LootBox> lootBoxOptional = findCaseByLocation(location);
        // if exists then cancel
        if(lootBoxOptional.isPresent()) {
            MessageUtil.sendMessage(player, "&cThere already exists case with location");
            return;
        }

        // create hologram
        CaseLocation.CaseHologram caseHologram = CaseLocation.CaseHologram.builder()
                // check hook hologram plugin
                .hologramKey(lootBox.getCaseName()+"_"+UUID.randomUUID())
                .enable(hookManager.isDecentHologramsEnabled())
                .lines(List.of("&7-----------------", "#eaff4fCase " + lootBox.getCaseName(), "&7-----------------"))
                .heightY(2.1)
                .location(location)
                .build();


        CaseLocation caseLocation = lootBox.getCaseLocation();
        caseLocation.getCaseHolograms().add(caseHologram);

        // create hologram
        createHologram(location, caseHologram);

        lootBox.save();
        MessageUtil.sendMessage(player, "&aSuccessfully created the case with this location.");
    }

    public boolean removeLocation(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock.getType() == Material.AIR)
            return false;

        // location block where player at looking
        Location location = targetBlock.getLocation();
        Optional<LootBox> lootBoxOptional = findCaseByLocation(location);
        if(lootBoxOptional.isEmpty())
            return false;

        LootBox lootBox = lootBoxOptional.get();
        lootBox.getCaseLocation().getCaseHolograms().removeIf(caseHologram -> {
            if(caseHologram.getLocation().equals(location)) {
                deleteHologram(caseHologram.getHologramKey());
                return true;
            }
            return false;
        });
        lootBox.save();
        return true;
    }

    // create hologram
    // foreach cases
    public void createHolograms() {
        if(!hookManager.isDecentHologramsEnabled())
            return;

        if(lootBoxManager.getCases().isEmpty())
            return;

        lootBoxManager.getCases().forEach(lootBox -> {
            CaseLocation caseLocation = lootBox.getCaseLocation();
            if(caseLocation.getCaseHolograms().isEmpty())
                return;

            caseLocation.getCaseHolograms().forEach(hologram -> {
                createHologram(hologram.getLocation(), hologram);
            });
        });
    }

    public void createHologram(Location location, CaseLocation.CaseHologram caseHologram) {

        // check hook decent holograms
        if(!GetCase.getInstance().getHookManager().isDecentHologramsEnabled())
            return;

        // hologram is enable
        if(!caseHologram.isEnable())
            return;

        // create hologram
        Hologram hologram = DHAPI.createHologram(
                caseHologram.getHologramKey(),
                location.clone().add(0.5, caseHologram.getHeightY(), 0.5),
                caseHologram.getLines());

        holograms.put(caseHologram.getHologramKey(), hologram);
    }

    public void deleteHologram(String hologramKey) {
        Hologram hologram = holograms.get(hologramKey);
        if(hologram != null) {
            hologram.destroy();
        }
    }

    public List<Hologram> getHolograms() {
        return holograms.values().stream().toList();
    }
}
