package kangarko.chatcontrol.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class SettingsConsole extends ConfHelper {

	protected static void load() throws Exception {
		createFileAndLoad("console.yml", SettingsConsole.class);
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
