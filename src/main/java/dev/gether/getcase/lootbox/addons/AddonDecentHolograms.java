package dev.gether.getcase.lootbox.addons;

import dev.gether.getcase.config.domain.chest.CaseHologram;
import dev.gether.getconfig.utils.MessageUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.enums.EnumFlag;
import org.bukkit.Location;

public class AddonDecentHolograms implements IHologram {
    @Override
    public Object create(Location location, CaseHologram caseHologram) {

        Hologram hologram = DHAPI.createHologram(
                caseHologram.getHologramKey(),
                location.clone().add(0.5, caseHologram.getHeightY(), 0.5),
                false,
                caseHologram.getLines());


        hologram.addFlags(EnumFlag.DISABLE_UPDATING);

        return hologram;

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
