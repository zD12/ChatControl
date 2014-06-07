package chatcontrol.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;

public class CustomConfig {

	private FileConfiguration config = null;
	private File configFile = null;
	private String fileName = null;

	public CustomConfig(FileType type) {
		fileName = type.string;
	}

	public void reload() {

		if (configFile == null) {
			configFile = new File(ChatControl.plugin.getDataFolder(), fileName);
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		InputStreamReader defConfigStream = new InputStreamReader(ChatControl.plugin.getResource(fileName));
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			reload();
		}
		return config;
	}

	public void save() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			Common.error("Nemozno ulozit konfiguraciu do " + configFile, ex);
		}
	}

	public void saveDefaultConfig() {
		if (configFile == null) {
			configFile = new File(ChatControl.plugin.getDataFolder(), fileName);
		}
		if (!configFile.exists()) {
			ChatControl.plugin.saveResource(fileName, false);
		}
	}
}
