package kangarko.chatcontrol.filter;

import java.util.Map.Entry;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import kangarko.chatcontrol.model.SettingsConsole;
import kangarko.chatcontrol.utils.Common;

public class ConsoleFilter implements Filter {

	@Override
	public boolean isLoggable(LogRecord record) {
		String msg = record.getMessage();

		if (SettingsConsole.FILTER_ENABLED) {
			for (Entry<String, String> entry : SettingsConsole.FILTER_REPLACE_MAP.entrySet())
				msg = msg.replace(entry.getKey().replace("<dot>", "\\."), entry.getValue());
		}

		if (SettingsConsole.FILTER_ENABLED && SettingsConsole.FILTER_COLORS_TO_ANSI)
			record.setMessage(Common.toAnsiColors(msg));

		for (String blacklist : SettingsConsole.FILTER_MESSAGES) {
			if (msg.equalsIgnoreCase(blacklist) || blacklist.toLowerCase().contains(msg.toLowerCase()))
				return false;
			else if (Common.regExMatch(blacklist, msg))
				return false;
		}

		return true;
	}
}