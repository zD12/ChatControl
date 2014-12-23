package kangarko.chatcontrol;

import org.bukkit.Location;

public class PlayerCache {
	
	public String lastMessage = "";
	public String lastCommand = "";
	
	public long lastMessageTime = 0;
	public long lastCommandTime = 0;
	
	public Location loginLocation = null;
}
