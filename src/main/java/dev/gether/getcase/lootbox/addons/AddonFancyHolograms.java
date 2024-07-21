package dev.gether.getcase.lootbox.addons;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.gether.getcase.config.domain.chest.CaseHologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

public class AddonFancyHolograms implements IHologram {

    HologramManager hologramManager = FancyHologramsPlugin.get().getHologramManager();

    @Override
    public Object create(Location location, CaseHologram caseHologram) {

        TextHologramData textHologramData = new TextHologramData(caseHologram.getHologramKey(),
                location.clone().add(0.5, caseHologram.getHeightY(), 0.5));

        textHologramData.setText(caseHologram.getLines());
        textHologramData.setTextShadow(caseHologram.isTextShadow());
        if(caseHologram.isTransparentBackground()) {
            textHologramData.setBackground(Hologram.TRANSPARENT);
        } else if(caseHologram.getColor() != null) {
            textHologramData.setBackground(caseHologram.getColor());
        }
        textHologramData.setScale(new Vector3f(caseHologram.getScale(), caseHologram.getScale(), caseHologram.getScale()));
        textHologramData.setVisibilityDistance(caseHologram.getVisibilityDistance());
        textHologramData.setBillboard(Display.Billboard.valueOf(caseHologram.getFancyBillboardType().name().toUpperCase()));
        Hologram hologram = hologramManager.create(textHologramData);
        hologramManager.addHologram(hologram);
        return hologram;

    }
    @Override
    public void delete(Object hologram) {
        if(hologram == null) {
            throw new NullPointerException("Cannot delete! Hologram is null.");
        }
        Hologram holo = (Hologram) hologram;
        hologramManager.removeHologram(holo);
    }
}
