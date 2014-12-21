package me.kangarko.chc.utils;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.SettingsChat;
import me.kangarko.chc.utils.Writer.FileType;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

public class Common {

	private static final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	/**
	 * Basic, colorizes msg and handles %prefix.
	 * every other method to tell player should extend this
	 */
	public static void tellColored(CommandSender sender, String msg) {
		sender.sendMessage(colorize(msg));
	}

	/**
	 * Sends sendColoredMsg with resolved %player.
	 */
	public static void tell(CommandSender sender, String... messages) {
		for (String msg : messages)
			tellColored(sender, msg.replace("%player", resolvedSender(sender)));
	}

	/**
	 * Sends sendColoredMsg with known specified %player in last argument.
	 */
	public static void tell(CommandSender pl, String msg, String plReplacement) {
		tellColored(pl, msg.replace("%player", plReplacement));
	}

	public static void tellTimed(CommandSender pl, String str, int delay_in_seconds) {
		new BukkitRunnable() {
			@Override
			public void run() {
				tell(pl, str);
			}
		}.runTaskLater(ChatControl.instance(), delay_in_seconds * 20);
	}

	public static void broadcast(String message) {
		Bukkit.broadcastMessage(colorize(message));
	}

	public static void broadcastWithPlayer(String message, String playerReplacement) {
		broadcast(message.replace("%player", playerReplacement));
	}

	public static void broadcastIfEnabled(boolean enabled, CommandSender plReplace, String msg, String reason) {
		if (enabled)
			broadcastWithPlayer(msg + (reason.equals("") ? "" : " " + Localization.Parts.REASON.replace("%reason", reason)), plReplace == null ? "" : resolvedSender(plReplace));
	}

	public static void messages(Player advertiser, String msg) {
		if (Settings.AntiAd.ALERT_STAFF)
			for (Player staff : Bukkit.getOnlinePlayers())
				if (hasPerm(staff, Permissions.Notify.ad))
					tell(staff, Localization.ANTIAD_STAFF_ALERT.replace("%message", msg), advertiser.getName());

		tell(advertiser, Localization.ANTIAD_PLAYER_WARN);

		if (Settings.AntiAd.BROADCAST_BLOCK)
			for (Player online : Bukkit.getOnlinePlayers())
				if (!online.isOp() && !online.getName().equals(advertiser.getName()))
					tell(online, Localization.ANTIAD_BROADCAST_ALERT.replace("%message", msg), advertiser.getName());

		if (Settings.AntiAd.ALERT_CONSOLE)
			Log(Localization.ANTIAD_CONSOLE_ALERT.replace("%player", advertiser.getName()).replace("%message", msg));

		if (Settings.AntiAd.WRITE_TO_FILE)
			Writer.zapisatDo(FileType.ADVERTISEMENTS, advertiser.getName(), msg);
	}

	public static boolean hasPerm(CommandSender sender, String str) {
		if (sender.hasPermission(str) && sender.isOp() && !Settings.OP_HAS_PERMISSIONS)
			return false;
		if (sender.isOp() && Settings.OP_HAS_PERMISSIONS)
			return true;
		if (sender.hasPermission(str))
			return true;
		return false;
	}

	/**
	 * @deprecated broken, fix
	 * TODO Fix
	 */
	public static void customAction(final Player pl, final String action, final String msg) {
		/*if(!ChatControl.Config.getString(action).equalsIgnoreCase("none")){
			Bukkit.getScheduler().scheduleSyncDelayedTask(ChatControl.instance(), new Runnable() {
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), colorize(ChatControl.Config.getString(action).replace("%player", pl.getName()).replace("%message", msg)));
				}
			});
		}*/
	}

	public static String insertDot(String msg) {
		if (!Settings.Chat.Grammar.INSERT_DOT)
			return msg;
		if (msg.length() < Settings.Chat.Grammar.INSERT_DOT_MSG_LENGTH)
			return msg;

		String lastChar = msg.substring(msg.length() - 1);
		String[] words = msg.split("\\s");
		String lastWord = words[(words.length - 1)];

		if ((lastChar.matches("(?i)[a-z]")) && (!isDomain(lastWord)))
			msg = msg + ".";

		return msg;
	}

