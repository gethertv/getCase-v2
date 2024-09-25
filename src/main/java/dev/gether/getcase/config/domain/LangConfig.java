package dev.gether.getcase.config.domain;

import dev.gether.getutils.GetConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LangConfig extends GetConfig {
    private String noKey = "&cNo key!";
    private String caseIsDisable = "&cThis case is disabled!";
    private String fullInventory = "&cFull inventory!";

    private String noPermission = "&cNo permission! &8(&f{permission}&8)";
    private String usageCommand  = "&7Usage: #5eff96{usage}";
    private List<String> usageHelpList = List.of(
            "&7",
            "#c4ff5egetCase:",
            "&8» #5eff96/getcase reload #cfffdf- reload plugin",
            "&8» #5eff96/getcase setlocation - set block with interact case",
            "&8» #5eff96/getcase create #cfffdf- create case",
            "&8» #5eff96/getcase give #12ff64[player] [case] [amount] #cfffdf- give player a key",
            "&8» #5eff96/getcase giveall #12ff64[case] [amount] #cfffdf- give all players keys",
            "&8» #5eff96/getcase edit #12ff64[name] #cfffdf- edit drop case",
            "&8» #5eff96/getcase removelocation #cfffdf- remove block with chest interact",
            "&8» #5eff96/getcase delete #cfffdf- remove the case",
            "&8» #5eff96/getcase enable #12ff64* #cfffdf- enable all cases",
            "&8» #5eff96/getcase enable #12ff64[name] #cfffdf- enable this case",
            "&8» #5eff96/getcase disable #12ff64* #cfffdf- disable all cases",
            "&8» #5eff96/getcase disable #12ff64[name] #cfffdf- disable this case",
            "&7");
}
