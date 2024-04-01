package dev.gether.getcase.cmd.arguments;

import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.manager.CaseManager;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import java.util.Optional;

public class CaseArg extends ArgumentResolver<CommandSender, CaseObject> {

    private final CaseManager caseManager;

    public CaseArg(CaseManager caseManager) {
        this.caseManager = caseManager;
    }

    @Override
    protected ParseResult<CaseObject> parse(Invocation<CommandSender> invocation, Argument<CaseObject> context, String argument) {
        Optional<CaseObject> caseByName = this.caseManager.findCaseByName(argument);
        return caseByName.map(ParseResult::success).orElseGet(() -> ParseResult.failure("&cPodana skrzynia nie istnieje!"));
    }
    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<CaseObject> argument, SuggestionContext context) {
        return caseManager.getAllNameSuggestionOfCase();
    }

}
