package dev.gether.getcase.user;

import dev.gether.getcase.config.FileManager;
import dev.gether.getcase.lootbox.LootBoxManager;
import dev.gether.getcase.lootbox.model.LootBox;
import dev.gether.getcase.storage.DatabaseType;
import dev.gether.getcase.storage.GetTable;
import dev.gether.getcase.storage.MySQL;
import dev.gether.getutils.utils.ConsoleColor;
import dev.gether.getutils.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements GetTable {

    String table = "getcase_users";
    MySQL mySQL;
    FileManager fileManager;
    LootBoxManager lootBoxManager;

    public UserService(MySQL mySQL, FileManager fileManager, LootBoxManager lootBoxManager) {
        this.mySQL = mySQL;
        this.fileManager = fileManager;
        this.lootBoxManager = lootBoxManager;

        createTable();
        updateColumns();
    }

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "uuid TEXT,"
                + "username TEXT)";

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Cannot create the table " + table + ". Error " + e.getMessage());
        }
    }

    public void updateColumnForLootBox(LootBox lootBox) {
        String caseName = lootBox.getCaseName();
        String columnName = "case_" + caseName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        String checkColumnQuery = "PRAGMA table_info(" + table + ")";
        String alterTableQuery = "ALTER TABLE " + table + " ADD COLUMN " + columnName + " INTEGER DEFAULT 0";

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(checkColumnQuery);
            boolean columnExists = false;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(columnName)) {
                    columnExists = true;
                    break;
                }
            }

            if (!columnExists) {
                stmt.execute(alterTableQuery);
                MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] Added column " + columnName + " to " + table);
            } else {
                MessageUtil.logMessage(ConsoleColor.YELLOW, "[getCase] Column " + columnName + " already exists in " + table);
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Error checking/adding column " + columnName + ": " + e.getMessage());
        }
    }

    private void updateColumns() {
        lootBoxManager.getCases().forEach(lootBox -> {
            String caseName = lootBox.getCaseName();
            String columnName = "case_" + caseName.toLowerCase().replaceAll("[^a-z0-9]", "_");

            String checkColumnQuery = "PRAGMA table_info(" + table + ")";
            String alterTableQuery = "ALTER TABLE " + table + " ADD COLUMN " + columnName + " INTEGER DEFAULT 0";

            try (Connection conn = mySQL.getHikariDataSource().getConnection();
                 Statement stmt = conn.createStatement()) {

                ResultSet rs = stmt.executeQuery(checkColumnQuery);
                boolean columnExists = false;
                while (rs.next()) {
                    if (rs.getString("name").equalsIgnoreCase(columnName)) {
                        columnExists = true;
                        break;
                    }
                }

                if (!columnExists) {
                    stmt.execute(alterTableQuery);
                    MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] Added column " + columnName + " to " + table);
                }
            } catch (SQLException e) {
                MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Error checking/adding column " + columnName + ": " + e.getMessage());
            }
        });
    }

    @Override
    public String getTable() {
        return table;
    }

    public Optional<User> loadUser(Player player) {
        String selectQuery = "SELECT * FROM " + table + " WHERE uuid = ?";
        String insertQuery = "INSERT INTO " + table + " (uuid, username) VALUES (?, ?)";

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            selectStmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String username = rs.getString("username");

                Map<String, Integer> openedCases = new HashMap<>();

                lootBoxManager.getCases().forEach(lootBox -> {
                    String caseName = lootBox.getCaseName();
                    String columnName = "case_" + caseName.toLowerCase().replaceAll("[^a-z0-9]", "_");
                    try {
                        int openedCount = rs.getInt(columnName);
                        openedCases.put(caseName, openedCount);
                    } catch (SQLException e) {
                        MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Error reading column " + columnName + ": " + e.getMessage());
                    }
                });

                return Optional.of(new User(uuid, username, openedCases));
            } else {
                insertStmt.setString(1, player.getUniqueId().toString());
                insertStmt.setString(2, player.getName());
                insertStmt.executeUpdate();

                return Optional.of(new User(player.getUniqueId(), player.getName(), new HashMap<>()));
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Cannot load/create user for " + player.getName() + ". Error " + e.getMessage());
        }
        return Optional.empty();
    }

    public void saveUser(User user) {
        StringBuilder queryBuilder = new StringBuilder("INSERT OR REPLACE INTO " + table + " (uuid, username");
        StringBuilder valuesBuilder = new StringBuilder(") VALUES (?, ?");

        List<String> caseNames = new ArrayList<>();
        lootBoxManager.getCases().forEach(lootBox -> {
            String caseName = lootBox.getCaseName();
            String columnName = "case_" + caseName.toLowerCase().replaceAll("[^a-z0-9]", "_");
            queryBuilder.append(", ").append(columnName);
            valuesBuilder.append(", ?");
            caseNames.add(caseName);
        });

        String query = queryBuilder.toString() + valuesBuilder.toString() + ")";

        try (Connection conn = mySQL.getHikariDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, user.getUuid().toString());
            stmt.setString(paramIndex++, user.getName());

            for (String caseName : caseNames) {
                int openedCount = user.getOpenedCases().getOrDefault(caseName, 0);
                stmt.setInt(paramIndex++, openedCount);
            }

            stmt.executeUpdate();
            MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] Successfully saved user " + user.getName());
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Cannot save user " + user.getName() + ". Error " + e.getMessage());
        }
    }

    public void saveUsers(List<User> users) {
        if (users.isEmpty()) {
            return;
        }

        final int BATCH_SIZE = 1000;
        StringBuilder queryBuilder = new StringBuilder("INSERT OR REPLACE INTO " + table + " (uuid, username");
        StringBuilder valuesBuilder = new StringBuilder(") VALUES (?, ?");

        List<String> caseNames = new ArrayList<>();
        lootBoxManager.getCases().forEach(lootBox -> {
            String caseName = lootBox.getCaseName();
            String columnName = "case_" + caseName.toLowerCase().replaceAll("[^a-z0-9]", "_");
            queryBuilder.append(", ").append(columnName);
            valuesBuilder.append(", ?");
            caseNames.add(caseName);
        });

        String query = queryBuilder.toString() + valuesBuilder.toString() + ")";

        try (Connection conn = mySQL.getHikariDataSource().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int count = 0;
                for (User user : users) {
                    int paramIndex = 1;
                    stmt.setString(paramIndex++, user.getUuid().toString());
                    stmt.setString(paramIndex++, user.getName());

                    for (String caseName : caseNames) {
                        int openedCount = user.getOpenedCases().getOrDefault(caseName, 0);
                        stmt.setInt(paramIndex++, openedCount);
                    }

                    stmt.addBatch();
                    count++;

                    if (count % BATCH_SIZE == 0) {
                        stmt.executeBatch();
                        stmt.clearBatch();
                        MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] Saved batch of " + BATCH_SIZE + " users.");
                    }
                }

                if (count % BATCH_SIZE != 0) {
                    stmt.executeBatch();
                }

                conn.commit();
                MessageUtil.logMessage(ConsoleColor.GREEN, "[getCase] Successfully saved all " + users.size() + " users.");
            } catch (SQLException e) {
                conn.rollback();
                MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Error saving users batch. Error: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            MessageUtil.logMessage(ConsoleColor.RED, "[getCase] Cannot establish database connection. Error: " + e.getMessage());
        }
    }
}