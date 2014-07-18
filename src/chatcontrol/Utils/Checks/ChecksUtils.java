package chatcontrol.Utils.Checks;

import org.bukkit.entity.Player;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;

public class ChecksUtils {
	
	/**
	 * Advertising check.
	 */
	public static boolean advertisingCheck(Player pl, String msg, boolean command){
		if (pl.hasPermission(Permissions.Bypasses.ads))
			return false;
		
		String finalMsg = msg.replaceAll(ChatControl.Config.getString("Anti_Ad.Filter_Pre_Process"), "");

		if(command && !ChatControl.Config.getBoolean("Anti_Ad.Enabled_In_Commands"))
			return false;
		
		for(String ip : ChatControl.Config.getStringList("Anti_Ad.Whitelisted.IP"))
			if(msg.matches(".*" + ip + ".*"))
				return false;

		for(String domeny : ChatControl.Config.getStringList("Anti_Ad.Whitelisted.Domains"))
			if(msg.toLowerCase().matches(".*" + domeny.toLowerCase() + ".*"))
				return false;
		
		/*if (Common.debugEnabled()) {
			Common.debug("Legacy Matches IP Filter? " + finalMsg.matches((".*" + ChatControl.Config.getString("Anti_Ad.IP_Filter")) + ".*"));
			Common.debug("Legacy Matches Domain Filter? " + finalMsg.matches(".*" + ChatControl.Config.getString("Anti_Ad.Domain_Filter") + ".*"));
			Common.debug("Matches IP Filter? " + Common.regExMatch(ChatControl.Config.getString("Anti_Ad.IP_Filter"), finalMsg));
			Common.debug("Matches Domain Filter? " + Common.regExMatch(ChatControl.Config.getString("Anti_Ad.Domain_Filter"), finalMsg));
		}*/
		
		if (Common.regExMatch(ChatControl.Config.getString("Anti_Ad.IP_Filter"), finalMsg) || Common.regExMatch(ChatControl.Config.getString("Anti_Ad.Domain_Filter"), finalMsg))
			return true;
		
		if (!ChatControl.Config.getString("Anti_Ad.Custom_Filter").equalsIgnoreCase("none"))
			if(Common.regExMatch(ChatControl.Config.getString("Anti_Ad.Custom_Filter"), finalMsg))
				return true;
		
		return false;
	}	
	
	public static String swearCheck(Player swearer, String finalMessage, String strippedMsg) {
		boolean isSwear = false;
		String originalMsg = finalMessage;
		
		for(String regex : ChatControl.Config.getStringList("Anti_Swear.Word_List")) {
			regex = regex.toLowerCase();

			if(Common.regExMatch(regex, strippedMsg)){
				isSwear = true;
				finalMessage = finalMessage.replaceAll("(?i)" + regex, Common.colorize(ChatControl.Config.getString("Anti_Swear.Replacement").replace("%player", swearer.getName())));
			} else if (Common.regExMatch(regex, Common.stripDuplicate(strippedMsg))) {
				isSwear = true;
				finalMessage = Common.stripDuplicate(finalMessage);
				finalMessage = finalMessage.replaceAll("(?i)" + regex, Common.colorize(ChatControl.Config.getString("Anti_Swear.Replacement").replace("%player", swearer.getName())));
			}
		}		
		
		if(isSwear) {
			Common.swearActions(originalMsg, swearer);
			return finalMessage;
		}
		
		return finalMessage;
	}
}
