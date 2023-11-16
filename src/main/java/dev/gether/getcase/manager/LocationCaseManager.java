package dev.gether.getcase.manager;

import dev.gether.getcase.config.CaseLocationConfig;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.utils.MessageUtil;
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

    public LocationCaseManager(CaseLocationConfig caseLocationConfig, CaseManager caseManager) {
        this.caseLocationConfig = caseLocationConfig;
        this.caseManager = caseManager;
    }

    // find object by location
    public Optional<CaseLocationConfig.CaseLocation> findCaseByLocation(Location location) {
        return caseLocationConfig.getCaseLocationData().stream().filter(caseLocation -> caseLocation.getLocation().equals(location)).findFirst();
    }

    public void createLocationCase(Player player, CaseObject caseData) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock.getType() == Material.AIR) {
            MessageUtil.sendMessage(player, "&cMusisz patrzeć na blok!");
            return;
        }

        // location block where player at looking
        Location location = targetBlock.getLocation();
        // check the location is in use
        Optional<CaseLocationConfig.CaseLocation> caseByLocation = findCaseByLocation(location);
        // if exists then cancel
        if(caseByLocation.isPresent()) {
            MessageUtil.sendMessage(player, "&cTutaj znajduje sie juz skrzynia!");
            return;
        }
        // create case location
        CaseLocationConfig.CaseLocation caseLocation = CaseLocationConfig.CaseLocation.builder()
                .caseId(caseData.getCaseId())
                .location(location)
                .build();

        // add object to set<>
        caseLocationConfig.getCaseLocationData().add(caseLocation);
        // create hologram
        caseData.getCaseHologram().createHologram(caseData.getName(), location);
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
        Optional<CaseLocationConfig.CaseLocation> caseByLocation = findCaseByLocation(location);
        if(caseByLocation.isEmpty())
            return false;

        // get object with caseLocation
        CaseLocationConfig.CaseLocation caseLocation = caseByLocation.get();
        return removeCaseLocation(caseLocation);
    }
    public boolean removeCaseLocation(CaseLocationConfig.CaseLocation caseLocation) {
        // find case by ID
        Optional<CaseObject> caseByName = caseManager.findCaseByID(caseLocation.getCaseId());
        if(caseByName.isEmpty()) {
            return false;
        }
        // case object
        CaseObject caseObject = caseByName.get();
        // remove hologram
        caseObject.getCaseHologram().deleteHologram();
        // remove object from set<>
        caseLocationConfig.getCaseLocationData().remove(caseLocation);
        // save config
        caseLocationConfig.save();
        return true;
    }

    public List<CaseLocationConfig.CaseLocation> findCaseLocationById(UUID id) {
        return caseLocationConfig.getCaseLocationData().stream().filter(caseLocation -> {
            UUID caseId = caseLocation.getCaseId();
            Optional<CaseObject> caseByID = caseManager.findCaseByID(caseId);
            // check case exists
            if(caseByID.isEmpty())
                return false;

            // check the argument and id case is the same
            if(caseId.equals(id))
                return true;

            return false;
        }).toList();
    }

    // create hologram
    // for all cases
    public void createHolograms() {
        caseLocationConfig.getCaseLocationData().forEach(caseLocation -> {
            // find case by UUID
            Optional<CaseObject> caseByID = caseManager.findCaseByID(caseLocation.getCaseId());
            if(caseByID.isEmpty()) {
                return;
            }
            // case object
            CaseObject caseObject = caseByID.get();
            // location case/hologram
            Location location = caseLocation.getLocation();
            // create hologram for this case
            caseObject.getCaseHologram().createHologram(caseObject.getName(), location);
        });
    }


    public void saveFile() {
        caseLocationConfig.save();
    }
}
