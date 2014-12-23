package kangarko.chatcontrol.model;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;

import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("unused")
public class SettingsConsole extends ConfHelper {

	public static void load() throws Exception {
		HEADER = "This file manages the server console.\n"
				+ "\n"
				+ "You can add UTF-8 characters here,\n"
				+ "but the file must be saved with UTF-8 encoding. Tested on Notepad++\n"
				+ "WordPad or Notepad on Windows is known to cause issues!\n"
				+ "To make a dot (.) on the left side, simply type <DOT>";
		FILE_NAME = "Console.yml";
		file = new File(ChatControl.instance().getDataFolder(), FILE_NAME);

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
		FILTER_FILTER_PLUGINS = getBoolean("Filter.Filter_Plugins", true);
		FILTER_COLORS_TO_ANSI = getBoolean("Filter.Add_Ansi_Colors_To_Console", true);
		FILTER_MESSAGES = getStringList("Filter.Filter_Console_Messages", Arrays.asList("Reached end of stream for", "Connection reset", "lost connection"));
	
		HashMap<String, String> defaults = new HashMap<>();
		defaults.put("CONSOLE: Reload complete<dot>", "CONSOLE: &bReloaded by magical powers!");
		
		FILTER_REPLACE_MAP = getValuesAndKeys("Filter.Replace_Messages", defaults, false);
	}
}
