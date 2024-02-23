package dev.gether.getcase.cmd;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.manager.AdminEditManager;
import dev.gether.getcase.manager.CaseManager;
import dev.gether.getcase.manager.LocationCaseManager;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import dev.rollczi.litecommands.platform.LiteSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Route(name = "getcase", aliases = "case")
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

    @Execute(route = "create")
    public void createCase(Player player, @Arg @Name("nazwa skrzynki") String caseName) {
        // check case exists
        Optional<CaseObject> caseByName = caseManager.findCaseByName(caseName);
        if(caseByName.isPresent()) {
            MessageUtil.sendMessage(player, "&cPodana skrzynka juz istnieje!");
            return;
        }
        // if case not exists then create
        boolean success = caseManager.createCase(caseName);
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie stworzono skrzynke!");
        else
            MessageUtil.sendMessage(player, "&cWystapil problem.");
    }


    @Execute(route = "enable")
    public void enableCase(Player player, @Arg @Name("nazwa skrzynki") CaseObject caseObject) {
        // check case exists
        boolean success = caseManager.enableCase(caseObject);
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie wlaczono skrzynke!");
        else
            MessageUtil.sendMessage(player, "&cWystapil problem.");
    }

    @Execute(route = "enable *")
    public void enableAllCases(Player player) {
        // check case exists
        boolean success = caseManager.enableAllCases();
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie wlaczono wszystkie skrzynki!");
        else
            MessageUtil.sendMessage(player, "&cWystapil problem.");
    }



    @Execute(route = "disable")
    public void disableCase(Player player, @Arg @Name("nazwa skrzynki") CaseObject caseObject) {
        // check case exists
        boolean success = caseManager.disableCase(caseObject);
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie wylaczono skrzynke!");
        else
            MessageUtil.sendMessage(player, "&cWystapil problem.");
    }

    @Execute(route = "disable *")
    public void disableAllCases(Player player) {
        // check case exists
        boolean success = caseManager.disableAllCases();
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie wylaczono wszystkie skrzynki!");
        else
            MessageUtil.sendMessage(player, "&cWystapil problem.");
    }

    @Execute(route = "setlocation")
    public void setLocation(Player player, @Arg @Name("case") CaseObject caseName) {
        locationCaseManager.createLocationCase(player, caseName);
    }

    @Execute(route = "give")
    public void giveKey(LiteSender sender, @Arg @Name("player") Player target, @Arg @Name("case") CaseObject caseObject, @Arg @Name("ilosc") int amount) {
        boolean success = caseManager.givePlayerKey(target, caseObject, amount);
        if(success) {
            MessageUtil.sendMessage(sender, "&aPomyslnie nadano klucz!");
        }
    }

    @Execute(route = "giveall")
    public void giveAllKey(LiteSender sender, @Arg @Name("case") CaseObject caseObject, @Arg @Name("ilosc") int amount) {
        boolean success = caseManager.giveAllKey(caseObject, amount);
        if(success) {
            MessageUtil.sendMessage(sender, "&aPomyslnie nadano wszystkim klucze!");
        }
    }


    @Execute(route = "reload")
    public void reloadPlugin(LiteSender sender) {
        boolean success = getCase.reloadPlugin();
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie przeladowano config!");
    }

    @Execute(route = "edit")
    public void edit(Player player, @Arg @Name("case") CaseObject caseName) {
        adminEditManager.editCase(player, caseName);
    }

    @Execute(route = "delete")
    public void delete(LiteSender sender, @Arg @Name("case") CaseObject caseName) {
        boolean success = caseManager.deleteCase(caseName, locationCaseManager);
        if(success)
            MessageUtil.sendMessage(sender, "&aPomyslnie usunieto skrzynie!");
        else
            MessageUtil.sendMessage(sender, "&cNie udalo się usunac!");
    }
    @Execute(route = "removelocation")
    public void remove(Player player) {
        boolean success = locationCaseManager.removeLocation(player);
        if(success)
            MessageUtil.sendMessage(player, "&aPomyslnie usunieto skrzynie!");
        else
            MessageUtil.sendMessage(player, "&cNie znaleziono tutaj skrzynki!");
    }
}
