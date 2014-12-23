package kangarko.chatcontrol.checks;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;
import kangarko.chatcontrol.utils.Writer;
import kangarko.chatcontrol.utils.Writer.FileType;

import org.bukkit.entity.Player;

public class ChecksUtils {

	/**
	 * Return modified message if the check found an ad.
	 */
	public static boolean advertisementCheck(Player pl, String msg, boolean command, boolean sign) {
		if (Common.hasPerm(pl, Permissions.Bypasses.ADVERTISING))
			return false;

		if (command && !Settings.AntiAd.ENABLED_IN_COMMANDS)
			return false;

		if (sign && !Settings.Signs.CHECK_FOR_ADS)

			for (String ip : Settings.AntiAd.WHITELIST_IP)
				if (msg.contains(ip))
					return false;

		for (String domeny : Settings.AntiAd.WHITELIST_DOMAINS)
			if (msg.toLowerCase().contains(domeny.toLowerCase()))
				return false;

		String finalMsg = msg.replaceAll(Settings.AntiAd.PREPROCESS_STRIP, "");

		boolean isAd = false;

		if (Common.regExMatch(Settings.AntiAd.REGEX_IP, finalMsg) || Common.regExMatch(Settings.AntiAd.REGEX_DOMAINS, finalMsg))
			isAd = true;

		if (!Settings.AntiAd.REGEX_CUSTOM.equalsIgnoreCase("none"))
			if (Common.regExMatch(Settings.AntiAd.REGEX_CUSTOM, finalMsg))
				isAd = true;

		if (isAd) {
			Common.customAction(pl, Settings.AntiAd.DISPATCH_COMMAND, msg);

			if (command)
				msg = "&7[" + Localization.Parts.SIGN + "&7]&f " + msg;
			else if (sign)
				msg = "&7[" + Localization.Parts.COMMAND + "&7]&f " + msg;

			if (Settings.AntiAd.ALERT_STAFF)
				for (Player staff : ChatControl.getOnlinePlayers())
					if (Common.hasPerm(staff, Permissions.Notify.ADVERTISE))
						Common.tell(staff, Localization.ANTIAD_STAFF_ALERT.replace("%message", msg), pl.getName());

			if (Settings.AntiAd.BROADCAST_BLOCK)
				for (Player online : ChatControl.getOnlinePlayers())
					if (!online.isOp() && !online.getName().equals(pl.getName()))
						Common.tell(online, Localization.ANTIAD_BROADCAST_ALERT.replace("%message", msg), pl.getName());

			if (Settings.AntiAd.ALERT_CONSOLE)
				Common.Log(Localization.ANTIAD_CONSOLE_ALERT.replace("%player", pl.getName()).replace("%message", msg));

			if (Settings.AntiAd.WRITE_TO_FILE)
				Writer.zapisatDo(FileType.ADVERTISEMENTS, pl.getName(), msg);

			Common.tell(pl, Localization.ANTIAD_PLAYER_WARN);
		}

		return isAd;
	}

	/**
	 * Return modified message if the check found some swear.
	 */
	public static String swearCheck(Player swearer, String finalMessage, String strippedMsg) {
		boolean isSwear = false;
		String originalMsg = finalMessage;

		for (String regex : Settings.AntiSwear.WORD_LIST) {
			regex = regex.toLowerCase();

			if (Common.regExMatch(regex, strippedMsg)) {
				isSwear = true;
				finalMessage = finalMessage.replaceAll("(?i)" + regex, Common.colorize(Settings.AntiSwear.REPLACEMENT.replace("%player", swearer.getName())));
			} else if (Common.regExMatch(regex, Common.stripDuplicate(strippedMsg))) {
				isSwear = true;
				finalMessage = Common.stripDuplicate(finalMessage);
				finalMessage = finalMessage.replaceAll("(?i)" + regex, Common.colorize(Settings.AntiSwear.REPLACEMENT.replace("%player", swearer.getName())));
			}
		}

		if (isSwear) {
			Common.swearActions(originalMsg, swearer);
			return finalMessage;
		}

		return finalMessage;
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
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
}
