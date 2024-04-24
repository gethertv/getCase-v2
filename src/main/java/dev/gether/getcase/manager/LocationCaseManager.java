package dev.gether.getcase.manager;

import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.CaseLocationConfig;
import dev.gether.getcase.config.domain.chest.CaseHologram;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LocationCaseManager {

    private final CaseLocationConfig caseLocationConfig;
    private final CaseManager caseManager;
    private final HookManager hookManager;

    public LocationCaseManager(CaseLocationConfig caseLocationConfig, CaseManager caseManager, HookManager hookManager) {
        this.caseLocationConfig = caseLocationConfig;
        this.caseManager = caseManager;
        this.hookManager = hookManager;
    }

    // find object by location
    public Optional<CaseLocation> findCaseByLocation(Location location) {
        return caseLocationConfig.getCaseLocationData().stream().filter(caseLocation -> caseLocation.getLocation().equals(location)).findFirst();
    }

    public void createLocationCase(Player player, LootBox caseData) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock.getType() == Material.AIR) {
            MessageUtil.sendMessage(player, "&cMusisz patrzeć na blok!");
            return;
        }

        // location block where player at looking
        Location location = targetBlock.getLocation();
        // check the location is in use
        Optional<CaseLocation> caseByLocation = findCaseByLocation(location);
        // if exists then cancel
        if(caseByLocation.isPresent()) {
            MessageUtil.sendMessage(player, "&cTutaj znajduje sie juz skrzynia!");
            return;
        }

        // create hologram
        CaseHologram caseHologram = CaseHologram.builder()
                // check hook hologram plugin
                .enable(hookManager.isDecentHologramsEnable())
                .lines(List.of("&7-----------------", "#eaff4fSkrzynia " + caseData.getName(), "&7-----------------"))
                .heightY(2.1)
                .build();


        // create case location
        CaseLocation caseLocation = CaseLocation.builder()
                .caseId(caseData.getCaseId())
                .location(location)
                .caseHologram(caseHologram)
                .build();

        // add object to set<>
        caseLocationConfig.getCaseLocationData().add(caseLocation);
        // create hologram
        caseHologram.createHologram(caseData.getName(), location);
        // save config
        caseLocationConfig.save();
        MessageUtil.sendMessage(player, "&aPomyslnie utworzono lokalizacje ze skrzynia!");
    }

    public boolean removeLocation(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock.getType() == Material.AIR)
            return false;

        // location block where player at looking
        Location location = targetBlock.getLocation();
        Optional<CaseLocation> caseByLocation = findCaseByLocation(location);
        if(caseByLocation.isEmpty())
            return false;

        // get object with caseLocation
        CaseLocation caseLocation = caseByLocation.get();
        return removeCaseLocation(caseLocation);
    }
    public boolean removeCaseLocation(CaseLocation caseLocation) {
        // remove hologram
        caseLocation.getCaseHologram().deleteHologram();
        // remove object from set<>
        caseLocationConfig.getCaseLocationData().remove(caseLocation);
        // save config
        caseLocationConfig.save();
        return true;
    }

    public List<CaseLocation> findCaseLocationById(UUID id) {
        return caseLocationConfig.getCaseLocationData().stream().filter(caseLocation -> {
            UUID caseId = caseLocation.getCaseId();
            Optional<LootBox> caseByID = caseManager.findCaseByID(caseId);
            // check case exists
            if(caseByID.isEmpty())
                return false;

            // check the argument and id case is the same
            return caseId.equals(id);
        }).toList();
    }

    // create hologram
    // for all cases
    public void createHolograms() {
        if(caseLocationConfig.getCaseLocationData().isEmpty())
            return;

        caseLocationConfig.getCaseLocationData().forEach(caseLocation -> {
            if(caseLocation==null) {
                return;
            }
            // find case by UUID
            Optional<LootBox> caseByID = caseManager.findCaseByID(caseLocation.getCaseId());
            if(caseByID.isEmpty()) {
                return;
            }
            // case object
            LootBox lootBox = caseByID.get();

            // location case/hologram
            Location location = caseLocation.getLocation();
            // create hologram for this case
            caseLocation.getCaseHologram().createHologram(lootBox.getName(), location);
        });
    }


    public void saveFile() {
        caseLocationConfig.save();
    }
}
