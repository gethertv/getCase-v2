package dev.gether.getcase.cmd.arguments;

import dev.gether.getcase.config.chest.CaseObject;
import dev.gether.getcase.manager.CaseManager;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ArgumentName("case")
public class CaseArg implements OneArgument<CaseObject> {

    private final CaseManager caseManager;

    public CaseArg(CaseManager caseManager) {
        this.caseManager = caseManager;
    }

    @Override
    public Result<CaseObject, Object> parse(LiteInvocation invocation, String argument) {
        Optional<CaseObject> caseByName = this.caseManager.findCaseByName(argument);
        return caseByName.map(Result::ok).orElseGet(() -> Result.error("&cPodana skrzynia nie istnieje!"));

    }
    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return invocation.lastArgument()
                .map(text -> caseManager.getAllNameSuggestionOfCase())
                .orElse(Collections.emptyList());
    }

}
