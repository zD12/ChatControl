package chatcontrol.ConsoleFilter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import chatcontrol.ChatControl;

public class ConsoleFilter implements Filter {

	public boolean isLoggable(LogRecord record){
		String m = record.getMessage().toLowerCase();
		for (String str : ChatControl.Config.getStringList("Console.Filter_Messages")){
			
			str = str.toLowerCase();
			
			if(m.matches(str) ){
				return false;
			}
			if(m.contains(str) ){
				return false;
			}
			if(m.startsWith(str) ){
				return false;
			}
			if(m.equalsIgnoreCase(str) ) {
				return false;
			}
			if(m.endsWith(str) ){
				return false;
			}
		}
		return true;
	}

}
