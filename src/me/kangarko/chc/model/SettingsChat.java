package me.kangarko.chc.model;

import java.io.File;
import java.io.IOException;
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

	public static boolean smileys;
	public static HashMap<String, String> replaceMap;
	
	private static final void smileysAndReplacements() {
		smileys = getBoolean("inbuilt-smileys", true);
		
		HashMap<String, String> defaults = new HashMap<>();
		defaults.put("\\bdis\\b", "this");
		defaults.put("\\bwanna\\b", "want");
		defaults.put("\\bgonna\\b", "going");
		defaults.put("(can|may|would you like if) i (have|be|become|get|has) (op|admin|mod|builder)", "can i has weepcraft?");
		
		replaceMap = getValuesAndKeys("replace-characters", defaults, false);
	}
}
