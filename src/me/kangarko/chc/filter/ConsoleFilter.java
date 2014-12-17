package me.kangarko.chc.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import me.kangarko.chc.model.SettingsConsole;
import me.kangarko.chc.utils.Common;

@Deprecated // TODO
public class ConsoleFilter implements Filter {

	public boolean isLoggable(LogRecord record) {
		String msg = record.getMessage();

		/*if (SettingsConsole.filterEnabled) TODO get replace back
			for(String str : ChatControl.ConsoleConfig.getConfig().getConfigurationSection("Console.Replace_Messages").getKeys(false))
				msg = msg.replaceAll("(?i)" + str.replace("<DOT>", "\\."), ChatControl.ConsoleConfig.getConfig().getString("Console.Replace_Messages." + str));*/

		if (SettingsConsole.filterEnabled && SettingsConsole.filterColorToAnsi)
			record.setMessage(Common.toAnsiColors(msg));

		for (String blacklist : SettingsConsole.filterMessageList) {
			if (msg.equalsIgnoreCase(blacklist))
				return false;
			else if (blacklist.contains(msg))
				return false;
			else if (Common.regExMatch(blacklist, msg))
				return false;
		}

		return true;
	}
}