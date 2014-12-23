package kangarko.chatcontrol.model;

import java.io.File;
import java.util.HashMap;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;

import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("unused")
public class SettingsChat extends ConfHelper {

	public static void load() throws Exception {
		HEADER = "This file manages replacing characters in the game chat.\n"
				+ "\n"
				+ "You can add custom smileys and other UTF-8 characters here,\n"
				+ "but the file must be saved with UTF-8 encoding. Tested on Notepad++\n"
				+ "WordPad or Notepad on Windows is known to cause issues!";
		FILE_NAME = "Chat.yml";
		file = new File(ChatControl.instance().getDataFolder(), FILE_NAME);

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
	public static HashMap<String, String> REPLACE_WHOLE_MAP;
	
	private static final void init() {		
		HashMap<String, String> utf = new HashMap<>();
		utf.put(":)", "\u263a");
		utf.put(":-)", "\u263a");
		utf.put(":(", "\u2639");
		utf.put(":-(", "\u2639");
		utf.put(";)", "\u32e1");
		utf.put(";-)", "\u32e1");
		utf.put("<3", "\u2665");
		utf.put(":square:", "\u25a8");
		utf.put(":rect:", "\u2588");
		
		HashMap<String, String> part = new HashMap<>();
		part.put(whole("dis"), "this");
		part.put(whole("bwanna"), "want");
		part.put(whole("gonna"), "going");
		part.put("(can|may|would you like if) i (have|be|become|get|has) (op|admin|mod|builder)", "can i has weepcraft?");
		
		HashMap<String, String> whole = new HashMap<>();
		whole.put("test", "nonono");
		
		REPLACE_UTF_MAP = getValuesAndKeys("Replace_UTF-8", utf, false);
		REPLACE_REGEX_MAP = getValuesAndKeys("Replace_Parts", part, false);
		REPLACE_WHOLE_MAP = getValuesAndKeys("Replace_Whole", whole, false);
	}
	
	private static String whole(String str) {
		return "\\b" + str + "\\b";
	}
}
