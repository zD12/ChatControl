package kangarko.chatcontrol.model;

import java.io.File;
import java.util.HashMap;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;

@SuppressWarnings("unused")
public class SettingsRemap extends ConfHelper {

	public static void load() throws Exception {
		HEADER = "---------------------------------------------------------\n" +
				"This file manages remapping messages with the help of packets\n" +
				"\n" +
				"It supports unicode characters, such as smileys\n" +
				"but the file must be saved with UTF-8 encoding. Tested on Notepad++\n" +
				"WordPad or Notepad on Windows are known to cause issues!" +
				"\n" +
				"You need to have ProtocolLib to enable these features!\n" +
				"You can replace plugin or server messages, too.\n" +
				"---------------------------------------------------------\n";
		FILE_NAME = "remap.yml";

		File old = new File(ChatControl.instance().getDataFolder(), "chat.yml");
		if (old.exists()) {
			Common.Log("&fDetected old chat.yml. This is &cnot &fcompatible with this version, and file renamed to old_chat.yml");

			old.renameTo(new File(ChatControl.instance().getDataFolder(), "old_chat.yml"));
			old.delete();
		}

		createFileAndLoad();
		loadValues(SettingsRemap.class);
	}

	public static HashMap<String, String> REPLACE_UTF_MAP;
	public static HashMap<String, String> REPLACE_REGEX_MAP;
	//public static HashMap<String, String> REPLACE_WHOLE_MAP;

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

		//HashMap<String, String> whole = new HashMap<>();
		//whole.put("test", "nonono");

		REPLACE_UTF_MAP = getValuesAndKeys("Replace_UTF-8", utf, false);
		REPLACE_REGEX_MAP = getValuesAndKeys("Replace_Parts", part, false);
		//REPLACE_WHOLE_MAP = getValuesAndKeys("Replace_Whole", whole, false);
	}

	private static String whole(String str) {
		return "\\b" + str + "\\b";
	}
}
