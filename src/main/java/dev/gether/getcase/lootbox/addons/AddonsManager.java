package dev.gether.getcase.lootbox.addons;

import dev.gether.getcase.GetCase;

import java.util.List;

public class AddonsManager {

    private boolean fancyHologramsEnable = false;
    private boolean decentHologramsEnable = false;
    private IHologram hologram;

    public AddonsManager(GetCase plugin) {

        // enable DecentHolograms
        if(plugin.getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
            this.decentHologramsEnable = true;
            this.hologram = new AddonDecentHolograms();
        } else if(plugin.getServer().getPluginManager().isPluginEnabled("FancyHolograms")) {
            this.fancyHologramsEnable = true;
            //this.hologram = new AddonFancyHolograms();
        }
    }

    public IHologram getHologram() {
        return hologram;
    }

    public boolean isHologramSupport() {
        return decentHologramsEnable || fancyHologramsEnable;
    }

}
