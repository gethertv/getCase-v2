package dev.gether.getcase.lootbox.addons;

import dev.gether.getcase.config.domain.chest.CaseHologram;
import org.bukkit.Location;

public interface IHologram {
    Object create(Location location, CaseHologram caseHologram);
    void delete(Object hologram);
}
