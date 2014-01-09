package chatcontrol;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ConsoleFilter implements Filter {
	
	public boolean isLoggable(LogRecord record){
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
	}
}
