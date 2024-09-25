package dev.gether.getcase.lootbox.location;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.chest.CaseHologram;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.hook.HookManager;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getutils.utils.MessageUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class LocationCaseManager {

    private final FileManager fileManager;
    private final LootBoxManager lootBoxManager;
    private final HookManager hookManager;
    private final Map<String, Hologram> holograms = new HashMap<>();

    public LocationCaseManager(FileManager fileManager, LootBoxManager lootBoxManager, HookManager hookManager) {
        this.fileManager = fileManager;
        this.lootBoxManager = lootBoxManager;
        this.hookManager = hookManager;
    }

    // find object by location
    public Optional<CaseLocation> findCaseByLocation(Location location) {
        return fileManager.getCaseLocationConfig().getCaseLocationData().stream().filter(caseLocation -> caseLocation.getLocation().equals(location)).findFirst();
    }

    public void createLocationCase(Player player, LootBox caseData) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock.getType() == Material.AIR) {
            MessageUtil.sendMessage(player, "&cYou have to look at the block");
            return;
        }

        // location block where player at looking
        Location location = targetBlock.getLocation();
        // check the location is in use
        Optional<CaseLocation> caseByLocation = findCaseByLocation(location);
        // if exists then cancel
        if(caseByLocation.isPresent()) {
            MessageUtil.sendMessage(player, "&cThere already exists case with location");
            return;
        }

        // create hologram
        CaseHologram caseHologram = CaseHologram.builder()
                // check hook hologram plugin
                .hologramKey(caseData.getName()+"_"+UUID.randomUUID())
                .enable(hookManager.isDecentHologramsEnabled())
                .lines(List.of("&7-----------------", "#eaff4fCase " + caseData.getName(), "&7-----------------"))
                .heightY(2.1)
                .build();


        // create case location
        CaseLocation caseLocation = CaseLocation.builder()
                .caseId(caseData.getCaseId())
                .location(location)
                .caseHologram(caseHologram)
                .build();

        // add object to set<>
        fileManager.getCaseLocationConfig().getCaseLocationData().add(caseLocation);
        // create hologram
        createHologram(location, caseHologram);
        // save config
        fileManager.getCaseLocationConfig().save();
        MessageUtil.sendMessage(player, "&aSuccessfully created the case with this location.");
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
        removeCaseLocation(caseLocation);
        return true;
    }
    public void removeCaseLocation(CaseLocation caseLocation) {
        // remove hologram
        deleteHologram(caseLocation.getCaseHologram().getHologramKey());
        // remove object from set<>
        fileManager.getCaseLocationConfig().getCaseLocationData().remove(caseLocation);
        // save config
        fileManager.getCaseLocationConfig().save();
    }

    public List<CaseLocation> findCaseLocationById(UUID id) {
        return fileManager.getCaseLocationConfig().getCaseLocationData().stream().filter(caseLocation -> {
            UUID caseId = caseLocation.getCaseId();
            Optional<LootBox> caseByID = lootBoxManager.findCaseByID(caseId);
            // check case exists
            if(caseByID.isEmpty())
                return false;

            // check the argument and id case is the same
            return caseId.equals(id);
        }).toList();
    }

    // create hologram
    // foreach cases
    public void createHolograms() {
        if(!hookManager.isDecentHologramsEnabled())
            return;

        if(fileManager.getCaseLocationConfig().getCaseLocationData().isEmpty())
            return;

        fileManager.getCaseLocationConfig().getCaseLocationData().forEach(caseLocation -> {
            if(caseLocation==null) {
                return;
            }
            // find case by UUID
            Optional<LootBox> caseByID = lootBoxManager.findCaseByID(caseLocation.getCaseId());
            if(caseByID.isEmpty()) {
                return;
            }

            // location case/hologram
            Location location = caseLocation.getLocation();
            // create hologram for this case
            createHologram(location, caseLocation.getCaseHologram());
        });
    }

    public void createHologram(Location location, CaseHologram caseHologram) {

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

    public void saveFile() {
        fileManager.getCaseLocationConfig().save();
    }
}
