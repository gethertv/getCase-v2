package dev.gether.getcase.lootbox.addons;

import dev.gether.getcase.config.domain.chest.CaseHologram;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

public class AddonDecentHolograms implements IHologram {
    @Override
    public Object create(Location location, CaseHologram caseHologram) {

        return DHAPI.createHologram(
                caseHologram.getHologramKey(),
                location.clone().add(0.5, caseHologram.getHeightY(), 0.5),
                caseHologram.getLines());

    }

    @Override
    public void delete(Object hologram) {
        if(hologram == null) {
            throw new NullPointerException("Cannot delete! Hologram is null.");
        }
        Hologram holo = (Hologram) hologram;
        holo.delete();
    }
}
