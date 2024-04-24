package dev.gether.getcase.lootbox.open;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.domain.LangConfig;
import dev.gether.getcase.lootbox.animation.AnimationType;
import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.utils.InventoryUtil;
import dev.gether.getconfig.utils.ItemUtil;
import dev.gether.getconfig.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Random;

public class OpenCaseManager {


    private final Random random;
    private final CaseConfig caseConfig;
    private final LangConfig langConfig;
    private final GetCase plugin;

    public OpenCaseManager(GetCase plugin, CaseConfig caseConfig, LangConfig langConfig) {
        this.plugin = plugin;
        this.random = new Random();
        this.caseConfig = caseConfig;
        this.langConfig = langConfig;
    }

    @Override
    // open case with animation
    public void openCase(Player player, final LootBox lootBox, AnimationType animationType) {
        // check case is enable
        if(!lootBox.isEnable()) {
            MessageUtil.sendMessage(player, langConfig.getCaseIsDisable());
            return;
        }
        // check requirements like CASE is not empty and player has key for this case
        boolean hasRequirements  =  checkRequirements(player, lootBox);
        // is not meets then return
        if(!hasRequirements)
            return;

        // take key
        ItemUtil.removeItem(player, lootBox.getKeyItemStack(), 1);

        // open case with animation
        if(animationType == AnimationType.SPIN) {
            // start animation
            animationManager.startSpin(player, lootBox);
        }
        // open case without the animation
        else if(animationType == AnimationType.QUICK) {
            // give reward
            rewardsManager.giveReward(player, lootBox);
        }

    }



}
