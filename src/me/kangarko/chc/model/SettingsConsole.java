package me.kangarko.chc.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Common;

import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("unused")
public class SettingsConsole extends ConfHelper {

	public static void load() throws ReflectiveOperationException {
		file = new File(ChatControl.instance().getDataFolder(), "Console.yml");

		File oldFile = new File(ChatControl.instance().getDataFolder(), "console.yml");
		cfg = YamlConfiguration.loadConfiguration(oldFile);

		if (cfg.isSet("Console")) {
			Common.Log("&fDetected old console.yml. This is &cnot &fcompatible with the new one, and file was moved to broken/console.yml");

			oldFile.renameTo(new File(ChatControl.instance().getDataFolder(), "/broken/console.yml"));
			oldFile.delete();
		}

		createFileAndLoad();
		loadValues(SettingsConsole.class);
	}

	
	public static boolean FILTER_ENABLED;
	public static boolean FILTER_FILTER_PLUGINS;
	public static boolean FILTER_COLORS_TO_ANSI;
	public static List<String> FILTER_MESSAGES;
	public static HashMap<String, String> FILTER_REPLACE_MAP;
	
	private static final void init() {
		FILTER_ENABLED = getBoolean("Filter.Enabled", true);
		FILTER_FILTER_PLUGINS = getBoolean("Filter.Filter Plugins", true);
		FILTER_COLORS_TO_ANSI = getBoolean("Filter.Add Ansi Colors To Console", true);
		FILTER_MESSAGES = getStringList("Filter.Filter Console Messages", Arrays.asList("Reached end of stream for", "Connection reset", "lost connection", ".*CONSOLE: (Save complete.|Forcing save..).*"));
	
		HashMap<String, String> defaults = new HashMap<>();
		defaults.put("CONSOLE: Reload complete<dot>", "CONSOLE: &bReloaded by magical powers!");
		
		FILTER_REPLACE_MAP = getValuesAndKeys("Filter.Replace Messages", defaults, false);
	}
}
