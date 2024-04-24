package dev.gether.getcase.config;

import dev.gether.getcase.GetCase;
import dev.gether.getcase.config.domain.CaseConfig;
import dev.gether.getcase.config.domain.CaseLocationConfig;
import dev.gether.getcase.config.domain.LangConfig;
import dev.gether.getconfig.ConfigManager;
import lombok.Getter;

import java.io.File;


@Getter
public class FileManager {


    // configuration/files
    private CaseConfig caseConfig;
    private CaseLocationConfig caseLocationConfig;
    private LangConfig langConfig;
    // path to cases
    public static File FILE_PATH_CASES;

    public FileManager(GetCase plugin) {
        // init folder with cases
        FILE_PATH_CASES = new File(plugin.getDataFolder() + "/cases/");

        // register serializer
        caseConfig = ConfigManager.create(CaseConfig.class, it -> {
            it.file(new File(plugin.getDataFolder(), "config.yml"));
            it.load();
        });

        caseLocationConfig = ConfigManager.create(CaseLocationConfig.class, it -> {
            it.file(new File(plugin.getDataFolder(), "location.yml"));
            it.load();
        });

        langConfig = ConfigManager.create(LangConfig.class, it -> {
            it.file(new File(plugin.getDataFolder(), "lang.yml"));
            it.load();
        });

    }

}
