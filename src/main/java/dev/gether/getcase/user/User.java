package dev.gether.getcase.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class User {

    UUID uuid;
    String name;
    Map<String, Integer> openedCases;
    @Setter boolean update;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.openedCases = new HashMap<>();
    }

    public User(UUID uuid, String username, Map<String, Integer> openedCases) {
        this(uuid, username);
        this.openedCases = openedCases;
    }

    public void openedCase(String caseName, int amount) {
        openedCases.merge(caseName, amount, Integer::sum);
        update = true;
    }

    public int getOpenedCase(String caseName) {
        return openedCases.getOrDefault(caseName, 0);
    }

    public int getSumOpenedCase() {
        return openedCases.values().stream().mapToInt(Integer::intValue).sum();
    }
}
