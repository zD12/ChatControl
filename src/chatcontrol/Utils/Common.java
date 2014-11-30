package chatcontrol.Utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Writer.TypSuboru;

@SuppressWarnings("deprecation")
public class Common {

	private static ConsoleCommandSender console = Bukkit.getConsoleSender();

	public static void sendRawMsg(CommandSender pl, String str){
		pl.sendMessage( colorize( str.replace("%prefix", prefix()).replace("%player", resolvedSender(pl)) ) );
	}
	
	public static void sendRawTimedMsg(final Player pl, final String str, int delay_in_seconds){
		Bukkit.getScheduler().runTaskLater(ChatControl.plugin, new Runnable() {
			@Override
			public void run() {
				pl.sendMessage( colorize( str.replace("%prefix", prefix()).replace("%player", resolvedSender(pl)) ) );
			}
		}, delay_in_seconds * 20);
	}

	/**
	 * Colorizes msg. </h>
	 * Handles %prefix and %player.
	 */
	public static void sendRawMsg(Player pl, String str){
		pl.sendMessage( colorize( str.replace("%prefix", prefix()).replace("%player", pl.getName()) ) );
	}

	/**
	 * Colorizes msg. </h>
	 * Handles %prefix and %player.
	 */
	public static void sendRawMsg(CommandSender pl, String... msgs){
		for (String msg : msgs)
			pl.sendMessage( colorize(msg.replace("%prefix", prefix()).replace("%player", resolvedSender(pl)) ) );
	}

	/**
	 * Colorizes msg and handles %prefix </h>
	 */
	public static void sendColoredMsg(Player pl, String str){
		pl.sendMessage( colorize( str.replace("%prefix", prefix()) ) );
	}

	public static void sendRawMsg(Player pl, String... msgs){
		for (String msg : msgs)
			pl.sendMessage( colorize( msg.replace("%prefix", prefix()).replace("%player", pl.getName()) ) );
	}

	public static void sendMsg(CommandSender pl, String str){
		try {
			pl.sendMessage( colorize(ChatControl.Config.getString(str).replace("%prefix", prefix()).replace("%player", resolvedSender(pl)) ) );
		} catch (Exception ex){
			pl.sendMessage( colorize("&e<Missing language key: &6\"" + str + "\"&e>"));
		}
	}

	public static void sendMsg(Player pl, String str){
		sendMsg(pl, str, pl.getName());
	}
	
	public static void sendMsg(Player pl, String str, String plReplacement){
		if(ChatControl.Config.getString(str).isEmpty() || ChatControl.Config.getString(str).equalsIgnoreCase("none")) {
			debug("Message in config path \"" + str + "\" is none or \"\", so nothing is sent to " + pl.getName());
			return;
		}
		try {
			pl.sendMessage( colorize(ChatControl.Config.getString(str).replace("%prefix", prefix()).replace("%player", plReplacement) ) );
		} catch (Exception ex){
			pl.sendMessage( colorize("&e<Missing language key: &6\"" + str + "\"&e>"));
		}
	}

	public static String prefix(){
		if (ChatControl.Config.getString("Localization.Prefix") == null)
			return colorize(ChatControl.Config.getString("Localization.Prefix"));
		return ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "ChatControl" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE;
	}

	public static String console(){
		if (ChatControl.Config.getString("Localization.Console") == null)
			return colorize(ChatControl.Config.getString("Localization.Console"));
		return ChatColor.RED + "server";
	}

	public static void broadcastMsg(CommandSender pl, String configPath, String message, String reason){
		try {
			if(!ChatControl.Config.getBoolean(configPath))
				return;
		} catch (NullPointerException ex){
			ChatControl.Config.set(configPath, false);
			Bukkit.broadcastMessage(colorize("&e<Missing config key: &6\"" + configPath + "\"&e>"));
			return;
		}
		
		try {
			String finalMessage = ChatControl.Config.getString(message).replace("%prefix", prefix()).replace("%player", (pl == null ? "" : resolvedSender(pl)) );
			Bukkit.broadcastMessage(colorize(finalMessage + (reason == "" ? "" : " " + ChatControl.Config.getString("Localization.Reason").replace("%reason", reason))) );
		} catch (NullPointerException ex){
			Bukkit.broadcastMessage(colorize("&e<Missing language key: &6\"" + message + "\"&e>"));
		}
	}

