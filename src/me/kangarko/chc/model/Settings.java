package me.kangarko.chc.model;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Common;

@SuppressWarnings("unused")
public class Settings extends ConfHelper {

	// TODO prevent typos by prefixes , e.g. "chat.formatter"

	public static void load() {
		file = new File(ChatControl.instance().getDataFolder(), "Settings.yml");

		File oldFile = new File(ChatControl.instance().getDataFolder(), "config.yml");
		if (oldFile.exists()) {
			Common.Log("&fRenaming old config to old-config.yml, as it is not compatible with branch 5.0.0!");

			oldFile.renameTo(new File(ChatControl.instance().getDataFolder(), "old-config.yml"));
			oldFile.delete();
		}

		createFileAndLoad();
		loadValues(Settings.class);
	}

	public static class Packets {
		public static boolean PREVENT_TAB_COMPLETE; // TODO Customizable length.

		private static void init() {
			PREVENT_TAB_COMPLETE = getBoolean("protect.disable-tab-complete", false);
		}
	}

	public static class SoundNotify {
		public static boolean ENABLED;
		public static boolean ONLY_WHEN_AFK;
		public static boolean ENABLED_IN_COMMANDS; // TODO move to cmd section
		public static HashSet<String> ENABLED_IN_FOLLOWING_COMMANDS; // TODO fix name and move.
		public static SoundType SOUND;
		public static String CHAT_PREFIX;

		private static void init() {
			ENABLED = getBoolean("chat.sound-notify.enabled", true);
			ONLY_WHEN_AFK = getBoolean("chat.sound-notify.only-when-afk", true);
			ENABLED_IN_COMMANDS = getBoolean("adsas", true);
			ENABLED_IN_FOLLOWING_COMMANDS = new HashSet<>(getStringList("chat.sound-notify.enabled-in-commands", Arrays.asList("r")));
			try {
				SOUND = new SoundType(getString("dopitche", "CHICKEN_EGG_POP, 1F, 1.5F"));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			CHAT_PREFIX = getString("chat.sound-notify.chat-prefix", "@");
		}
	}

	public static class Chat {

		public static boolean BLOCK_CHAT_UNTIL_MOVED;
		public static int MESSAGE_DELAY;		
		public static boolean STRIP_UNICODE_IN_CHECKS;		
		public static int BLOCK_SIMILAR_MORE_THAN; // TODO make percentage customizable

		private static void init() {
			BLOCK_CHAT_UNTIL_MOVED = getBoolean("ewr", true);
			STRIP_UNICODE_IN_CHECKS = getBoolean("ba", false);
			MESSAGE_DELAY = getInteger("chat.delay-between.messages", 1);
			BLOCK_SIMILAR_MORE_THAN = getInteger("45e", 75);
		}
		
		public static class Formatter {
			public static boolean ENABLED;
			public static boolean RANGED_MODE;
			public static String FORMAT;
			public static String GLOBAL_FORMAT;
			public static double RANGE;

			private static void init() {
				ENABLED = getBoolean("chat.formatter.enabled", false);
				FORMAT = getString("chat.formatter.message-format", "%prefix%player:%suffix %message");
				GLOBAL_FORMAT = getString("chat.formatter.global-message-format", "%prefix%player:%suffix %message");
				RANGED_MODE = getBoolean("chat.formatter.ranged-mode", false);
				RANGE = getDouble("chat.formatter.range", 100);
			}
		}

		public static class Clear {
			public static int CONSOLE_LINES;
			public static boolean IGNORE_STAFF;		

			private static void init() {
				CONSOLE_LINES = getInteger("chat.clear.console-lines", 300);
				IGNORE_STAFF = getBoolean("chat.clear.ignore-staff", true);
			}
		}

		public static class Grammar {
			public static boolean INSERT_DOT;
			public static int INSERT_DOT_MSG_LENGTH;
			
			public static boolean CAPITALIZE;			
			public static int CAPITALIYE_MSG_LENGTH;

			private static void init() {
				INSERT_DOT = getBoolean("chat.grammar.punctuate.enabled", true);
				INSERT_DOT_MSG_LENGTH = getInteger("chat.grammar.punctuate.minimum-message-length", 5);

				CAPITALIZE = getBoolean("chat.grammar.capitalize.enabled", true);
				CAPITALIYE_MSG_LENGTH = getInteger("chat.grammar.capitalize.minimum-message-length", 5);
			}
		}
	}

	public static class AntiSwear {

