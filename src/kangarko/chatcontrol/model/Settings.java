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
		HEADER = "This is the main configuration for ChatControl plugin\n"
				+ "\n"
				+ "For general support and updates visit:\n"
				+ "http://www.spigotmc.org/resources/chatcontrol.271\n"
				+ "\n"
				+ "For configuration help visit:\n"
				+ "https://github.com/kangarko/ChatControl/blob/master/config.yml\n"
				+ "\n"
				+ "SLOVAK DEVELOPER FTW\n"
				+ "Supports color codes with the '&' character.\n";		
		FILE_NAME = "Settings.yml";
		file = new File(ChatControl.instance().getDataFolder(), FILE_NAME);

		File oldFile = new File(ChatControl.instance().getDataFolder(), "config.yml");
		if (oldFile.exists()) {
			Common.Log("&fRenaming old config to old-config.yml, as it is not compatible with versions 5.x!");

			oldFile.renameTo(new File(ChatControl.instance().getDataFolder(), "old-config.yml"));
			oldFile.delete();
		}

		createFileAndLoad();
		loadValues(Settings.class);
	}

	public static class Packets {
		public static boolean DISABLE_TAB_COMPLETE;
		//public static boolean REMAP_MESSAGES; // TODO;
		
		private static void init() {
			pathPrefix("Packets");
			//REMAP_MESSAGES = getBoolean("Remap_Messages", false);
			DISABLE_TAB_COMPLETE = getBoolean("Disable_Tab_Complete", false);
		}
	}

	public static class SoundNotify {
		public static boolean ENABLED;
		public static boolean ONLY_WHEN_AFK;
		public static HashSet<String> ENABLED_IN_FOLLOWING_COMMANDS;
		public static SoundHelper SOUND;
		public static String CHAT_PREFIX;

		private static void init() {
			pathPrefix("Sound_Notify");

			ENABLED = getBoolean("Enabled", true);
			ONLY_WHEN_AFK = getBoolean("Notify_Only_When_Afk", true);
			CHAT_PREFIX = getString("Notify_Only_If_Prefixed_With", "@");
			SOUND = new SoundHelper(getString("Sound", "CHICKEN_EGG_POP, 1F, 1.5F"));
			ENABLED_IN_FOLLOWING_COMMANDS = new HashSet<>(getStringList("Enabled_In_Commands", Arrays.asList("msg", "tell", "t", "w", "r")));
		}
	}

	public static class AntiSpam {
		public static class Messages {
			public static int DELAY;
			public static int SIMILARITY;
			
			private static void init() {
				pathPrefix("Anti_Spam.Chat");
				
				DELAY = getInteger("Delay_Between_Messages", 1);
				SIMILARITY = getInteger("Block_Similar_Over", 80);
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
				SIMILARITY = getInteger("Block_Similar_Over", 80);
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
			public static int CAPITALIYE_MSG_LENGTH;

			private static void init() {
				pathPrefix("Chat.Grammar.Insert_Dot");

				INSERT_DOT = getBoolean("Enabled", true);
				INSERT_DOT_MSG_LENGTH = getInteger("Min_Message_Length", 5);

				pathPrefix("Chat.Grammar.Capitalize");
				
				CAPITALIZE = getBoolean("Enabled", true);
				CAPITALIYE_MSG_LENGTH = getInteger("Min_Message_Length", 5);
			}
		}
	}

	public static class Commands {

		public static HashSet<String> WHITELIST_ADS; // TODO introduce custom filters
		public static HashSet<String> WHITELIST_SWEAR; // TODO introduce custom filters


		private static void init() {
			pathPrefix("Commands");
			WHITELIST_ADS = new HashSet<>(getStringList("Whitelist_Ads_In", Arrays.asList("/auction", "//")));
			WHITELIST_SWEAR = new HashSet<>(getStringList("Whitelist_Swears_In", Arrays.asList("/register", "/login", "/l"))); 
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
			TIMED_ENABLED = getBoolean("Enabled", true);
			TIMED_RANDOM_ORDER = getBoolean("Random_Order", false);
			TIMED_RANDOM_NO_REPEAT = getBoolean("Random_No_Repeat", true);
			TIMED_PREFIX = getString("Prefix", "&8[&2Tip&8]&2");
			TIMED_DELAY_SECONDS = getInteger("Delay_Seconds", 180);
			
			
			HashMap<String, List<String>> timedDef = new HashMap<>();
			timedDef.put("global", Arrays.asList("Hey, %player, did you know that this server is running ChatControl?", "Visit developer website: &awww.rushmine.6f.sk"));
			timedDef.put("hardcore", Arrays.asList("Grief is not permitted what-so-ever and every griefer will be banned.", "Can you survive the night on hardcore world?"));
			timedDef.put("creative", Arrays.asList("excludeGlobal", "Welcome on Creative world. Enjoy your gamemode :)"));
			timedDef.put("ignored-world", Arrays.asList(""));
			
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

	public static class AntiAd {
		public static boolean ENABLED;
		public static boolean ENABLED_IN_COMMANDS;

		public static boolean ALERT_STAFF;
		public static boolean ALERT_CONSOLE;
		public static boolean BROADCAST_BLOCK;
		public static boolean WRITE_TO_FILE;
		public static String DISPATCH_COMMAND;

		public static String PREPROCESS_STRIP;
		public static String REGEX_IP; // TODO Put it to the trash, where it belongs (and make custom filters)
		public static String REGEX_DOMAINS; // TODO Put it to the trash, where it belongs (and make custom filters)
		public static String REGEX_CUSTOM; // TODO Put it to the trash, where it belongs (and make custom filters)

		public static List<String> WHITELIST_IP;
		public static List<String> WHITELIST_DOMAINS;

		private static void init() {
			pathPrefix("Anti_Ad");

			ENABLED = getBoolean("Enabled", true);
			ENABLED_IN_COMMANDS = getBoolean("Enabled_In_Commands", true);

			ALERT_STAFF = getBoolean("Alert_Staff", true);
			ALERT_CONSOLE = getBoolean("Alert_Console", true);
			BROADCAST_BLOCK = getBoolean("Broadcast", false);
			WRITE_TO_FILE = getBoolean("Write_To_File", true);
			DISPATCH_COMMAND = getString("Dispatch_Command", "none");

			PREPROCESS_STRIP = getString("Filter_Preprocess", "[\\(\\[\\]\\)]");
			REGEX_IP = getString("Ip_Filter", "\\b[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}\\b");
			REGEX_DOMAINS = getString("Domain_Filter", "[a-zA-Z0-9\\-\\.]+\\s?(\\.|dot|\\(dot\\)|-|;|:|,)\\s?(com|org|net|cz|co|uk|sk|biz|mobi|xxx|eu)\\b");
			REGEX_CUSTOM = getString("Custom_Filter", "none");

			WHITELIST_IP = getStringList("Whitelist_IPs", Arrays.asList("127.0.0.1", "Your.Server.IP"));
			WHITELIST_DOMAINS = getStringList("Whitelist_Domains", Arrays.asList("google.com", "youtube.com", "your-server.domain"));
		}
	}

	public static class AntiSwear {

		public static boolean ENABLED;
		public static boolean ENABLED_IN_COMMANDS;
		public static boolean ALERT_STAFF;
		public static boolean WARN_PLAYER;
		public static String DISPATCH_COMMAND;

		public static String REPLACEMENT;
		public static boolean BLOCK_MESSAGE;
		public static boolean REPLACE_MESSAGE;
		@Deprecated
		public static List<String> WORD_LIST; // TODO Put it to the trash, where it belongs (and make custom filters)

		private static void init() {
			pathPrefix("Anti_Swear");

			ENABLED = getBoolean("Enabled", true);
			ENABLED_IN_COMMANDS = getBoolean("Enabled_In_Commands", true);

			ALERT_STAFF = getBoolean("Alert_Staff", true);
			WARN_PLAYER = getBoolean("Warn_Player", true);
			DISPATCH_COMMAND = getString("Dispatch_Command", "none");

			BLOCK_MESSAGE = getBoolean("Block_Message", false);
			REPLACE_MESSAGE = getBoolean("Replace_Word", true);
			REPLACEMENT = getString("Replacement", "*****");

			WORD_LIST = getStringList("Word_List_Deprecated", Arrays.asList("fuck", "pussy", "bitch", "\\bass\\b"));
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
		public static boolean CHECK_FOR_ADS;
		public static boolean CHECK_FOR_DUPLICATION;
		public static boolean REWRITE_LINES_WHEN_AD_FOUND;
		public static String REWRITE_TEXT;

		private static void init() {
			pathPrefix("Signs");

			CHECK_FOR_ADS = getBoolean("Check_For_Ads", true);
			CHECK_FOR_DUPLICATION = getBoolean("Check_For_Duplication", false);
			REWRITE_LINES_WHEN_AD_FOUND = getBoolean("Rewrite_Lines_When_Ad_Found", true);
			REWRITE_TEXT = getString("Rewrite_Text", "&4Advertising:&4is not:&4permitted on:&4this server");
		}
	}

	public static class Writer {
		public static boolean ENABLED;
		public static HashSet<String> WHITELIST_PLAYERS;
		public static HashSet<String> INCLUDE_COMMANDS;

		private static void init() {
			pathPrefix("Writer");

			ENABLED = getBoolean("Write_Chat_Communication", true);
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

	public static class General {
		public static boolean OP_HAS_PERMISSIONS;
		@Deprecated // TODO further inspection of that thing
		public static boolean STRIP_UNICODE_IN_CHECKS;
		public static int REGEX_TIMEOUT;
		public static int MIN_PLAYERS_TO_ENABLE;
		public static String LOCALIZATION_SUFFIX;
		public static String LOCALIZATION;
		public static boolean DEBUG;

		private static void init() {
			pathPrefix("General");

			MIN_PLAYERS_TO_ENABLE = getInteger("Minimum_Players_To_Enable_Checks", 0);
			STRIP_UNICODE_IN_CHECKS = getBoolean("Ignore_Unicode_In_Checks", true);
			OP_HAS_PERMISSIONS = getBoolean("Op_Has_Permissions", true);
			REGEX_TIMEOUT = getInteger("Regex_Timeout_Milis", 100);
			LOCALIZATION_SUFFIX = getString("Locale", "en");
			LOCALIZATION = "messages_" + LOCALIZATION_SUFFIX + ".yml";
			DEBUG = getBoolean("Debug", false);
		}
	}
}
