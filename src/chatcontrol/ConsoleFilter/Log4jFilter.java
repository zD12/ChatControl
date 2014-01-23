package chatcontrol.ConsoleFilter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import chatcontrol.ChatControl;

public class Log4jFilter implements Filter {

	public void init() {
		((Logger) LogManager.getRootLogger()).addFilter(this);
	}

	@Override
	public Result filter(LogEvent record) {
		try {
			String m = record.getMessage().getFormattedMessage().toLowerCase();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
				str = str.toLowerCase();
				if(m.contains(str)){
					return Result.DENY;
				}
				if(m.matches(str)){
					return Result.DENY;
				}
			}
			return Result.NEUTRAL;
		} catch (NullPointerException npe) {
			return Result.NEUTRAL;
		}
	}

	@Override
	public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {		
		try {
			String m = message.toLowerCase();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
				str = str.toLowerCase();
				if(m.contains(str)){
					return Result.DENY;
				}
				if(m.matches(str)){
					return Result.DENY;
				}
			}
			return Result.NEUTRAL;
		} catch (NullPointerException npe) {
			return Result.NEUTRAL;
		}
	}

	@Override
	public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {

		try {
			String m = message.toString().toLowerCase();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
				str = str.toLowerCase();
				if(m.contains(str)){
					return Result.DENY;
				}
				if(m.matches(str)){
					return Result.DENY;
				}
			}
			return Result.NEUTRAL;
		} catch (NullPointerException npe) {
			return Result.NEUTRAL;
		}
	}

	@Override
	public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {

		try {
			String m = message.getFormattedMessage().toLowerCase();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
				str = str.toLowerCase();
				if(m.contains(str)){
					return Result.DENY;
				}
				if(m.matches(str)){
					return Result.DENY;
				}
			}
			return Result.NEUTRAL;
		} catch (NullPointerException npe) {
			return Result.NEUTRAL;
		}
	}

	@Override
	public Result getOnMatch() {
		return Result.NEUTRAL;
	}

	@Override
	public Result getOnMismatch() {
		return Result.NEUTRAL;
	}
}
