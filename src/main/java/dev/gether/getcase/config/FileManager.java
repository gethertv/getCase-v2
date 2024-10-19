package dev.gether.getcase.config;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.*;
import dev.gether.getcase.config.domain.inv.preview.MultiCaseWinItemsPreviewConfig;
import dev.gether.getcase.config.domain.inv.preview.WinItemPreviewConfig;
import dev.gether.getcase.config.domain.inv.spinning.MultiCaseSpinningInvConfig;
import dev.gether.getcase.config.domain.inv.spinning.SpinningInvConfig;
import dev.gether.getutils.ConfigManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.File;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileManager {


    // configuration/files
    CaseConfig caseConfig;
    LangConfig langConfig;
    AnimationSpinConfig animationSpinConfig;
    MultiCaseOpeningConfig multiCaseOpeningConfig;

    // spinning
    MultiCaseSpinningInvConfig multiCaseSpinningInvConfig;
    SpinningInvConfig spinningInvConfig;

    // previews
    WinItemPreviewConfig winItemPreviewConfig;
    MultiCaseWinItemsPreviewConfig multiCaseWinItemsPreviewConfig;

    // database
    DatabaseConfig databaseConfig;

    // path to cases
    public static File FILE_PATH_CASES;

    public FileManager(GetCase plugin) {
        // init folder with cases
        FILE_PATH_CASES = new File(plugin.getDataFolder() + "/cases/");

        caseConfig = ConfigManager.create(CaseConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder(), "config.yml"));
            it.load();
        });

        langConfig = ConfigManager.create(LangConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder(), "lang.yml"));
            it.load();
        });

        animationSpinConfig = ConfigManager.create(AnimationSpinConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder()+"/animation/", "animation-spin.yml"));
            it.load();
        });

        multiCaseOpeningConfig = ConfigManager.create(MultiCaseOpeningConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder(), "multi-case-opening.yml"));
            it.load();
        });

        // spinning
        multiCaseSpinningInvConfig = ConfigManager.create(MultiCaseSpinningInvConfig.class,it -> {
            it.setFile(new File(plugin.getDataFolder()+"/spinning/", "multi-case.yml"));
            it.load();
        });

        spinningInvConfig = ConfigManager.create(SpinningInvConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder()+"/spinning/", "normal-case.yml"));
            it.load();
        });


        // previews
        winItemPreviewConfig = ConfigManager.create(WinItemPreviewConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder()+"/previews/", "normal-preview.yml"));
            it.load();
        });

        multiCaseWinItemsPreviewConfig = ConfigManager.create(MultiCaseWinItemsPreviewConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder()+"/previews/", "multi-case-preview.yml"));
            it.load();
        });

        // database
        databaseConfig = ConfigManager.create(DatabaseConfig.class, it -> {
            it.setFile(new File(plugin.getDataFolder(), "database.yml"));
            it.load();
        });


    }

    public void reload() {
        caseConfig.load();
        langConfig.load();
        animationSpinConfig.load();
        multiCaseOpeningConfig.load();

        // spinning
        multiCaseSpinningInvConfig.load();
        spinningInvConfig.load();

        // previews
        winItemPreviewConfig.load();
        multiCaseWinItemsPreviewConfig.load();

        // database
        databaseConfig.load();
    }
}
