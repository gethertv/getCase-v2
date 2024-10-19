package dev.gether.getcase.user;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.storage.MySQL;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserManager {

    GetCase plugin;
    UserService userService;

    Map<UUID, User> users = new HashMap<>();

    public UserManager(GetCase plugin, LootBoxManager lootBoxManager, MySQL mySQL) {
        this.plugin = plugin;
        this.userService = new UserService(mySQL, plugin.getFileManager(), lootBoxManager);


        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveUsersAsync, 20L * 2, 20L * 2);
    }

    public void loadUser(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<User> userOptional = userService.loadUser(player);

            if(userOptional.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer("[getCase] cannot load user"));
                return;
            }

            users.put(player.getUniqueId(), userOptional.get());
        });
    }

    public void saveUser(Player player) {
        User user = users.remove(player.getUniqueId());

        if(user == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            userService.saveUser(user);
            user.setUpdate(false);
        });

    }

    public Optional<User> findUserByPlayer(Player player) {
        return Optional.ofNullable(users.get(player.getUniqueId()));
    }

    public void saveUsersSync() {
        List<User> usersToUpdate  = users.values().stream().filter(User::isUpdate).toList();
        if(!usersToUpdate.isEmpty()) {
            userService.saveUsers(usersToUpdate);
            usersToUpdate.forEach(user -> user.setUpdate(false));
        }
  ;
    }
    public void saveUsersAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::saveUsersSync);
    }

    public void updateColumn(LootBox lootBox) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            userService.updateColumnForLootBox(lootBox);
        });
    }
}