		public static boolean ENABLED;
		
		public static boolean ALERT_STAFF;
		public static boolean WARN_PLAYER;
		public static String REPLACEMENT;
		public static boolean BLOCK_MESSAGE;
		public static boolean REPLACE_MESSAGE;
		public static List<String> WORD_LIST; // TODO remove and introduce custom filters

		private static void init() {
			ENABLED = getBoolean("541re", true);
			
			ALERT_STAFF = getBoolean("anti-swear.alert-staff", true);
			WARN_PLAYER = getBoolean("anti-swear.warn-player", true);
			REPLACEMENT = getString("anti-swear.replacement", "*****");
			BLOCK_MESSAGE = getBoolean("da6s15", false);
			REPLACE_MESSAGE = getBoolean("a65s1d", true);

			WORD_LIST = getStringList("anti-swear.wordlist", Arrays.asList("fuck", "pussy", "bitch", "\\bass\\b"));
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
			ENABLED = getBoolean("asd651", true);
			WARN_PLAYER = getBoolean("asd256as", true);
			
			WHITELIST = getStringList("anti-caps.whitelist", Arrays.asList("OMG", "LOL", "WTF", "WOW", "ROFL"));
			MIN_MESSAGE_LENGTH = getInteger("654we", 5);
			MIN_CAPS_PERCENTAGE = getInteger("rer", 50);
			MIN_CAPS_IN_A_ROW = getInteger("z51zr", 5);
		}
	}

	public static class AntiBot {
		public static int REJOIN_TIME;

		private static void init() {
			REJOIN_TIME = getInteger("anti-bot.rejoin-delay-seconds", 4);
		}
	}

	public static class AntiAd {
		public static boolean ENABLED;
		public static boolean ENABLED_IN_COMMANDS;	// TODO smove to Commands section
		
		public static boolean ALERT_STAFF;
		public static boolean ALERT_CONSOLE;
		public static boolean BROADCAST_BLOCK;
		public static boolean WRITE_TO_FILE;
		public static String PREPROCESS_STRIP; // TODO This is shit, fix it
		
		public static String REGEX_IP;
		public static String REGEX_DOMAINS; // TODO introduce custom filters
		public static String REGEX_CUSTOM;
		
		public static List<String> WHITELIST_IP;
		public static List<String> WHITELIST_DOMAINS;

		private static void init() {
			ENABLED = getBoolean("ad.eanbeld", true);

			ALERT_STAFF = getBoolean("anti-ad.alert-staff", true);
			ALERT_CONSOLE = getBoolean("anti-ad.alert-console", true);
			BROADCAST_BLOCK = getBoolean("anti-ad.broadcast", false);
			WRITE_TO_FILE = getBoolean("anti-ad.write-to-file", true);
			ENABLED_IN_COMMANDS = getBoolean("anti-ad.enabled-in-commands", true);

			PREPROCESS_STRIP = getString("anti-ad.filter-pre-process", "[\\(\\[\\]\\)]");
			REGEX_IP = getString("anti-ad.ip-filter", "\\b[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}\\b");
			REGEX_DOMAINS = getString("anti-ad.domain-filter", "[a-zA-Z0-9\\-\\.]+\\s?(\\.|dot|\\(dot\\)|-|;|:|,)\\s?(com|org|net|cz|co|uk|sk|biz|mobi|xxx|eu)\\b");
			REGEX_CUSTOM = getString("anti-ad.custom-filter", "none");

			WHITELIST_IP = getStringList("anti-ad.ip-whitelit", Arrays.asList("127.0.0.1", "Your.Server.IP"));
			WHITELIST_DOMAINS = getStringList("anti-ad.domain-whitelist", Arrays.asList("google.com", "youtube.com", "your-server.domain"));
		}
	}

	public static class Mute {
		public static boolean SILENT_JOIN, SILENT_QUIT, SILENT_KICK, SILENT_DEATHS;
		public static HashSet<String> DISABLED_CMDS_WHEN_MUTED;

		private static void init() {
			SILENT_JOIN = getBoolean("mute.hide-when-muted.join-messages", true);
			SILENT_QUIT = getBoolean("mute.hide-when-muted.quit-messages", true);
			SILENT_KICK = getBoolean("mute.hide-when-muted.kick-messages", true);
			SILENT_DEATHS = getBoolean("mute.hide-when-muted.death-messages", true);
			DISABLED_CMDS_WHEN_MUTED = new HashSet<>(getStringList("mute.disabled-commands-during-mute", Arrays.asList("/me", "/tell", "/msg", "/r", "/w")));
		}
	}

