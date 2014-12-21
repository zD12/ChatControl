package me.kangarko.chc.filter;

import me.kangarko.chc.model.SettingsConsole;
import me.kangarko.chc.utils.Common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class Log4jFilter implements Filter {

	public void init() {
		((Logger) LogManager.getRootLogger()).addFilter(this);
	}

	@Override
	public Result filter(LogEvent record) {
		return checkMessage(record.getMessage().getFormattedMessage());
	}

	@Override
	public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
		return checkMessage(message);
	}

	@Override
	public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
		return checkMessage(message.toString());
	}

	@Override
	public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
		return checkMessage(message.getFormattedMessage());
	}

	@Override
	public Result getOnMatch() {
		return Result.NEUTRAL;
	}

	@Override
	public Result getOnMismatch() {
		return Result.NEUTRAL;
	}

	private Result checkMessage(String message) {		
		for (String filter : SettingsConsole.FILTER_MESSAGES) {
			if (filter.equalsIgnoreCase(message))
				return Result.DENY;
			else if (filter.toLowerCase().contains(message.toLowerCase()))
				return Result.DENY;
			else if (Common.regExMatch(filter, message))
				return Result.DENY;
		}
		return Result.NEUTRAL;
	}
}
