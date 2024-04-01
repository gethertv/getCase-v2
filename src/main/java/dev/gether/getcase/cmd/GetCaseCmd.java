package dev.gether.getcase.cmd;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.manager.AdminEditManager;
import dev.gether.getcase.manager.CaseManager;
import dev.gether.getcase.manager.LocationCaseManager;
import dev.gether.getconfig.utils.MessageUtil;
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
    private final CaseManager caseManager;
    private final LocationCaseManager locationCaseManager;
    private final AdminEditManager adminEditManager;

    public GetCaseCmd(GetCase getCase, CaseManager caseManager, LocationCaseManager locationCaseManager, AdminEditManager adminEditManager) {
        this.getCase = getCase;
        this.caseManager = caseManager;
        this.locationCaseManager = locationCaseManager;
        this.adminEditManager = adminEditManager;
    }

    @Execute(name = "create")
    public void createCase(@Context CommandSender sender, @Arg("nazwa skrzynki") String caseName) {
        // check case exists
        Optional<CaseObject> caseByName = caseManager.findCaseByName(caseName);
        if(caseByName.isPresent()) {
            MessageUtil.sendMessage(sender, "&cPodana skrzynka juz istnieje!");
            return;
        }
        // if case not exists then create
        boolean success = caseManager.createCase(caseName);
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie stworzono skrzynke!");
        else
            MessageUtil.sendMessage(sender, "&cWystapil problem.");
    }


    @Execute(name = "enable")
    public void enableCase(@Context CommandSender sender, @Arg("nazwa skrzynki") CaseObject caseObject) {
        // check case exists
        boolean success = caseManager.enableCase(caseObject);
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie wlaczono skrzynke!");
        else
            MessageUtil.sendMessage(sender, "&cWystapil problem.");
    }

    @Execute(name = "enable *")
    public void enableAllCases(@Context CommandSender sender) {
        // check case exists
        boolean success = caseManager.enableAllCases();
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie wlaczono wszystkie skrzynki!");
        else
            MessageUtil.sendMessage(sender, "&cWystapil problem.");
    }


    @Execute(name = "disable")
    public void disableCase(@Context CommandSender sender, @Arg("nazwa skrzynki") CaseObject caseObject) {
        // check case exists
        boolean success = caseManager.disableCase(caseObject);
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie wylaczono skrzynke!");
        else
            MessageUtil.sendMessage(sender, "&cWystapil problem.");
    }

    @Execute(name = "disable *")
    public void disableAllCases(@Context CommandSender sender) {
        // check case exists
        boolean success = caseManager.disableAllCases();
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie wylaczono wszystkie skrzynki!");
        else
            MessageUtil.sendMessage(sender, "&cWystapil problem.");
    }

    @Execute(name = "setlocation")
    public void setLocation(@Context CommandSender sender, @Arg("case") CaseObject caseName) {
        if(!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, "&cNie mozesz tego przez konsole!");
            return;
        }
        locationCaseManager.createLocationCase(player, caseName);
    }

    @Execute(name = "give")
    public void giveKey(@Context CommandSender sender, @Arg("player") Player target, @Arg("case") CaseObject caseObject, @Arg("ilosc") int amount) {
        boolean success = caseManager.givePlayerKey(target, caseObject, amount);
        if(success) {
            MessageUtil.sendMessage(sender, "&aPomyslnie nadano klucz!");
        }
    }

    @Execute(name = "giveall")
    public void giveAllKey(@Context CommandSender sender, @Arg("case") CaseObject caseObject, @Arg("ilosc") int amount) {
        boolean success = caseManager.giveAllKey(caseObject, amount);
        if(success) {
            MessageUtil.sendMessage(sender, "&aPomyslnie nadano wszystkim klucze!");
        }
    }

    @Execute(name = "reload")
    public void reloadPlugin(@Context CommandSender sender) {
        boolean success = getCase.reloadPlugin();
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie przeladowano config!");
    }

    @Execute(name = "edit")
    public void edit(@Context CommandSender sender, @Arg("case") CaseObject caseName) {
        if(!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, "&cNie mozesz tego przez konsole!");
            return;
        }
        adminEditManager.editCase(player, caseName);
    }

    @Execute(name = "delete")
    public void delete(@Context CommandSender sender, @Arg("case") CaseObject caseName) {
        boolean success = caseManager.deleteCase(caseName, locationCaseManager);
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie usunieto skrzynie!");
        else
            MessageUtil.sendMessage(sender, "&cNie udalo się usunac!");
    }
    @Execute(name = "removelocation")
    public void remove(@Context CommandSender sender) {
        if(!(sender instanceof Player player)) {
            MessageUtil.sendMessage(sender, "&cNie mozesz tego przez konsole!");
            return;
        }
        boolean success = locationCaseManager.removeLocation(player);
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie usunieto skrzynie!");
        else
            MessageUtil.sendMessage(player, "&cNie znaleziono tutaj skrzynki!");
    }
}
