package chatcontrol;

import java.util.logging.LogRecord;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;


public class ConsoleFilter implements java.util.logging.Filter, org.apache.logging.log4j.core.Filter {

	/*public boolean isLoggable(LogRecord record){
		String m = record.getMessage();		
		for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
			if(m.contains(str)){
				return false;
			}
			if(m.matches(str)){
				return false;
			}
		}
		return true;
	}*/

	@Override
	public boolean isLoggable(LogRecord record) {
		/*try {
			if (record == null || record.getMessage() == null) return true;
			String logM = record.getMessage().toLowerCase();
			if (!logM.contains("issued server command:")) return true;
			if (!logM.contains("/login ") && !logM.contains("/l ") && !logM.contains("/reg ") && !logM.contains("/changepassword ") && !logM.contains("/unregister ")
					&& !logM.contains("/authme register ") && !logM.contains("/authme changepassword ")&& !logM.contains("/authme reg ")&& !logM.contains("/authme cp ") && !logM.contains("/register ")) return true;
			String playername = record.getMessage().split(" ")[0];			
			record.setMessage(playername + " issued an AuthMe command!");
			return true;
		} catch (NullPointerException npe) {
			return true;
		}*/
		return true;
	}

	@Override
	public Result filter(LogEvent record) {
		try {
			String m = record.getMessage().getFormattedMessage();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
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
			String m = message;
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
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
			String m = message.toString();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
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
			String m = message.getFormattedMessage();
			for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
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
