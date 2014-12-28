package kangarko.chatcontrol.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.ConfHelper.ChatMessage.Type;
import kangarko.chatcontrol.utils.Common;

@SuppressWarnings("unused")
public class Settings extends ConfHelper {

	public static void load() throws Exception {
		HEADER = "---------------------------------------------------------\n" +
				"This is the main configuration for ChatControl plugin\n" +
				"\n" +
				"For general support and updates visit:\n" +
				"http://www.spigotmc.org/resources/chatcontrol.271\n" +
				"\n" +
				"For configuration help visit:\n" +
				"https://github.com/kangarko/ChatControl/blob/master/config.yml\n" +
				"\n" +
				"SLOVAK DEVELOPER FTW\n" +
				"Supports color codes with the '&' character.\n" +
				"\n" +
				"Most of the messages (warning messages, etc) are found\n" +
				"in localization. To customize it, make a new file in\n" +
				"localization/messages_LOCALENAME.yml and it will be filled\n" +
				"with all the default values.\n" +
				"---------------------------------------------------------\n";		
		FILE_NAME = "settings.yml";

		File oldFile = new File(ChatControl.instance().getDataFolder(), "config.yml");
		if (oldFile.exists()) {
			Common.Log("&fRenaming old config to old_config.yml, as it is not compatible with versions 5.x!");

			oldFile.renameTo(new File(ChatControl.instance().getDataFolder(), "old_config.yml"));
			oldFile.delete();
		}

		createFileAndLoad();
		loadValues(Settings.class);
	}

	public static class Packets {
		public static boolean DISABLE_TAB_COMPLETE;

		private static void init() {
			pathPrefix("Packets");
			DISABLE_TAB_COMPLETE = getBoolean("Disable_Tab_Complete", false);
		}
	}

	public static class SoundNotify {
		public static boolean ENABLED;
		public static boolean ONLY_WHEN_AFK;
		public static HashSet<String> ENABLED_IN_COMMANDS;
		public static SoundHelper SOUND;
		public static String CHAT_PREFIX;

		private static void init() {
			pathPrefix("Sound_Notify");

			ENABLED = getBoolean("Enabled", true);
			ONLY_WHEN_AFK = getBoolean("Notify_Only_When_Afk", true);
			CHAT_PREFIX = getString("Notify_Only_If_Prefixed_With", "@");
			SOUND = new SoundHelper(getString("Sound", "CHICKEN_EGG_POP, 1F, 1.5F"));
			ENABLED_IN_COMMANDS = new HashSet<>(getStringList("Enabled_In_Commands", Arrays.asList("msg", "tell", "t", "w", "r")));
		}
	}

	public static class AntiSpam {
		public static class Messages {
			public static int DELAY;
			public static int SIMILARITY;

			private static void init() {
				pathPrefix("Anti_Spam.Chat");

				DELAY = getInteger("Delay_Between_Messages", 1);
				SIMILARITY = getInteger("Similar_Percentage_Block", 80);
			}
		}

		public static class Commands {
			public static HashSet<String> WHITELIST_DELAY;
			public static HashSet<String> WHITELIST_SIMILARITY;
			
			public static int DELAY;
			public static int SIMILARITY;

			private static void init() {
				pathPrefix("Anti_Spam.Commands");

				DELAY = getInteger("Delay_Between_Commands", 2);
				SIMILARITY = getInteger("Similar_Percentage_Block", 80);
				WHITELIST_DELAY = new HashSet<>(getStringList("Whitelist_Delay", Arrays.asList("tell", "pm", "t", "w", "r")));
				WHITELIST_SIMILARITY = new HashSet<>(getStringList("Whitelist_Similarity", Arrays.asList("spawn", "home")));
			}
		}

		public static boolean BLOCK_CHAT_UNTIL_MOVED;
		public static boolean STRIP_SPECIAL_CHARS;
		public static boolean STRIP_DUPLICATE_CHARS;

		private static void init() {
			pathPrefix("Anti_Spam");

			BLOCK_CHAT_UNTIL_MOVED = getBoolean("Block_Chat_Until_Moved", true);
			STRIP_SPECIAL_CHARS = getBoolean("Similarity_Check.Ignore_Special_Characters", true);
			STRIP_DUPLICATE_CHARS = getBoolean("Similarity_Check.Ignore_Duplicate_Characters", false);
		}
	}

	public static class Chat {
		public static class Formatter {
			public static boolean ENABLED;
			public static boolean RANGED_MODE;
			public static String FORMAT;
			public static String GLOBAL_FORMAT;
			public static double RANGE;

