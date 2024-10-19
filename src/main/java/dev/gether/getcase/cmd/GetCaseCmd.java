package dev.gether.getcase.cmd;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getutils.utils.MessageUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command(name = "getcase", aliases = "case")
@Permission("getcase.admin")
public class GetCaseCmd {

    private final GetCase getCase;
    private final LootBoxManager lootBoxManager;

    public GetCaseCmd(GetCase getCase, LootBoxManager lootBoxManager) {
        this.getCase = getCase;
        this.lootBoxManager = lootBoxManager;
    }

    @Execute(name = "create")
    public void createCase(@Context CommandSender sender, @Arg("case-name") String caseName) {
        // check case exists
        Optional<LootBox> caseByName = lootBoxManager.findCaseByName(caseName);
        if(caseByName.isPresent()) {
            MessageUtil.sendMessage(sender, "&cError! Can't create a new case.");
            return;
        }
        // if case not exists then create
        lootBoxManager.createCase(caseName);
        MessageUtil.sendMessage(sender, "&aSuccessful created a new case!");
    }


    @Execute(name = "enable")
    public void enableCase(@Context CommandSender sender, @Arg("case-name") LootBox lootBox) {
        // check case exists<
        lootBoxManager.enableCase(lootBox);
        MessageUtil.sendMessage(sender, "&aSuccessful enabled the case!");
    }

    @Execute(name = "enable *")
    public void enableAllCases(@Context CommandSender sender) {
        // check case exists
        lootBoxManager.enableAllCases();
        MessageUtil.sendMessage(sender, "&aSuccessful enabled all cases!");
    }


    @Execute(name = "disable")
    public void disableCase(@Context CommandSender sender, @Arg("case-name") LootBox lootBox) {
        // check case exists
        lootBoxManager.disableCase(lootBox);
        MessageUtil.sendMessage(sender, "&aSuccessful disabled the case!");
    }

    @Execute(name = "disable *")
    public void disableAllCases(@Context CommandSender sender) {
        // check case exists
        lootBoxManager.disableAllCases();
        MessageUtil.sendMessage(sender, "&aSuccessful disabled all cases!");
    }

    @Execute(name = "setlocation")
    public void setLocation(@Context CommandSender sender, @Arg("case-name") LootBox caseName) {
        if(!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, "&cYou can't do it from console!");
            return;
        }
        lootBoxManager.getLocationCaseManager().createLocationCase(player, caseName);
    }

    @Execute(name = "give")
    public void giveKey(@Context CommandSender sender, @Arg("player") Player target, @Arg("case-name") LootBox lootBox, @Arg("amount") int amount) {
        lootBoxManager.givePlayerKey(target, lootBox, amount);
        MessageUtil.sendMessage(sender, "&aSuccessful give the keys!");
    }

    @Execute(name = "giveall")
    public void giveAllKey(@Context CommandSender sender, @Arg("case-name") LootBox lootBox, @Arg("amount") int amount) {
        lootBoxManager.giveAllKey(lootBox, amount);
        MessageUtil.sendMessage(sender, "&aSuccessful give the keys for all!");
    }

    @Execute(name = "reload")
    public void reloadPlugin(@Context CommandSender sender) {
        getCase.reloadPlugin();
        MessageUtil.sendMessage(sender, "&aSuccessful reload configs!!");
    }

    @Execute(name = "edit")
    public void edit(@Context CommandSender sender, @Arg("case-name") LootBox caseName) {
        if(!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, "&cYou can't do it from console!");
            return;
        }
        lootBoxManager.getEditLootBoxManager().editCase(player, caseName);
    }

    @Execute(name = "delete")
    public void delete(@Context CommandSender sender, @Arg("case-name") LootBox caseName) {
        lootBoxManager.deleteCase(caseName);
        MessageUtil.sendMessage(sender, "&aSuccessful delete the case!");
    }
    @Execute(name = "removelocation")
    public void remove(@Context CommandSender sender) {
        if(!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, "&cYou can't do it from console!");
            return;
        }
        boolean status = lootBoxManager.getLocationCaseManager().removeLocation(player);
        if(status) {
            MessageUtil.sendMessage(player, "&aSuccessful remove location of case!");
        } else {
            MessageUtil.sendMessage(player, "&cSomething wrong! Maybe there case location isn't exists!");
        }
    }

    @Execute(name = "preview")
    public void preview(@Context Player player, @Arg("case-name") LootBox lootBox) {
        lootBoxManager.openPreview(player, lootBox);
    }
}
