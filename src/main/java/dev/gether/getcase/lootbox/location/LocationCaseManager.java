package dev.gether.getcase.lootbox.location;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.config.domain.CaseLocation;
import dev.gether.getcase.config.domain.chest.CaseHologram;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.addons.AddonsManager;
import dev.gether.getcase.lootbox.addons.FancyBillboardType;
import dev.gether.getconfig.utils.MessageUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.enums.EnumFlag;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LocationCaseManager {

    private final FileManager fileManager;
    private final LootBoxManager lootBoxManager;
    private final Map<String, dev.gether.getcase.lootbox.CaseHologram> holograms = new HashMap<>();
    private final AddonsManager addonsManager;


    public LocationCaseManager(FileManager fileManager, LootBoxManager lootBoxManager, AddonsManager addonsManager) {
        this.fileManager = fileManager;
        this.lootBoxManager = lootBoxManager;
        this.addonsManager = addonsManager;
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
                .enable(this.addonsManager.isHologramSupport())
                .lines(List.of("&7-----------------", "#eaff4fCase " + caseData.getName(), "&7-----------------"))
                .heightY(2.1)
                .fancyBillboardType(FancyBillboardType.HORIZONTAL)
                .color(Color.fromRGB(255, 255, 255))
                .visibilityDistance(32)
                .textShadow(true)
                .scale(1.0f)
                .transparentBackground(true)
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
        createHologram(location, caseData.getCaseId(), caseHologram);
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
        if(!this.addonsManager.isHologramSupport())
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

            LootBox lootBox = caseByID.get();
            // location case/hologram
            Location location = caseLocation.getLocation();
            // create hologram for this case
            createHologram(location, lootBox.getCaseId(), caseLocation.getCaseHologram());
        });
    }

    public void createHologram(Location location, UUID caseUUID, CaseHologram caseHologram) {

        // check hook decent holograms
        if(!this.addonsManager.isHologramSupport())
            return;

        // hologram is enable
        if(!caseHologram.isEnable())
            return;

        Object hologram = addonsManager.getHologram().create(location, caseHologram);

        dev.gether.getcase.lootbox.CaseHologram caseH = new dev.gether.getcase.lootbox.CaseHologram(hologram, caseUUID);

        // create hologram
        holograms.put(caseHologram.getHologramKey(), caseH);
    }

    public void deleteAllHolograms() {
        for (dev.gether.getcase.lootbox.CaseHologram hologram : holograms.values()) {
            deleteHologram(hologram);
        }
    }

    public void deleteHologram(String hologramKey) {
        dev.gether.getcase.lootbox.CaseHologram hologram = holograms.get(hologramKey);
        deleteHologram(hologram);
    }

    public void deleteHologram(dev.gether.getcase.lootbox.CaseHologram caseHologram) {
        this.addonsManager.getHologram().delete(caseHologram.getHologram());
    }

    public List<dev.gether.getcase.lootbox.CaseHologram> getHolograms() {
        return holograms.values().stream().toList();
    }

    public void saveFile() {
        fileManager.getCaseLocationConfig().save();
    }

    public void showHologram(UUID caseId) {
        Optional<dev.gether.getcase.lootbox.CaseHologram> first = getHolograms().stream().filter(caseHologram -> caseHologram.getCaseUUID().equals(caseId)).findFirst();
        if(first.isEmpty())
            return;

        dev.gether.getcase.lootbox.CaseHologram caseHologram = first.get();
        if(caseHologram.getHologram() instanceof Hologram hologram) {
            hologram.enable();
            hologram.updateAll();
        }

    }

    public List<dev.gether.getcase.lootbox.CaseHologram> hideHologram(UUID caseId) {
        List<dev.gether.getcase.lootbox.CaseHologram> collect = getHolograms().stream().filter(caseHologram -> caseHologram.getCaseUUID().equals(caseId)).toList();
        if(collect.isEmpty())
            return new ArrayList<>();

        collect.forEach(ob -> {
            if(ob.getHologram() instanceof Hologram hologram) {
                hologram.disable();
                hologram.updateAll();
            }
        });

        return collect;

    }
}
