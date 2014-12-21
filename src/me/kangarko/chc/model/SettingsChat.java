package me.kangarko.chc.model;

import java.io.File;
import java.util.HashMap;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Common;

import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("unused")
public class SettingsChat extends ConfHelper {

	public static void load() {
		file = new File(ChatControl.instance().getDataFolder(), "Chat.yml");

		File oldFile = new File(ChatControl.instance().getDataFolder(), "chat.yml");
		cfg = YamlConfiguration.loadConfiguration(oldFile);

		if (cfg.isSet("Replacing_Characters")) {
			Common.Log("&fDetected old chat.yml. This is &cnot &fcompatible with the new one, and file was moved to broken/chat.yml");

			oldFile.renameTo(new File(ChatControl.instance().getDataFolder(), "/broken/chat.yml"));
			oldFile.delete();
		}

		createFileAndLoad();
		loadValues(SettingsChat.class);
	}

	public static HashMap<String, String> REPLACE_UTF_MAP;
	public static HashMap<String, String> REPLACE_REGEX_MAP;
	
	private static final void init() {		
		HashMap<String, String> defaults = new HashMap<>();
		defaults.put(":)", "☺");
		defaults.put(":-)", "☺");
		defaults.put(":(", "☹");
		defaults.put(":-(", "☹");
		defaults.put(";)", "㋡");
		defaults.put(";-)", "㋡");
		defaults.put("<3", "♥");
		defaults.put(":square:", "■");
		defaults.put(":rect:", "█");
		
		HashMap<String, String> defaultsRegex = new HashMap<>();
		defaultsRegex.put(whole("dis"), "this");
		defaultsRegex.put(whole("bwanna"), "want");
		defaultsRegex.put(whole("gonna"), "going");
		defaultsRegex.put("(can|may|would you like if) i (have|be|become|get|has) (op|admin|mod|builder)", "can i has weepcraft?");
		
		REPLACE_UTF_MAP = getValuesAndKeys("Replace UTF-8 Messages", defaults, false);
		REPLACE_REGEX_MAP = getValuesAndKeys("Replace Regex Messages", defaultsRegex, false);
	}
	
	private static String whole(String str) {
		return "\\b" + str + "\\b";
	}
}
