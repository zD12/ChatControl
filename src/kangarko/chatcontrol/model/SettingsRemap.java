package kangarko.chatcontrol.model;

import java.util.HashMap;

@SuppressWarnings("unused")
public class SettingsRemap extends ConfHelper {

	protected static void load() throws Exception {
		createFileAndLoad("remap.yml", SettingsRemap.class);
	}

	public static HashMap<String, String> REPLACE_UTF_MAP;
	public static HashMap<String, String> REPLACE_REGEX_MAP;

	@Deprecated // TODO move
	public static HashMap<String, String> REPLACE_PROTOCOL_MAP;

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
		REPLACE_PROTOCOL_MAP = getValuesAndKeys("Replace_Whole", whole, false);
	}

	private static String whole(String str) {
		return "\\b" + str + "\\b";
	}
}