	public static class Messages {
		public static ChatMessage JOIN, QUIT, KICK;

		private static void init() {
			JOIN = getMessage("messages.join", new ChatMessage(ChatMessage.Type.DEFAULT));
			QUIT = getMessage("messages.quit", new ChatMessage(ChatMessage.Type.DEFAULT));
			KICK = getMessage("messages.kick", new ChatMessage(ChatMessage.Type.DEFAULT));
		}
	}

	public static class Signs {
		public static boolean CHECK_FOR_ADS;
		public static boolean CHECK_FOR_DUPLICATION;
		public static boolean REWRITE_LINES_WHEN_AD_FOUND;
		public static String REWRITE_TEXT;

		private static void init() {
			CHECK_FOR_ADS = getBoolean("signs.check-for-ads", true);
			CHECK_FOR_DUPLICATION = getBoolean("signs.check-text-duplication", false);
			REWRITE_LINES_WHEN_AD_FOUND = getBoolean("signs.rewrite-lines", true);
			REWRITE_TEXT = getString("signs.rewrite-text", "&4Advertising:&4is not:&4permitted on:&4this server");
		}
	}

	public static class Commands {
		public static HashSet<String> WHITELIST_TIME;
		public static HashSet<String> WHITELIST_DUPLICATION;
		public static HashSet<String> WHITELIST_ADS; // TODO introduce custom filters
		public static HashSet<String> WHITELIST_SWEAR; // TODO introduce custom filters

		public static int TIME_DELAY;
		public static boolean STRIP_UNICODE_IN_CHECKS;
		public static int BLOCK_SIMILAR_MORE_THAN;

		public static boolean CHECK_FOR_ADS;
		public static boolean CHECK_FOR_SWEARS;
		
		private static void init() {
			WHITELIST_TIME = new HashSet<>(getStringList("commands.bypass-time", Arrays.asList("tell", "pm", "t", "w", "r")));
			WHITELIST_DUPLICATION = new HashSet<>(getStringList("commands.bypass-time", Arrays.asList("spawn", "home")));
			WHITELIST_ADS = new HashSet<>(getStringList("commands.whitelist-ads", Arrays.asList("auction")));
			WHITELIST_SWEAR = new HashSet<>(getStringList("commands.whitelist-swear", Arrays.asList("fuck server commands"))); 
			TIME_DELAY = getInteger("commands.time-delay", 1);
			STRIP_UNICODE_IN_CHECKS = getBoolean("commands.strip-unicode", false);
			BLOCK_SIMILAR_MORE_THAN = getInteger("comamnds", 75);

			CHECK_FOR_ADS = getBoolean("a", true);
			CHECK_FOR_SWEARS = getBoolean("a", true);
		}
	}

	public static boolean OP_HAS_PERMISSIONS;
	public static int REGEX_TIMEOUT;
	public static int MIN_PLAYERS_TO_ENABLE;
	public static String LOCALIZATION_SUFFIX;
	public static boolean DEBUG;
	
	private static void init() {
		MIN_PLAYERS_TO_ENABLE = getInteger("minimum-players-to-enable-checks", 0);
		OP_HAS_PERMISSIONS = getBoolean("op-has-permissions", true);
		REGEX_TIMEOUT = getInteger("regex-timeout-milis", 1500);

		LOCALIZATION_SUFFIX = "messages_" + getString("localization", "sk") + ".yml";
		DEBUG = getBoolean("debug", false);
	}
	
	public static class Writer {

		public static boolean ENABLED;
		public static HashSet<String> WHITELIST_PLAYERS;
		public static HashSet<String> WHITELIST_COMMANDS;

		private static void init() {
			ENABLED = getBoolean("aa", true);
			WHITELIST_PLAYERS = new HashSet<>(getStringList("t0r", Arrays.asList("ignoredAdmin")));
			WHITELIST_COMMANDS = new HashSet<>(getStringList("asd21as", Arrays.asList("tell")));
		}
	}

	public static class Updater {
		public static boolean ENABLED;
		public static boolean NOTIFY;
		public static boolean DOWNLOAD;

		private static void init() {
			ENABLED = getBoolean("forceop.enabled", true);
			NOTIFY = getBoolean("updater.notify", true);
			DOWNLOAD = getBoolean("updater.download", true);
		}
	}
}
