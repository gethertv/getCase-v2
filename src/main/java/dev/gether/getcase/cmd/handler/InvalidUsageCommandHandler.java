package dev.gether.getcase.cmd.handler;

import dev.gether.getcase.config.FileManager;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class InvalidUsageCommandHandler implements InvalidUsageHandler<CommandSender> {

    private final FileManager fileManager;

    public InvalidUsageCommandHandler(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.all().size() == 1) {
            String first = schematic.first();
            String[] split = first.split("\\|");
            if(split.length<=1) {
                MessageUtil.sendMessage(sender, fileManager.getLangConfig().getUsageCommand().replace("{usage}", first));
                return;
            }
        }
        MessageUtil.sendMessage(sender, fileManager.getLangConfig().getUsageHelpList());
    }
}
