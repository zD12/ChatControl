package chatcontrol.ConsoleFilter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;

public class ConsoleFilter implements Filter {
	
	public boolean isLoggable(LogRecord record){
		String msg = record.getMessage();

		if(ChatControl.ConsoleConfig.getConfig().getBoolean("Console.Filter_Enabled")) {
			for(String str : ChatControl.ConsoleConfig.getConfig().getConfigurationSection("Console.Replace_Messages").getKeys(false)) {
				msg = msg.replaceAll("(?i)" + str.replace("<DOT>", "\\."), ChatControl.ConsoleConfig.getConfig().getString("Console.Replace_Messages." + str));
			}
		}

		if(ChatControl.ConsoleConfig.getConfig().getBoolean("Console.Filter_Enabled") || ChatControl.ConsoleConfig.getConfig().getBoolean("Console.Correct_Color_Codes")) {
			record.setMessage(Common.toAnsiColors(msg));
		}

		for (String blackListedMsg : ChatControl.ConsoleConfig.getConfig().getStringList("Console.Filter_Messages")) {
			if(msg.equalsIgnoreCase(blackListedMsg))
				return false;
			
			if(msg.contains(blackListedMsg))
				return false;
			
			if(Common.regExMatch(blackListedMsg, msg))
				return false;
			
		}

		return true;
	}
}