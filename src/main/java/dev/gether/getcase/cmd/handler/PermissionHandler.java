package dev.gether.getcase.cmd.handler;

import dev.gether.getcase.config.FileManager;
import dev.gether.getconfig.utils.MessageUtil;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;

public class PermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final FileManager fileManager;

    public PermissionHandler(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> chain) {
        String permissions = missingPermissions.asJoinedText();
        CommandSender sender = invocation.sender();

        MessageUtil.sendMessage(sender, fileManager.getLangConfig().getNoPermission()
                .replace("{permission}", String.join(", ", permissions))
        );
    }
}