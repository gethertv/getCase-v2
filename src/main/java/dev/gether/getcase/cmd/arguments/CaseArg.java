package dev.gether.getcase.cmd.arguments;

import dev.gether.getcase.config.domain.chest.LootBox;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import java.util.Optional;

public class CaseArg extends ArgumentResolver<CommandSender, LootBox> {

    private final LootBoxManager lootBoxManager;

    public CaseArg(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
    }

    @Override
    protected ParseResult<LootBox> parse(Invocation<CommandSender> invocation, Argument<LootBox> context, String argument) {
        Optional<LootBox> caseByName = this.lootBoxManager.findCaseByName(argument);
        return caseByName.map(ParseResult::success).orElseGet(() -> ParseResult.failure("&cPodana skrzynia nie istnieje!"));
    }
    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<LootBox> argument, SuggestionContext context) {
        return lootBoxManager.getAllNameSuggestionOfCase();
    }

}
