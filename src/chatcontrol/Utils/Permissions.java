package chatcontrol.Utils;

public class Permissions {

	public class Commands {
		// Permission to use commands. Use chatcontrol.commands.* to allow use of all commands.
		public static final String global_perm = "chatcontrol.commands";
		
		// Permission for "/chatcontrol mute" command.
		public static final String mute = "chatcontrol.commands.mute";
		public static final String muteSilent = "chatcontrol.commands.mute.silent";
		public static final String muteAnonymous = "chatcontrol.commands.mute.anonymous";
		
		// Permission for "/chatcontrol clear" command.
		public static final String clear = "chatcontrol.commands.clear";
		public static final String clearSilent = "chatcontrol.commands.clear.silent";
		public static final String clearAnonymous = "chatcontrol.commands.clear.anonymous";
		public static final String clearConsole = "chatcontrol.commands.clear.console";
		
		// Permission for "/chatcontrol reload" command.
		public static final String reload = "chatcontrol.commands.reload";
		
		// Permission for "/chatcontrol list" command.
		public static final String command_list = "chatcontrol.commands.list";
		
		// Permission for "/chatcontrol fake" command.
		public static final String fake = "chatcontrol.commands.fake";
	}

	public class Bypasses {
		// Bypass everything, basically equals to OP status.
		public static final String global_perm = "chatcontrol.bypass.*";
		
		// Allow player chat even when the chat is muted.
		public static final String mute = "chatcontrol.bypass.mute";
		
		// Bypass command message delay.
		public static final String timeCmd = "chatcontrol.bypass.time.commands";
		
		// Bypass chat message delay.
		public static final String timeChat = "chatcontrol.bypass.time.chat";
		
		// Allow player to send duplicate commands.
		public static final String dupeCmd = "chatcontrol.bypass.dupe.commands";
		
		// Allow player to send duplicate messages.
		public static final String dupeChat = "chatcontrol.bypass.dupe.chat";
		
		// Allow player to place signs with same text.
		public static final String dupeSigns = "chatcontrol.bypass.dupe.signs";
		
		// Bypass advertising check.
		public static final String ads = "chatcontrol.bypass.ad";
		
		// Bypass swear check.
		public static final String swear = "chatcontrol.bypass.swear";
		
		// Allow player to chat even when not moved on game join. NOT RECOMMENDED due to spam bots.
		public static final String move = "chatcontrol.bypass.move";
		
		// Bypass check for CAPS.
		public static final String caps = "chatcontrol.bypass.caps";
		
		// Allow player to bypass rejoin check.
		public static final String rejoin = "chatcontrol.bypass.rejoin";
		
		// Allow player to tab complete.
		public static final String tab_complete = "chatcontrol.bypass.tabcomplete";
		
		// Players chat don´t get clear, he recieve just one message.
		public static final String chat_clear = "chatcontrol.bypass.clear";
		
		// Bypass replacing characters (from chat.yml file).
		public static final String replace = "chatcontrol.bypass.replace";
		
		// Bypass Capitalizing the Sentences.
		public static final String capitalize = "chatcontrol.bypass.capitalize";
		
		// Don´t insert dot "." after a sentence.
		public static final String insertDot = "chatcontrol.bypass.insertdot";
	}

	public class Notify {
		// Permission to recieve warning when a message gets blocked as a swear.
		public static final String swear = "chatcontrol.notify.swear";
		
		// Permission to recieve warning when a message gets blocked as an ad.
		public static final String ad = "chatcontrol.notify.ad";
		
		// Permission to recieve warning when a new version of ChatControl is available.
		public static final String plugin_update = "chatcontrol.notify.update";
		
		public static final String whenMentioned = "chatcontrol.notify.whenmentioned";
	}
	
	public class Formatter {
		public static final String color = "chatcontrol.chat.color";
		
		public static final String magic = "chatcontrol.chat.magic";
		
		public static final String bold = "chatcontrol.chat.bold";
		
		public static final String strikethrough = "chatcontrol.chat.strikethrough";
		
		public static final String underline = "chatcontrol.chat.underline";
		
		public static final String italic = "chatcontrol.chat.italic";
		
		// Permission to speak in global chat (begin message with "!") when chat range is enabled.
		public static final String globalChat = "chatcontrol.chat.global";
		
		// Permission to override the chat range (when enabled) and force speak to global chat.
		public static final String overrideRanged = "chatcontrol.chat.overrideranged";
	}
}
