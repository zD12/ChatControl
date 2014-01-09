package chatcontrol;

import org.bukkit.Location;

public class Storage {
	public String lastMessage = "";
	public String lastCommand = "";
	public Long lastMessageTime = (long) 0;
	public Long lastCommandTime = (long) 0;
	public Location loginLocation = null;
	public String lastSignText = "";
}
