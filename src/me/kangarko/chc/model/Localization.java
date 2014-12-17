package me.kangarko.chc.model;

import java.io.File;

import me.kangarko.chc.ChatControl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;

@SuppressWarnings("unused")
public class Localization extends ConfHelper {

	public static void load() {
		// try if the user has modified version of localization inside his plugin folder		
		file = new File(ChatControl.instance().getDataFolder(), Settings.LOCALIZATION_SUFFIX);

		if (file.exists())
			cfg = YamlConfiguration.loadConfiguration(file);
		else {
			file = null;
			cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(Localization.class.getResourceAsStream("/localization/" + Settings.LOCALIZATION_SUFFIX)));
		}

		loadValues(Localization.class);
	}

	public static class Parts {
		public static String JOIN;
		public static String QUIT;
		public static String PREFIX;
		public static String CONSOLE;
		public static String REASON;
		
		private static final void init() {
			JOIN = getString("general.player-join", "player join");
			QUIT = getString("general.player-quit", "player quit");
			PREFIX = getString("general.prefix", "&7[&bChatControl&7]&f");
			CONSOLE = getString("general.console", "&cserver");
			REASON = getString("general.reason", "&7Reason:&f%reason");
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
	public static String ANTIBOT_DUPE_SIGN;
	
	public static String UPDATE_AVAILABLE;
	public static String NO_PERMISSION;
	public static String RELOAD_COMPLETE;
	
	public static String MUTE_SUCCESS;
	public static String MUTE_UNMUTE_SUCCESS;
	public static String CHATCLEAR_CONSOLE;
	public static String CHATCLEAR_STAFF;
	public static String USAGE_FAKE_CMD;
	
	private static final void init() {
		WRONG_PARAMETERS = getString("wrong-parameters", "&cWrong parameters, available are: &7%params");
		WRONG_ARGUMENTS = getString("wrong-arguments", "&cWrong arguments. Type &6/chc list&c for command list.");
		
		CANNOT_BROADCAST_EMPTY_MESSAGE = getString("cannot.broadcast-empty-message", "&cMessage on %event is empty. No broadcast.");
		CANNOT_CHAT_WHILE_MUTED = getString("cannot.chat-while-muted", "&7You cannot chat while the chat is muted!");
		CANNOT_COMMAND_WHILE_MUTED = getString("cannot.use-command-while-muted", "&7You cannot use this command while the chat is muted!");
		CANNOT_CHAT_UNTIL_MOVED = getString("cannot.chat-until-moved", "&7You cannot chat until you move!"); // TODO radius?
		
		ANTISWEAR_PLAYER_WARN = getString("anti-swear.player-warn", "&cPlease do not swear, otherwise an action will be taken!");
		ANTISWEAR_STAFF_ALERT = getString("anti-swear.staff-alert", "%prefix &c%player has sworn:&f %message");
		
		ANTIBOT_REJOIN_TOO_QUICKLY = getString("anti-bot.rejoin-message", "%prefix\\n\\n&6Please wait &7%time second(s)&6 before logging in again.");
		ANTIBOT_DUPE_SIGN = getString("anti-bot.sign-dupe", "&cPlease do not repeat the same text on signs."); // FIXME remove, handled better by nocheatplus, no point including it her
		
		ANTIAD_PLAYER_WARN = getString("anti-ad.player-warn", "&7Please do not advertise other websites or IP adresses.");
		ANTIAD_STAFF_ALERT = getString("anti-ad.staff-alert", "&c%player might have advertised: &f%message");
		ANTIAD_BROADCAST_ALERT = getString("anti-ad.broadcast-alert", "&c%player might have advertised, alert admins!");
		ANTIAD_CONSOLE_ALERT = getString("anti-ad.console-alert", "&c%player might have advertised: &f%message");
		
		ANTISPAM_SIMILAR_MESSAGE = getString("anti-spam.similar-message", "&cPlease do not repeat the same (or similar) message.");
		ANTISPAM_SIMILAR_COMMAND = getString("anti-spam.similar-command", "&cPlease do not repeat the same (or similar) command.");
		ANTISPAM_CAPS_MESSAGE = getString("anti-spam.too-much-caps", "&cDo not use so much CAPS LOCK!");
		
		COMMAND_WAIT_MESSAGE = getString("commands.wait-time", "&cPlease wait %time second(s) before your next command.");
		CHAT_WAIT_MESSAGE = getString("chat.wait-time", "&cPlease wait %time second(s) before your next message.");
		
		UPDATE_AVAILABLE = getString("update-available", "&2A new version of &3ChatControl&2 is available.\\n&2Current version: &f%current&2; New version: &f%new\\n&2You can disable this notification in the config.");
		NO_PERMISSION = getString("no-permission",  "&cInsufficient permission.");
		RELOAD_COMPLETE = getString("reload-completed", "%prefix &2Reloading done. &7Beware: Reload might not be safe. In case of issues try restarting server.");
		
		MUTE_SUCCESS = getString("chat.mute", "&7Chat was successfully muted.");
		MUTE_UNMUTE_SUCCESS = getString("chat.unmute", "%prefix &eChat was muted by %player!");
		
		CHATCLEAR_CONSOLE = getString("chat-clear.console-message", "%prefix &7Console was successfully cleared.");
		CHATCLEAR_STAFF = getString("chat-clear.staff-message", "&7^----- [ == &fChat was cleared by %player &7== ] -----^");
		
		USAGE_FAKE_CMD = getString("usage.fake-cmd", "%prefix Usage: /chatcontrol fake <&bjoin&f/&aleave&f>");
	}
}