	public static void messages(Player pl, String msg){
		if(ChatControl.Config.getBoolean("Anti_Ad.Inform_Admins"))
			for(Player hrac : Bukkit.getOnlinePlayers())
				if(hrac.isOp() || hrac.hasPermission(Permissions.Notify.ad))
					sendColoredMsg(hrac, ChatControl.Config.getString("Localization.Ad_Staff_Message").replace("%message", msg).replace("%player", pl.getName()));
		
		sendMsg(pl, "Localization.Ad_Message");
		
		if(ChatControl.Config.getBoolean("Anti_Ad.Broadcast"))
			for(Player hrac : Bukkit.getOnlinePlayers())
				if(!hrac.isOp() && !hrac.getName().equals(pl.getName()))
					sendColoredMsg(hrac, ChatControl.Config.getString("Localization.Ad_Broadcast_Message").replace("%message", msg).replace("%player", pl.getName()));

		if(ChatControl.Config.getBoolean("Anti_Ad.Console_Message"))
			Log(ChatControl.Config.getString("Localization.Ad_Console_Message").replace("%player", pl.getName()).replace("%message", msg));
		
		if(ChatControl.Config.getBoolean("Anti_Ad.Write_To_File"))
			Writer.zapisatDo(TypSuboru.REKLAMY, pl.getName(), msg);		
	}

	public static boolean hasPerm(Player pl, String perm){
		if(pl.hasPermission(perm))
			return true;
		if(pl.isOp() && ChatControl.Config.getBoolean("Miscellaneous.Op_Has_Permissions", true))
			return true;
		return false;
	}

