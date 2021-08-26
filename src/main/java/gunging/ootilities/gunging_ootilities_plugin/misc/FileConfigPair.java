package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;

public class FileConfigPair {
    @NotNull YamlConfiguration configStorage;
    @NotNull File configFile;

    public FileConfigPair(@NotNull  File cFile, @NotNull YamlConfiguration cStorage) {
        configFile = cFile;
        configStorage = cStorage;
    }

    @NotNull
    public File GetFile() { return configFile; }
    @NotNull
    public File getFile() { return configFile; }

    @NotNull
    public YamlConfiguration GetStorage() { return configStorage; }
    @NotNull
    public YamlConfiguration getStorage() { return configStorage; }
}
