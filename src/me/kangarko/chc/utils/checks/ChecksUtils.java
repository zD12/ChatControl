package me.kangarko.chc.utils.checks;

import me.kangarko.chc.model.Settings;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class ChecksUtils {

	/**
	 * Advertising check.
	 * @deprecated inconsistend, make it in one class
	 */
	public static boolean advertisingCheck(Player pl, String msg, boolean command) {		
		if (Common.hasPerm(pl, Permissions.Bypasses.ads))
			return false;
		String finalMsg = msg.replaceAll(Settings.AntiAd.PREPROCESS_STRIP, "");

		if(command && !Settings.AntiAd.ENABLED_IN_COMMANDS)
			return false;
		
		for(String ip : Settings.AntiAd.WHITELIST_IP)
			if(msg.contains(ip))
				return false;

		for(String domeny : Settings.AntiAd.WHITELIST_DOMAINS)
			if(msg.toLowerCase().contains(domeny.toLowerCase()))
				return false;

		if (Common.regExMatch(Settings.AntiAd.REGEX_IP, finalMsg) || Common.regExMatch(Settings.AntiAd.REGEX_DOMAINS, finalMsg))
			return true;
		
		if (!Settings.AntiAd.REGEX_CUSTOM.equalsIgnoreCase("none"))
			if(Common.regExMatch(Settings.AntiAd.REGEX_CUSTOM, finalMsg))
				return true;

		return false;
	}	

	public static String swearCheck(Player swearer, String finalMessage, String strippedMsg) {
		boolean isSwear = false;
		String originalMsg = finalMessage;

		for(String regex : Settings.AntiSwear.WORD_LIST) {
			regex = regex.toLowerCase();

			if(Common.regExMatch(regex, strippedMsg)){
				isSwear = true;
				finalMessage = finalMessage.replaceAll("(?i)" + regex, Common.colorize(Settings.AntiSwear.REPLACEMENT.replace("%player", swearer.getName())));
			} else if (Common.regExMatch(regex, Common.stripDuplicate(strippedMsg))) {
				isSwear = true;
				finalMessage = Common.stripDuplicate(finalMessage);
				finalMessage = finalMessage.replaceAll("(?i)" + regex, Common.colorize(Settings.AntiSwear.REPLACEMENT.replace("%player", swearer.getName())));
			}
		}		

		if(isSwear) {
			Common.swearActions(originalMsg, swearer);
			return finalMessage;
		}

		return finalMessage;
	}

	// ------------------------ choose one best TODO
	
	/**
	 * Calculates the similarity (an int within 0 and 100%) between two strings.
	 */
	public static double similarityCheck(String s1, String s2) {
		String longer = s1, shorter = s2; 
		if (s1.length() < s2.length()) // longer should always have greater length
			longer = s2; shorter = s1;

			int longerLength = longer.length();	 
			if (longerLength == 0) 
				return 0; /* both strings are zero length */ 

			return ((longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longerLength);
	}

	public static boolean stringsAreSimilar(String string1, String string2) {
		if (string1 == null || string2 == null)
			return false;
		String str1;
		String str2;

		if (string2.length() < string1.length()) {
			str1 = string2;
			str2 = string1;
		} else {
			str1 = string1;
			str2 = string2;
		}

		int integer = str2.length() - str2.length() / 4;

		System.out.println("integer: " + integer);

		if (str1.length() < integer)
			return false;

		int count = 0;

		for (int lenght = 0; lenght < str1.length(); lenght++) {
			if (str1.charAt(lenght) == str2.charAt(lenght))
				count++;
			if (count > integer)
				return true;
		}
		for (int lenght = 0; lenght < str1.length(); lenght++) {
			if (str1.charAt(str1.length() - lenght - 1) == str2.charAt(str2.length() - lenght - 1))
				count++;
			if (count > integer)
				return true;
		}

		return false;
	}
}
