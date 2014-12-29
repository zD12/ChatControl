package kangarko.chatcontrol.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.model.SettingsRemap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

public class Common {

	private static final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private static String INTERNAL_PREFIX = "";

	public static void addLoggingPrefix() {
		INTERNAL_PREFIX = "[ChatControl] ";
	}
	
	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	/**
	 * DAY.MONTH.YEAR HOUR:MINUTES:SECONDS
	 */
	public static String getFormattedDate() {
		return dateFormat.format(System.currentTimeMillis());
	}

	/**
	 * Basic, colorizes msg and handles %prefix.
	 * every other method to tell player should extend this
	 */
	public static void tellColored(CommandSender sender, String msg) {
		if (!msg.isEmpty() && !msg.equalsIgnoreCase("none"))
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

	public static void tellLater(CommandSender pl, int delayTicks, String... msgs) {
		new BukkitRunnable() {
			@Override
			public void run() {
				tell(pl, msgs);
			}
		}.runTaskLater(ChatControl.instance(), delayTicks);
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

	public static boolean hasPerm(CommandSender sender, String str) {
		if (sender.hasPermission(str) && sender.isOp() && !Settings.OP_HAS_PERMISSIONS)
			return false;
		if (sender.isOp() && Settings.OP_HAS_PERMISSIONS)
			return true;

		return sender.hasPermission(str);
	}

	public static void customAction(Player pl, String action, String msg) {
		if (action.isEmpty() || action.equalsIgnoreCase("none"))
			return;

		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), colorize(action.replace("%player", pl.getName()).replace("%message", msg)));
			}
		}.runTask(ChatControl.instance());
	}

	public static String insertDot(String msg) {
		if (!Settings.Chat.Grammar.INSERT_DOT || msg.length() < Settings.Chat.Grammar.INSERT_DOT_MSG_LENGTH)
			return msg;

		String lastChar = msg.substring(msg.length() - 1);
		String[] words = msg.split("\\s");
		String lastWord = words[words.length - 1];

		if (!isDomain(lastWord) && lastChar.matches("(?i)[a-z]"))
			msg = msg + ".";

		return msg;
	}

	public static String capitalize(String msg) {
		if (!Settings.Chat.Grammar.CAPITALIZE || msg.length() < Settings.Chat.Grammar.CAPITALIZE_MSG_LENGTH)
			return msg;

		String[] sentences = msg.split("(?<=[!?\\.])\\s");
		String tempMessage = "";

		for (String sentence : sentences) {
			String word = msg.split("\\s")[0];

			if (!isDomain(word))
				sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);

			tempMessage = tempMessage + sentence + " ";
		}
		return tempMessage.trim();
	}

	public static String replaceCharacters(Player pl, String msg) {
		String finalMsg = msg;

		if (!SettingsRemap.REPLACE_UTF_MAP.isEmpty())
			for (String character : SettingsRemap.REPLACE_UTF_MAP.keySet())
				finalMsg = finalMsg.replace(colorize(character), SettingsRemap.REPLACE_UTF_MAP.get(character));

		if (!SettingsRemap.REPLACE_REGEX_MAP.isEmpty())
			for (String character : SettingsRemap.REPLACE_REGEX_MAP.keySet())
				finalMsg = finalMsg.replaceAll(colorize(character), SettingsRemap.REPLACE_REGEX_MAP.get(character));

		return finalMsg;
	}

	// ---------------------------- PRIVATE --------------------------------------

	private static String setPrefix(String str) {
		return str.replace("%prefix", Localization.Parts.PREFIX).replace("%server", Localization.Parts.PREFIX_SERVER);
	}

	private static boolean isDomain(String str) {
		return str.matches("(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");
	}

	// -------------------------------------------------------------------

	public static void Log(String str) {
		console.sendMessage(colorize(INTERNAL_PREFIX + str.replace("\n", "\n&r")));
	}

	public static void Warn(String str) {
		Bukkit.getLogger().log(Level.WARNING, colorize(INTERNAL_PREFIX + str));
	}

	public static void Debug(String str) {
		if (Settings.DEBUG)
			console.sendMessage(colorize("[ChatControl Debug] " + str));
	}

	public static void Error(String str, Throwable ex) {
		Bukkit.getLogger().log(Level.SEVERE, "[" + ChatControl.instance().getDescription().getVersion() + "]" + str, ex);
	}

	public static void Error(String str) {
		Bukkit.getLogger().log(Level.SEVERE, "[" + ChatControl.instance().getDescription().getVersion() + "]" + str);
	}

	public static void LogInFrame(boolean disable, String... messages) {
		Log(consoleLine());
		for (String msg : messages)
			Log(" &c" + msg);

		if (disable) {
			Bukkit.getPluginManager().disablePlugin(ChatControl.instance());
			Log(" &cPlugin is now disabled.");
		}

		Log(consoleLine());
	}

	public static String colorize(String str) {
		return ChatColor.translateAlternateColorCodes('&', setPrefix(str));
	}

	public static String resolvedSender(CommandSender sender) {
		if (sender instanceof Player)
			return sender.getName();

		return Localization.Parts.CONSOLE;
	}

	public static String prepareForSimilarityCheck(String str) {
		str = str.replaceAll(ChatColor.COLOR_CHAR + "([0-9a-fk-or])", ""); // strip colors

		if (Settings.AntiSpam.STRIP_SPECIAL_CHARS)
			str = str.replaceAll("[^a-zA-Z0-9\\s]", ""); // strip spec. characters EXCEPT spaces

		if (Settings.AntiSpam.STRIP_DUPLICATE_CHARS) {
			str = str.replaceAll("(.)(?=\\1\\1+)", "");
			str = str.replaceAll("(..)(?=\\1\\1+)", "");
			str = str.replaceAll("(...)(?=\\1\\1+)", "");
		}

		return str.toLowerCase();
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

		for (int i = 0; i < msg.length(); i++)
			if (Character.isUpperCase(msg.charAt(i)) && Character.isLetter(msg.charAt(i)))
				editedMsg[i] = 1;
			else
				editedMsg[i] = 0;
		return editedMsg;
	}

	public static int percentageCaps(int[] caps) {
		int sum = 0;
		for (int i = 0; i < caps.length; i++)
			sum += caps[i];

		double ratio = sum / caps.length;
		int percent = (int) (100.0D * ratio);
		return percent;
	}

	public static int checkCapsInRow(int[] caps) {
		int sum = 0;
		int sumTemp = 0;

		for (int i : caps)
			if (i == 1) {
				sumTemp++;
				sum = Math.max(sum, sumTemp);
			} else
				sumTemp = 0;
		return sum;
	}

	public static String toAnsiColors(String str) {
		str = str.replace(ChatColor.COLOR_CHAR, '&') + ChatColor.RESET;
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
		TimedCharSequence timedMsg = new TimedCharSequence(plain_msg.toLowerCase(), Settings.REGEX_TIMEOUT);

		Debug("Checking " + timedMsg + " against " + regex);

		try {
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException ex) {
			ex.printStackTrace();
			LogInFrame(false, "Malformed regex: \'" + regex + "\'", "Use online services (like &fregex101.com&f) for fixing errors");
			return false;
		}

		Matcher matcher = pattern.matcher(timedMsg);

		try {
			return matcher.find();
		} catch (RuntimeException ex) {
			Writer.Write(Writer.ERROR_FILE_PATH, null, "Regex check timed out (bad regex?) (plugin ver. " + ChatControl.instance().getDescription().getVersion() + ")! \nString checked: " + timedMsg + "\nRegex: " + regex + "");
			throw new RuntimeException("RegEx checking " + ex.getMessage());
		}
	}

	/**
	 * Calculates the similarity (a percentage within 0% and 100%) between two strings.
	 */
	public static int similarity(String s1, String s2) {
		String longer = s1, shorter = s2;

		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2;
			shorter = s1;
		}

		int longerLength = longer.length();

		if (longerLength == 0)
			return 100; /* both strings are zero length */

		double result = (longerLength - editDistance(longer, shorter)) / (double) longerLength;

		return (int) (result * 100);

	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	private static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++)
				if (i == 0)
					costs[j] = j;
				else if (j > 0) {
					int newValue = costs[j - 1];
					if (s1.charAt(i - 1) != s2.charAt(j - 1))
						newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
					costs[j - 1] = lastValue;
					lastValue = newValue;
				}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

	public static String consoleLine() {
		return "&6*----------------------------------------------*";
	}

	public static String shortLocation(Location loc) {
		return loc.getWorld().getName() + " x:" + (int) loc.getX() + " y:" + (int) loc.getY() + " z:" + (int) loc.getZ();
	}
}
