package dev.gether.getcase.placeholder;

import dev.gether.getcase.user.User;
import dev.gether.getcase.user.UserManager;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CasePlaceholder extends PlaceholderExpansion {

   UserManager userManager;


    public CasePlaceholder(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();

        Optional<User> userByPlayer = userManager.findUserByPlayer(player);
        if(userByPlayer.isEmpty()) return null;
        User user = userByPlayer.get();

        if(params.startsWith("opened")) {

            String[] args = params.split("_");
            if(args.length == 2) {
                String caseName = args[1];
                return String.valueOf(user.getOpenedCase(caseName));
            }
        }
        if(params.equalsIgnoreCase("total_opened")) {
            return String.valueOf(user.getSumOpenedCase());
        }

        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "getcase";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gether";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }
}
