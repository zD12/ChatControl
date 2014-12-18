package me.kangarko.chc.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.SettingsChat;
import me.kangarko.chc.utils.Writer.TypSuboru;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

@SuppressWarnings("deprecation")
public class Common {

	private static final ConsoleCommandSender console;

	static {
		console = Bukkit.getServer().getConsoleSender();
	}

	/**
	 * Basic, colorizes msg and handles %prefix.
	 * every other method to tell player should extend this
	 */
	public static void tellColored(CommandSender sender, String msg) {
		sender.sendMessage(colorize(msg.replace("%prefix", prefix())));
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

	// TODO get rid of
	@Deprecated
	public static String prefix() {
		return Localization.Parts.PREFIX;
	}

	// TODO get rid of
	@Deprecated
	public static String console() {
		return Localization.Parts.CONSOLE;
	}

	public static void broadcastMsgIfEnabled(CommandSender pl, String configPath, String message, String reason) {
		/* TODO try {
			if(!ChatControl.Config.getBoolean(configPath))
				return;
		} catch (NullPointerException ex){
			ChatControl.Config.set(configPath, false);
			Bukkit.broadcastMessage(colorize("&e<Missing config key: &6\"" + configPath + "\"&e>"));
			return;
		}*/

		try {
			String finalMessage = "NOT_IMPLEMENTED_BROADCAST_IF_ENABLED".replace("%prefix", prefix()).replace("%player", (pl == null ? "" : resolvedSender(pl)));
			Bukkit.broadcastMessage(colorize(finalMessage + (reason == "" ? "" : " " + Localization.Parts.PREFIX.replace("%reason", reason))));
		} catch (NullPointerException ex) {
			Bukkit.broadcastMessage(colorize("&e<Missing language key: &6\"" + message + "\"&e>"));
		}
	}

	public static void messages(Player pl, String msg) {
		if (Settings.AntiAd.ALERT_STAFF)
			for (Player hrac : Bukkit.getOnlinePlayers())
				if (hrac.isOp() || hrac.hasPermission(Permissions.Notify.ad))
					tellColored(hrac, Localization.ANTIAD_STAFF_ALERT.replace("%message", msg).replace("%player", pl.getName()));

		tell(pl, Localization.ANTIAD_PLAYER_WARN);

		if (Settings.AntiAd.BROADCAST_BLOCK)
			for (Player hrac : Bukkit.getOnlinePlayers())
				if (!hrac.isOp() && !hrac.getName().equals(pl.getName()))
					tellColored(hrac, Localization.ANTIAD_BROADCAST_ALERT.replace("%message", msg).replace("%player", pl.getName()));

		if (Settings.AntiAd.ALERT_CONSOLE)
			Log(Localization.ANTIAD_CONSOLE_ALERT.replace("%player", pl.getName()).replace("%message", msg));

		if (Settings.AntiAd.WRITE_TO_FILE)
			Writer.zapisatDo(TypSuboru.REKLAMY, pl.getName(), msg);
	}

	public static boolean hasPerm(CommandSender sender, String str) {
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

		if ((lastChar.matches("(?i)[a-z]")) && (!jeOdkaz(lastWord)))
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

			if (!jeOdkaz(slovo))
				sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);

			tempMessage = tempMessage + sentence + " ";
		}
		return tempMessage.trim();
	}

	public static String replaceCharacters(Player pl, String msg) {
		String finalMsg = msg;

		if (SettingsChat.smileys)
			finalMsg = osmajlikovat(msg);

		if (!SettingsChat.replaceMap.isEmpty())
			for (String character : SettingsChat.replaceMap.keySet())
				finalMsg = finalMsg.replaceAll("(?i)" + Common.colorize(character), SettingsChat.replaceMap.get(character));

		return finalMsg;
	}

	public static String osmajlikovat(String msg) { // FIXME Corrupted characters
		msg = msg.replace(":)", "�?�").replace(":-)", "�?�").replace(":(", "�?�").replace(":-(", "�?�").replace(";)", "㋡").replace(";-)", "㋡").replace(":love:", "♥").replace(":square:", "■").replace(":rectangle:", "�?").replace("<3", "♥");
		return msg;
	}

	private static boolean jeOdkaz(String str) {
		return str.matches("(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");
	}

	public static void Log(String str) {
		console.sendMessage("[ChatControl] " + colorize(str));
	}

	public static void Warn(String str) {
		Logger.getLogger("Minecraft").log(Level.WARNING, "[ChatControl] " + colorize(str));
	}

	public static void Debug(String str) {
		if (debugEnabled())
			console.sendMessage("[ChatControl Debug] " + colorize(str));
	}

	public static boolean debugEnabled() { // TODO debug levels?
		return Settings.DEBUG;
	}

	public static void error(String str, Throwable ex) {
		throw new Error("Exception in ChatControl " + ChatControl.instance().getDescription().getVersion() + ": " + colorize(str), ex);
	}

	public static String colorize(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String resolvedSender(CommandSender sender) {
		if (sender instanceof Player) {
			return sender.getName();
		}
		return console();
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

	public static boolean regExMatch(String regex, String plain_msg) {
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
			Writer.zapisatDo(TypSuboru.ZAZNAM_CHYB, null, "Regex check timed out (bad regex?) (plugin ver. " + ChatControl.instance().getDescription().getVersion() + ")! \nString checked: " + timedMsg + "\nRegex: " + pattern.pattern());
			Common.error("RegEx \"" + pattern.pattern() + "\" has timed out while checking \"" + timedMsg + "\"! (malformed regex?)", ex);
			return false;
		}
	}

	public static void swearActions(String theMessage, Player swearer) {
		if (Settings.AntiSwear.ALERT_STAFF)
			for (Player pl : Bukkit.getOnlinePlayers())
				if (hasPerm(pl, Permissions.Notify.swear))
					Common.tellColored(pl, Localization.ANTISWEAR_STAFF_ALERT.replace("%message", theMessage).replace("%player", swearer.getName()));

		if (Settings.AntiSwear.WARN_PLAYER)
			Common.tell(swearer, Localization.ANTISWEAR_PLAYER_WARN);

		Common.customAction(swearer, "Anti_Swear.Custom_Command", theMessage);
	}
}
