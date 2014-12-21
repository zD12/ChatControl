package me.kangarko.chc.model;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import me.kangarko.chc.ChatControl;

import org.bukkit.configuration.file.YamlConfiguration;

import com.sun.xml.internal.bind.api.impl.NameConverter.Standard;

@SuppressWarnings("unused")
public class Localization extends ConfHelper {
	
	public static void load() {
		// try if the user has modified version of localization inside his plugin folder		
		file = new File(ChatControl.instance().getDataFolder(), Settings.LOCALIZATION_SUFFIX);

		if (file.exists())
			cfg = YamlConfiguration.loadConfiguration(file);
		else {
			file = null;
			cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(Localization.class.getResourceAsStream("/localization/" + Settings.LOCALIZATION_SUFFIX), StandardCharsets.UTF_8));
		}

		loadValues(Localization.class);
	}

	public static class Parts {		
		public static String JOIN;
		public static String QUIT;
		public static String KICK;
		public static String PREFIX = "";
		public static String CONSOLE;
		public static String REASON;
		public static String SIGN;
		public static CasusHelper SECONDS;
		
		private static final void init() {
			pathPrefix("General");
			
			JOIN = getString("Player Join", "player join");
			QUIT = getString("Player Quit", "player quit");
			KICK = getString("Player Kick", "player kick");
			PREFIX = getString("Prefix", "&7[&bChatControl&7]&f");
			CONSOLE = getString("Console", "&cserver");
			REASON = getString("Reason", "&7Reason: &f%reason");
			SIGN = getString("Sign", "SIGN");
			SECONDS = new CasusHelper(getString("Seconds", "second, seconds"));
		}
	}

	public static String WRONG_PARAMETERS;
	public static String WRONG_ARGUMENTS;
	
	public static String CANNOT_BROADCAST_EMPTY_MESSAGE;
	public static String CANNOT_CHAT_WHILE_MUTED;
	public static String CANNOT_COMMAND_WHILE_MUTED;
	public static String CANNOT_CHAT_UNTIL_MOVED;
	
	public static String ANTISWEAR_STAFF_ALERT; // TODO move to own sections
	public static String ANTISWEAR_PLAYER_WARN;
	public static String ANTIAD_PLAYER_WARN;
	public static String ANTIAD_STAFF_ALERT;
	public static String ANTIAD_BROADCAST_ALERT;
	public static String ANTIAD_CONSOLE_ALERT;
	
	public static String ANTISPAM_SIMILAR_MESSAGE;
	public static String ANTISPAM_SIMILAR_COMMAND;
	public static String ANTISPAM_CAPS_MESSAGE;
	
	public static String COMMAND_WAIT_MESSAGE;
	public static String CHAT_WAIT_MESSAGE;
	
	public static String ANTIBOT_REJOIN_TOO_QUICKLY;
	
	public static String MUTE_BROADCAST;
	public static String MUTE_UNMUTE_BROADCAST;	
	public static String MUTE_ANON_BROADCAST;
	public static String MUTE_ANON_UNMUTE_BROADCAST;	
	public static String MUTE_SUCCESS;
	public static String MUTE_UNMUTE_SUCCESS;
	
	public static String CLEAR_BROADCAST;
	public static String CLEAR_ANON_BROADCAST;
	public static String CLEAR_CONSOLE;
	public static String CLEAR_CONSOLE_MSG;
	public static String CLEAR_STAFF;
	
	public static String USAGE_FAKE_CMD;
	public static String UPDATE_AVAILABLE;
	public static String NO_PERMISSION;
	public static String RELOAD_COMPLETE;
	public static String RELOAD_FAILED;
	
	private static final void init() {		
		pathPrefix("Cannot");
		CANNOT_BROADCAST_EMPTY_MESSAGE = getString("Broadcast Empty Message", "&cMessage on %event is empty. No broadcast.");
		CANNOT_CHAT_WHILE_MUTED = getString("Chat While Muted", "&7You cannot chat while the chat is muted!");
		CANNOT_COMMAND_WHILE_MUTED = getString("Command While Muted", "&7You cannot use this command while the chat is muted!");
		CANNOT_CHAT_UNTIL_MOVED = getString("Chat Until Moved", "&7You cannot chat until you move!"); // TODO radius?
		
		pathPrefix("Anti Swear");
		ANTISWEAR_PLAYER_WARN = getString("Player Warn", "&cPlease do not swear, otherwise an action will be taken!");
		ANTISWEAR_STAFF_ALERT = getString("Staff Alert", "%prefix &c%player has sworn:&f %message");
		
		pathPrefix("Anti Bot");
		ANTIBOT_REJOIN_TOO_QUICKLY = getString("Rejoin Message", "%prefix\\n\\n&6Please wait &7%time second(s)&6 before logging in again.");
		
		pathPrefix("Anti Ad");
		ANTIAD_PLAYER_WARN = getString("Player Warn", "&7Please do not advertise other websites or IP adresses.");
		ANTIAD_STAFF_ALERT = getString("Staff Alert", "&c%player might have advertised: &f%message");
		ANTIAD_BROADCAST_ALERT = getString("Broadcast Alert", "&c%player might have advertised, alert admins!");
		ANTIAD_CONSOLE_ALERT = getString("Console Alert", "&c%player might have advertised: &f%message");
		
		pathPrefix("Anti Spam");
		ANTISPAM_SIMILAR_MESSAGE = getString("Similar Message", "&cPlease do not repeat the same (or similar) message.");
		ANTISPAM_SIMILAR_COMMAND = getString("Similar Command", "&cPlease do not repeat the same (or similar) command.");
		ANTISPAM_CAPS_MESSAGE = getString("Too Much Caps", "&cDo not use so much CAPS LOCK!");
		COMMAND_WAIT_MESSAGE = getString("Command Wait Message", "&cPlease wait %time %seconds before your next command.");
		CHAT_WAIT_MESSAGE = getString("Chat Wait Message", "&cPlease wait %time %seconds before your next message.");
		
		pathPrefix("Chat Mute");
		MUTE_SUCCESS = getString("Mute", "&7Chat was successfully muted.");
		MUTE_UNMUTE_SUCCESS = getString("Unmute", "&7Chat is no longer muted.");
		
		MUTE_BROADCAST = getString("Mute Broadcast", "&6%player has muted the chat.");
		MUTE_UNMUTE_BROADCAST = getString("Unmute Broadcast", "&6%player has unmuted the chat.");
		MUTE_ANON_BROADCAST = getString("Anonymous Mute Broadcast", "&cInitiated global chat mute.");
		MUTE_ANON_UNMUTE_BROADCAST = getString("Anonymous Unmute Broadcast", "&cGlobal chat mute cancelled.");
		
		pathPrefix("Chat Clear");
		CLEAR_BROADCAST = getString("Broadcast", "%prefix &e%player cleared the chat.");
		CLEAR_ANON_BROADCAST = getString("Anonymous Broadcast", "&cThe game chat was cleared.");
		CLEAR_CONSOLE = getString("Console Player Message", "%prefix &7Console was successfully cleared.");
		CLEAR_CONSOLE_MSG = getString("Console Message", "&7Console was cleared by %player");
		CLEAR_STAFF = getString("Chat Staff Message", "&7^----- [ == &fChat was cleared by %player &7== ] -----^");
		
		pathPrefix("Usage");
		USAGE_FAKE_CMD = getString("Fake Command", "%prefix Usage: /chatcontrol fake <&bjoin&f/&aleave&f>");
		
		pathPrefix(null);
		UPDATE_AVAILABLE = getString("Update Available", "&2A new version of &3ChatControl&2 is available.\\n&2Current version: &f%current&2; New version: &f%new\\n&2You can disable this notification in the config.");
		NO_PERMISSION = getString("No Permission",  "&cInsufficient permission.");
		RELOAD_COMPLETE = getString("Reload Complete", "%prefix &2Configuration reloaded successfuly.");
		RELOAD_FAILED = getString("Reload Failed", "%prefix &cReloading configuration failed, check console.");
		
		WRONG_PARAMETERS = getString("Wrong Parameters", "&cWrong parameters, type &6/chc list&c for instructions.");
		WRONG_ARGUMENTS = getString("Wrong Arguments", "&cWrong arguments. Type &6/chc list&c for command list.");
	}
}