			private static void init() {
				pathPrefix("Chat.Formatter");

				ENABLED = getBoolean("Enabled", false);
				FORMAT = getString("Message_Format", "%prefix%player:%suffix %message");
				GLOBAL_FORMAT = getString("Global_Message_Format", "%prefix%player:%suffix %message");
				RANGED_MODE = getBoolean("Ranged_Mode", false);
				RANGE = getDouble("Range", 100);
			}
		}

		public static class Grammar {
			public static boolean INSERT_DOT;
			public static int INSERT_DOT_MSG_LENGTH;

			public static boolean CAPITALIZE;			
			public static int CAPITALIZE_MSG_LENGTH;

			private static void init() {
				pathPrefix("Chat.Grammar.Insert_Dot");
				
				INSERT_DOT = getBoolean("Enabled", true);
				INSERT_DOT_MSG_LENGTH = getInteger("Min_Message_Length", 5);

				pathPrefix("Chat.Grammar.Capitalize");
				
				CAPITALIZE = getBoolean("Enabled", true);
				CAPITALIZE_MSG_LENGTH = getInteger("Min_Message_Length", 5);
			}
		}
	}

	public static class Messages {
		public static ChatMessage JOIN, QUIT, KICK;

		public static boolean TIMED_ENABLED;
		public static boolean TIMED_RANDOM_ORDER;
		public static boolean TIMED_RANDOM_NO_REPEAT;
		public static String TIMED_PREFIX;
		public static int TIMED_DELAY_SECONDS;

		public static HashMap<String, List<String>> TIMED;

		private static void init() {
			pathPrefix("Messages");

			JOIN = getMessage("Join", new ChatMessage(Type.DEFAULT));
			QUIT = getMessage("Quit", new ChatMessage(Type.DEFAULT));
			KICK = getMessage("Kick", new ChatMessage(Type.DEFAULT));

			pathPrefix("Messages.Timed");
			TIMED_ENABLED = getBoolean("Enabled", false);
			TIMED_RANDOM_ORDER = getBoolean("Random_Order", false);
			TIMED_RANDOM_NO_REPEAT = getBoolean("Random_No_Repeat", true);
			TIMED_PREFIX = getString("Prefix", "&8[&2Tip&8]&2");
			TIMED_DELAY_SECONDS = getInteger("Delay_Seconds", 180);

			HashMap<String, List<String>> timedDef = new HashMap<>();
			timedDef.put("global", Arrays.asList("Hey, %player, did you know that this server is running ChatControl?", "Visit developer website: &awww.rushmine.6f.sk"));
			timedDef.put("hardcore", Arrays.asList("Grief is not permitted what-so-ever and every griefer will be banned.", "Can you survive the night on hardcore world?"));
			timedDef.put("creative", Arrays.asList("excludeGlobal", "Welcome on Creative world. Enjoy your gamemode :)"));
			timedDef.put("ignored-world", Arrays.asList("excludeGlobal"));

			TIMED = getValuesAndList("Message_List", timedDef);

			List<String> global = new ArrayList<>(TIMED.get("global"));

			for (String world : TIMED.keySet()) {
				List<String> worldMessages = TIMED.get(world);

				if (worldMessages.size() == 0 || world.equalsIgnoreCase("global"))
					continue;

				if (worldMessages.get(0).equalsIgnoreCase("excludeGlobal")) {
					worldMessages.remove(0);
					continue;
				}

				worldMessages.addAll(global);
			}
		}
	}

	public static class Mute {
		public static boolean BROADCAST;
		public static boolean SILENT_JOIN, SILENT_QUIT, SILENT_KICK, SILENT_DEATHS;
		public static HashSet<String> DISABLED_CMDS_WHEN_MUTED;

		private static void init() {
			pathPrefix("Mute");

			BROADCAST = getBoolean("Broadcast", true);
			SILENT_JOIN = getBoolean("Silence.Join_Messages", true);
			SILENT_QUIT = getBoolean("Silence.Quit_Messages", true);
			SILENT_KICK = getBoolean("Silence.Kick_Messages", true);
			SILENT_DEATHS = getBoolean("Silence.Death_Messages", true);
			DISABLED_CMDS_WHEN_MUTED = new HashSet<>(getStringList("Disabled_Commands_During_Mute", Arrays.asList("me", "tell", "msg", "r", "w")));
		}
	}

	public static class Clear {
		public static boolean BROADCAST;	
		public static int CONSOLE_LINES;
		public static boolean IGNORE_STAFF;		