	public static String capitalize(String msg) {
		if (!Settings.Chat.Grammar.CAPITALIZE)
			return msg;
		if (msg.length() < Settings.Chat.Grammar.CAPITALIYE_MSG_LENGTH)
			return msg;

		String[] sentences = msg.split("(?<=[!?\\.])\\s");
		String tempMessage = "";

		for (String sentence : sentences) {
			String slovo = msg.split("\\s")[0];

			if (!isDomain(slovo))
				sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);

			tempMessage = tempMessage + sentence + " ";
		}
		return tempMessage.trim();
	}

	public static String replaceCharacters(Player pl, String msg) {
		String finalMsg = msg;

		if (!SettingsChat.REPLACE_UTF_MAP.isEmpty())
			for (String character : SettingsChat.REPLACE_UTF_MAP.keySet())
				finalMsg = finalMsg.replace(colorize(character), SettingsChat.REPLACE_UTF_MAP.get(character));
		
		if (!SettingsChat.REPLACE_REGEX_MAP.isEmpty())
			for (String character : SettingsChat.REPLACE_REGEX_MAP.keySet())
				finalMsg = finalMsg.replaceAll(colorize(character), SettingsChat.REPLACE_REGEX_MAP.get(character));

		return colorize(finalMsg);
	}

	// ---------------------------- PRIVATE --------------------------------------

	private static String setPrefix(String str) {
		return str.replace("%prefix", Localization.Parts.PREFIX);
	}

	private static boolean isDomain(String str) {
		return str.matches("(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");
	}

	// -------------------------------------------------------------------

	public static void Log(String str) {
		console.sendMessage("[ChatControl] " + colorize(str));
	}

	public static void Warn(String str) {
		Bukkit.getLogger().log(Level.WARNING, "[ChatControl] " + colorize(str));
	}

	public static void Debug(String str) {
		if (debugEnabled())
			console.sendMessage("[ChatControl Debug] " + colorize(str));
	}

	public static boolean debugEnabled() { // TODO debug levels?
		return Settings.DEBUG;
	}

	public static void Error(String str, Throwable ex) {
		throw new Error("Error in ChatControl " + ChatControl.instance().getDescription().getVersion() + ": " + colorize(str), ex);
	}

	public static String colorize(String str) {
		return ChatColor.translateAlternateColorCodes('&', setPrefix(str));
	}

	public static String resolvedSender(CommandSender sender) {
		if (sender instanceof Player)
			return sender.getName();

		return Localization.Parts.CONSOLE;
	}

	public static String stripSpecialCharacters(String str) {
		str = str.toLowerCase();
		str = str.replaceAll("(.)(?=\\1\\1+)", ""); // duplicate strip
		str = str.replaceAll("(..)(?=\\1\\1+)", ""); // duplicate strip
		str = str.replaceAll("(...)(?=\\1\\1+)", ""); // duplicate strip
		str = str.replaceAll("§([0-9a-fk-or])", "").replaceAll("[^a-zA-Z0-9]", ""); // strip spec. characters INCLUDES spaces " "
		return str;
	}

	public static String prepareForSwearCheck(String str) {
		str = str.toLowerCase();
		str = str.replaceAll("[^a-zA-Z\\d\\s:]", ""); //strips special characters expect spaces
		str = str.replaceAll("(.)(?=\\1\\1+)", ""); // duplicate strip
		str = str.replaceAll("(..)(?=\\1\\1+)", ""); // duplicate strip
		str = str.replaceAll("(...)(?=\\1\\1+)", ""); // duplicate strip
		return str;
	}

	public static String stripDuplicate(String str) {
		str = str.replaceAll("(.)\\1+", "$1"); // hardcore duplicate strip
		return str;
	}

	public static int[] checkCaps(String message) {
		int[] editedMsg = new int[message.length()];
		String[] parts = message.split(" ");

		for (int i = 0; i < parts.length; i++)
			for (String whitelisted : Settings.AntiCaps.WHITELIST)
				if (whitelisted.equalsIgnoreCase(parts[i]))
					parts[i] = parts[i].toLowerCase();

		String msg = StringUtils.join(parts, " ");

		for (int j = 0; j < msg.length(); j++) {
			if (Character.isUpperCase(msg.charAt(j)) && Character.isLetter(msg.charAt(j)))
				editedMsg[j] = 1;
			else
				editedMsg[j] = 0;
		}
		return editedMsg;
	}

	public static int percentageCaps(int[] caps) {
		int sum = 0;
		for (int i = 0; i < caps.length; i++)
			sum += caps[i];

		double ratioCaps = sum / caps.length;
		int percentCaps = (int) (100.0D * ratioCaps);
		return percentCaps;
	}

	public static int checkCapsInRow(int[] caps) {
		int sum = 0;
		int sumTemp = 0;

		for (int i : caps) {
			if (i == 1) {
				sumTemp++;
				sum = Math.max(sum, sumTemp);
			} else
				sumTemp = 0;
		}
		return sum;
	}

	public static String toAnsiColors(String str) {
		str = str.replace(ChatColor.COLOR_CHAR, '&');
		str = str.replace("&0", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
		str = str.replace("&1", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
		str = str.replace("&2", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
		str = str.replace("&3", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
		str = str.replace("&4", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
		str = str.replace("&5", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
		str = str.replace("&6", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
		str = str.replace("&7", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
		str = str.replace("&8", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
		str = str.replace("&9", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
		str = str.replace("&a", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
		str = str.replace("&b", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
		str = str.replace("&c", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
		str = str.replace("&d", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
		str = str.replace("&e", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
		str = str.replace("&f", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
		str = str.replace("&k", Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
		str = str.replace("&l", Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString());
		str = str.replace("&m", Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
		str = str.replace("&n", Ansi.ansi().a(Attribute.UNDERLINE).toString());
		str = str.replace("&o", Ansi.ansi().a(Attribute.ITALIC).toString());
		str = str.replace("&r", Ansi.ansi().a(Attribute.RESET).toString());
		return str;
	}

	static boolean serverCrash = true;
	
	public static boolean regExMatch(String regex, String plain_msg) {
		if (serverCrash)
			System.out.println("Lol thats what you get");
		
		Pattern pattern = null;
		TimedCharSequence timedMsg = new TimedCharSequence(plain_msg, Settings.REGEX_TIMEOUT);

		Debug("Checking " + plain_msg + " against " + regex);

		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException ex) {
			Log("&cInvalid regex: " + regex);
			Log("&eUse online services (like regex101.com) for fixing errors");
			ex.printStackTrace();
			return false;
		}

		Matcher matcher = pattern.matcher(timedMsg);

		try {
			return matcher.find();
		} catch (RuntimeException ex) {
			Writer.zapisatDo(FileType.ERROR_LOG, null, "Regex check timed out (bad regex?) (plugin ver. " + ChatControl.instance().getDescription().getVersion() + ")! \nString checked: " + timedMsg + "\nRegex: " + pattern.pattern());
			Error("RegEx \"" + pattern.pattern() + "\" has timed out while checking \"" + timedMsg + "\"! (malformed regex?)", ex);
			return false;
		}
	}

	public static void swearActions(String theMessage, Player swearer) {
		if (Settings.AntiSwear.ALERT_STAFF)
			for (Player pl : Bukkit.getOnlinePlayers())
				if (hasPerm(pl, Permissions.Notify.swear))
					tell(pl, Localization.ANTISWEAR_STAFF_ALERT.replace("%message", theMessage), swearer.getName());

		if (Settings.AntiSwear.WARN_PLAYER)
			tell(swearer, Localization.ANTISWEAR_PLAYER_WARN);

		customAction(swearer, "Anti_Swear.Custom_Command", theMessage);
	}
}
