package chatcontrol.Utils;

public class Permissions {

	public class Commands {
		// Permission to use all the commands.
		public static final String global_perm = "chatcontrol.commands";
		
		// Permission for "/chatcontrol mute" command.
		public static final String mute = "chatcontrol.commands.mute";
		public static final String muteSilent = "chatcontrol.commands.mute.silent";
		public static final String muteAnonymous = "chatcontrol.commands.mute.anonymous";
		
		// Permission for "/chatcontrol clear" command.
		public static final String clear = "chatcontrol.commands.clear";
		public static final String clearSilent = "chatcontrol.commands.clear.silent";
		public static final String clearAnonymous = "chatcontrol.commands.clear.anonymous";
		
		// Permission for "/chatcontrol reload" command.
		public static final String reload = "chatcontrol.commands.reload";
		
		// Permission for "/chatcontrol list" command.
		public static final String command_list = "chatcontrol.commands.list";
		
		// Permission for "/chatcontrol fake" command.
		public static final String fake = "chatcontrol.commands.fake";
	}

	public class Bypasses {
		// Permission to bypass everything, basically equals to OP status.
		// Permissions below should be easy to understand, if not please ask at ChatControl´s page at Spigot forums 
		public static final String global_perm = "chatcontrol.bypass";
		public static final String mute = "chatcontrol.bypass.mute";
		public static final String time = "chatcontrol.bypass.time";
		public static final String dupe = "chatcontrol.bypass.dupe";
		public static final String ads = "chatcontrol.bypass.ad";
		public static final String swear = "chatcontrol.bypass.swear";
		public static final String move = "chatcontrol.bypass.move";
		public static final String caps = "chatcontrol.bypass.caps";
		public static final String rejoin = "chatcontrol.bypass.rejoin";
		public static final String tab_complete = "chatcontrol.bypass.tabcomplete";
		public static final String chat_clear = "chatcontrol.bypass.clear";
	}

	public class Notify {
		// Permission to recieve warning when a message gets blocked as a swear.
		public static final String swear = "chatcontrol.notify.swear";
		
		// Permission to recieve warning when a message gets blocked as an ad.
		public static final String ad = "chatcontrol.notify.ad";
		
		// Permission to recieve warning when a new version of ChatControl is available.
		public static final String plugin_update = "chatcontrol.notify.update";
	}
	
	public static final String global_perm = "chatcontrol";
	
}
