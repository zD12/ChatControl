package kangarko.chatcontrol.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import kangarko.chatcontrol.model.SettingsConsole;
import kangarko.chatcontrol.utils.Common;

public class ConsoleFilter implements Filter {

	public boolean isLoggable(LogRecord record) {
		String msg = record.getMessage();

		if (SettingsConsole.FILTER_ENABLED)
			for (String str : SettingsConsole.FILTER_REPLACE_MAP.keySet())
				msg = msg.replaceAll(".*" + str.replace("<dot>", "\\.") + ".*", SettingsConsole.FILTER_REPLACE_MAP.get(str));

		if (SettingsConsole.FILTER_ENABLED && SettingsConsole.FILTER_COLORS_TO_ANSI)
			record.setMessage(Common.toAnsiColors(msg));

		for (String blacklist : SettingsConsole.FILTER_MESSAGES) {
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