		private static void init() {
			pathPrefix("Clear");

			BROADCAST = getBoolean("Broadcast", true);
			IGNORE_STAFF = getBoolean("Do_Not_Clear_For_Staff", true);
			CONSOLE_LINES = getInteger("Console_Lines_To_Clear", 300);				
		}
	}

	public static class AntiCaps {
		public static boolean ENABLED;
		public static boolean WARN_PLAYER;
		public static List<String> WHITELIST;

		public static int MIN_MESSAGE_LENGTH;
		public static int MIN_CAPS_PERCENTAGE;
		public static int MIN_CAPS_IN_A_ROW;

		private static void init() {
			pathPrefix("Anti_Caps");	
			ENABLED = getBoolean("Enabled", true);
			WARN_PLAYER = getBoolean("Warn_Player", true);

			MIN_MESSAGE_LENGTH = getInteger("Min_Message_Length", 5);
			MIN_CAPS_PERCENTAGE = getInteger("Min_Caps_Percentage", 50);
			MIN_CAPS_IN_A_ROW = getInteger("Min_Caps_In_A_Row", 5);

			WHITELIST = getStringList("Whitelist", Arrays.asList("OMG", "LOL", "WTF", "WOW", "ROFL"));
		}
	}

	public static class AntiBot {
		public static int REJOIN_TIME;

		private static void init() {
			pathPrefix("Anti_Bot");
			REJOIN_TIME = getInteger("Rejoin_Delay_Seconds", 4);
		}
	}

	public static class Signs {
		public static boolean DUPLICATION_CHECK;
		public static boolean DUPLICATION_ALERT_STAFF;
		public static boolean BLOCK_WHEN_VIOLATES_RULE;
		public static boolean DROP_SIGN;

		private static void init() {
			pathPrefix("Signs.Duplication");
			DUPLICATION_CHECK = getBoolean("Deny_Signs_With_Same_Text", false);
			DUPLICATION_ALERT_STAFF = getBoolean("Alert_Staff", true);
			BLOCK_WHEN_VIOLATES_RULE = getBoolean("Block_When_Violates_A_Rule", true);
			DROP_SIGN = getBoolean("Drop_Sign", true);
		}
	}

	public static class Rules {
		public static boolean CHECK_CHAT, CHECK_COMMANDS, CHECK_SIGNS;


		private static void init() {
			pathPrefix("Rules");

			CHECK_CHAT = getBoolean("Check_Chat", true);
			CHECK_COMMANDS = getBoolean("Check_Commands", true);
			CHECK_SIGNS = getBoolean("Check_Signs", true);
		}
	}

	public static class Writer {
		public static boolean ENABLED;
		public static boolean STRIP_COLORS;
		public static HashSet<String> WHITELIST_PLAYERS;
		public static HashSet<String> INCLUDE_COMMANDS;

		private static void init() {
			pathPrefix("Writer");

			ENABLED = getBoolean("Write_Chat_Communication", true);
			STRIP_COLORS = getBoolean("Strip_Colors", true);
			WHITELIST_PLAYERS = new HashSet<>(getStringList("Ignore_Players", Arrays.asList("ignoredAdmin")));
			INCLUDE_COMMANDS = new HashSet<>(getStringList("Write_Commands", Arrays.asList("tell")));
		}
	}

	public static class Updater {
		public static boolean ENABLED;
		public static boolean NOTIFY;
		public static boolean DOWNLOAD;

		private static void init() {
			pathPrefix("Updater");

			ENABLED = getBoolean("Enabled", true);
			NOTIFY = getBoolean("Notify", true);
			DOWNLOAD = getBoolean("Download", true);
		}
	}


	public static boolean OP_HAS_PERMISSIONS;
	public static int REGEX_TIMEOUT;
	public static int MIN_PLAYERS_TO_ENABLE;
	public static String LOCALIZATION_SUFFIX;
	protected static String LOCALIZATION;
	public static boolean VERBOSE;
	public static boolean DEBUG;
	public static int VERSION;

	private static void init() {
		MIN_PLAYERS_TO_ENABLE = getInteger("Minimum_Players_To_Enable_Checks", 0);
		OP_HAS_PERMISSIONS = getBoolean("Op_Has_Permissions", true);
		REGEX_TIMEOUT = getInteger("Regex_Timeout_Milis", 100);
		LOCALIZATION_SUFFIX = getString("Locale", "en");
		LOCALIZATION = "messages_" + LOCALIZATION_SUFFIX + ".yml";
		VERBOSE = getBoolean("Verbose_On_Startup", true);
		DEBUG = getBoolean("Debug", false);
		VERSION = getInteger("Version", 1);
	}
}
