package chatcontrol.ConsoleFilter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

import chatcontrol.ChatControl;

public class ConsoleFilter implements Filter {

	public boolean isLoggable(LogRecord record){		
		String msg = record.getMessage();
		String higherMsg = record.getMessage().toLowerCase();
		
		for(String str : ChatControl.ConsoleConfig.getConfig().getConfigurationSection("Console.Replace_Messages").getKeys(false)) {
			msg = msg.replaceAll("(?i)" + str.replace("<DOT>", "\\."), ChatControl.ConsoleConfig.getConfig().getString("Console.Replace_Messages." + str));
		}
		record.setMessage(color(msg));
		
		for (String str : ChatControl.ConsoleConfig.getConfig().getStringList("Console.Filter_Messages")){			
			str = str.toLowerCase();
			if(higherMsg.matches(str) || higherMsg.equalsIgnoreCase(str) || higherMsg.contains(str) || higherMsg.startsWith(str) || higherMsg.endsWith(str)){
				return false;
			}
		}
		
		return true;
	}
	
	private String color(String str) {		
        str = str.replace("&0", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        str = str.replace("&1", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        str = str.replace("&2", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        str = str.replace("&3", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
        str = str.replace("&4", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        str = str.replace("&5", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        str = str.replace("&6", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        str = str.replace("&7", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        str = str.replace("&8", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        str = str.replace("&9", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        str = str.replace("&a", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        str = str.replace("&b", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        str = str.replace("&c", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        str = str.replace("&d", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        str = str.replace("&e", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        str = str.replace("&e", Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        str = str.replace("&k", Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
        str = str.replace("&l", Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString());
        str = str.replace("&m", Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        str = str.replace("&n", Ansi.ansi().a(Attribute.UNDERLINE).toString());
        str = str.replace("&o", Ansi.ansi().a(Attribute.ITALIC).toString());
        str = str.replace("&r", Ansi.ansi().a(Attribute.RESET).toString());
		return str;
	}
}