	public static void customAction(final Player pl, final String action, final String msg){
		if(!ChatControl.Config.getString(action).equalsIgnoreCase("none")){
			Bukkit.getScheduler().scheduleSyncDelayedTask(ChatControl.plugin, new Runnable() {
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), colorize(ChatControl.Config.getString(action).replace("%player", pl.getName()).replace("%message", msg)));
				}
			});
		}
	}

	public static String insertDot(String msg){
		if(!ChatControl.Config.getBoolean("Grammar.Insert_Dot.Enabled"))
			return msg;		
		if(msg.length() < ChatControl.Config.getInt("Grammar.Insert_Dot.Minimum_Msg_Length")) 
			return msg;
		
		String lastChar = msg.substring(msg.length() - 1);
		String[] words = msg.split("\\s");
		String lastWord = words[(words.length - 1)];
		
		if ((lastChar.matches("(?i)[a-z]")) && (!jeOdkaz(lastWord)))
			msg = msg + ".";
		
		return msg;
	}

	public static String capitalize(String msg){
		if(!ChatControl.Config.getBoolean("Grammar.Capitalize.Enabled"))
			return msg;		
		if(msg.length() < ChatControl.Config.getInt("Grammar.Capitalize.Minimum_Msg_Length"))
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

	public static String replaceCharacters(Player pl, String msg){
		String finalMsg = msg;
		if(ChatControl.ChatConfig.getConfig().getBoolean("Replacing_Characters.Supply_With_Smileys"))
			finalMsg = osmajlikovat(msg);
		
		if(ChatControl.ChatConfig.getConfig().getBoolean("Replacing_Characters.Enabled"))
			for (String character : ChatControl.ChatConfig.getConfig().getConfigurationSection("Replacing_Characters.Replace_List").getKeys(false))
				finalMsg = finalMsg.replaceAll("(?i)" + Common.colorize(character), ChatControl.ChatConfig.getConfig().getString("Replacing_Characters.Replace_List." + character));

		return finalMsg;
	}

	public static String osmajlikovat(String msg){
		msg = msg.replace(":)", "☺").replace(":-)", "☺").replace(":(", "☹").replace(":-(", "☹").replace(";)", "㋡").replace(";-)", "㋡").replace(":love:", "♥")
			      .replace(":square:", "■").replace(":rectangle:", "█").replace("<3", "♥");
		return msg;
	}

	private static boolean jeOdkaz(String str){
		return str.matches("(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");
	}


	public static void Log(String str) {
		console.sendMessage("[ChatControl] " + colorize(str));
	}
	
	public static void Warn(String str) {
		Logger.getLogger("Minecraft").log(Level.WARNING, colorize(str));
	}

	public static void debug(String str){
		if(debugEnabled())
			console.sendMessage("[ChatControl Debug] " + colorize(str));
	}
	
	public static boolean debugEnabled() {
		return ChatControl.Config.getBoolean("Miscellaneous.Debug");
	}

	public static void error(String str, Throwable ex){
		throw new Error("Exception in ChatControl " + ChatControl.plugin.getDescription().getVersion() + ": " + colorize(str), ex);
	}

	public static String colorize(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String resolvedSender(CommandSender sender){
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

	public static boolean stringsAreSimilar(String string1, String string2) {
		if ((string1 == null) || (string2 == null)) {
			return false;
		}
		String str1;
		String str2;
		if (string2.length() < string1.length()) {
			str1 = string2;
			str2 = string1;
		} else {
			str1 = string1;
			str2 = string2;
		}

		if (str1.length() <= 5) {
			return str1.equals(str2);
		}

		int integer = str2.length() - str2.length() / 4;

		if (str1.length() < integer) {
			return false;
		}

		int count = 0;
		for (int lenght = 0; lenght < str1.length(); lenght++) {
			if (str1.charAt(lenght) == str2.charAt(lenght)) {
				count++;
			}
			if (count > integer) {
				return true;
			}
		}

		for (int lenght = 0; lenght < str1.length(); lenght++) {
			if (str1.charAt(str1.length() - lenght - 1) == str2.charAt(str2.length() - lenght - 1)) {
				count++;
			}
			if (count > integer) {
				return true;
			}
		}
		return false;
	}

	public static int[] checkCaps(String message) {
		int[] editedMsg = new int[message.length()];
		String[] parts = message.split(" ");
		for (int i = 0; i < parts.length; i++) {
			for (String whitelisted : ChatControl.Config.getStringList("Anti_Caps.Whitelist")) {
				if (whitelisted.equalsIgnoreCase(parts[i])) {
					parts[i] = parts[i].toLowerCase();
				}
			}
		}

		String msg = StringUtils.join(parts, " ");

		for (int j = 0; j < msg.length(); j++) {
			if ((Character.isUpperCase(msg.charAt(j))) && (Character.isLetter(msg.charAt(j)))) {
				editedMsg[j] = 1;
			} else {
				editedMsg[j] = 0;
			}
		}
		return editedMsg;
	}

	public static int percentageCaps(int[] caps) {
		int sum = 0;
		for (int i = 0; i < caps.length; i++) {
			sum += caps[i];
		}
		double ratioCaps = sum / caps.length;
		int percentCaps = (int)(100.0D * ratioCaps);
		return percentCaps;
	}

	public static int checkCapsInRow(int[] caps) {
		int sum = 0;
		int sumTemp = 0;
		for (int i : caps) {
			if (i == 1) {
				sumTemp++;
				sum = Math.max(sum, sumTemp);
			} else {
				sumTemp = 0;
			}
		}
		return sum;
	}

	public static String toAnsiColors(String str) {
		str = colorize(str);
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
        TimedCharSequence timedMsg = new TimedCharSequence(plain_msg, ChatControl.Config.getInt("Miscellaneous.RegEx_Timeout_Milis"));

        debug("Checking " + plain_msg + " against " + regex);
        
		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException ex){
			Log("&cInvalid regex: " + regex);
			Log("&eUse online services (like regex101.com) for fixing errors");
			ex.printStackTrace();
			return false;
		}
		
		Matcher matcher = pattern.matcher(timedMsg);
		
		try {
			return matcher.find();
		} catch (RuntimeException ex) {
            Writer.zapisatDo(TypSuboru.ZAZNAM_CHYB, null, "Regex check timed out (bad regex?) (plugin ver. " + ChatControl.plugin.getDescription().getVersion() + ")! \nString checked: " + timedMsg + "\nRegex: " + pattern.pattern());
            Common.error("RegEx \"" + pattern.pattern() + "\" has timed out while checking \"" + timedMsg + "\"! (malformed regex?)", ex);
            return false;
		}
	}

	public static void swearActions(String theMessage, Player swearer) {
		if(ChatControl.Config.getBoolean("Anti_Swear.Inform_Admins")) 
			for (Player pl : Bukkit.getOnlinePlayers())
				if (hasPerm(pl, Permissions.Notify.swear)) 
					Common.sendColoredMsg(pl, ChatControl.Config.getString("Localization.Swear_Admin_Message").replace("%message", theMessage).replace("%player", swearer.getName()));
				
		if (ChatControl.Config.getBoolean("Anti_Swear.Warn_Player"))
			Common.sendMsg(swearer, "Localization.Do_Not_Swear");
		
		Common.customAction(swearer, "Anti_Swear.Custom_Command", theMessage);
	}
}
