package chatcontrol.Utils;

import org.bukkit.command.CommandSender;


public class InsufficientPermissionEx extends Error {

	private static final long serialVersionUID = 1L;

	public InsufficientPermissionEx(CommandSender sender) {
		Common.sendMsg(sender, "Localization.No_Permission");
	}
	
}
