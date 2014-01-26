package chatcontrol.Utils.Checks;

import org.bukkit.entity.Player;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;

public class ChecksUtils {
	
	/**
	 * Advertising check.
	 */
	public static boolean advertisingCheck(Player pl, String msg){
		String finalMsg = msg.replaceAll(ChatControl.Config.getString("Anti_Ad.Filter_Pre_Process"), "");
		if(!ChatControl.Config.getBoolean("Anti_Ad.Enabled_In_Commands")){
			return false;
		}
		for(String ip : ChatControl.Config.getStringList("Anti_Ad.Whitelisted.IP")){
			if(msg.matches(".*" + ip + ".*")){
				return false;
			}
		}
		for(String domeny : ChatControl.Config.getStringList("Anti_Ad.Whitelisted.Domains")){
			if(msg.toLowerCase().matches(".*" + domeny + ".*")){
				return false;
			}
		}
		if (pl.hasPermission(Permissions.Bypasses.ads)){
			return false;
		} else if (finalMsg.matches((".*" + ChatControl.Config.getString("Anti_Ad.IP_Filter")) + ".*") || (finalMsg.matches(".*" + ChatControl.Config.getString("Anti_Ad.Domain_Filter") + ".*"))){
			return true;
		} else if (!ChatControl.Config.getString("Anti_Ad.Custom_Filter").equalsIgnoreCase("none")){
			if(finalMsg.matches(".*"  + ChatControl.Config.getString("Anti_Ad.Custom_Filter") + ".*")){
				return true;
			}
		}
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
