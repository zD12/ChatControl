package kangarko.chatcontrol.rules;

import java.io.File;
import java.util.List;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;

import org.bukkit.configuration.file.YamlConfiguration;

public class Handlers {

	private final static File file;

	private static YamlConfiguration cfg;
	private static String sectionName;

	static {
		file = new File(ChatControl.instance().getDataFolder(), "handlers.yml");

		if (!file.exists()) {
			ChatControl.instance().saveResource("handlers.yml", false);
			Common.Log("&fCreated default handlers file: " + file.getName());
		}
	}

	public static Handler loadHandler(String name, String ruleID) {
		cfg = YamlConfiguration.loadConfiguration(file);

		if (!cfg.isConfigurationSection(name))
			throw new NullPointerException("Unknown handler: " + name);

		sectionName = cfg.getConfigurationSection(name).getName();

		Handler handler = new Handler(sectionName, ruleID);
		String message;

		message = getString("Bypass_With_Permission");
		if (isValid(message))
			handler.setBypassPermission(message);

		message = getString("Player_Warn_Message");
		if (isValid(message))
			handler.setPlayerWarnMsg(message);

		message = getString("Broadcast_Message");
		if (isValid(message))
			handler.setBroadcastMsg(message);

		message = getString("Staff_Alert_Message");
		if (isValid(message))
			handler.setStaffAlertMsg(message);

		message = getString("Staff_Alert_Permission");
		if (isValid(message))
			handler.setStaffAlertPermission(message);

		message = getString("Console_Message");
		if (isValid(message))
			handler.setConsoleMsg(message);

		message = getString("Write_To_File");
		if (isValid(message))
			handler.setWriteToFileName(message);

		Boolean block = cfg.getBoolean(sectionName + ".Block_Message");
		if (block != null && block)
			handler.setBlockMessage();

		message = getString("Replace_Word");
		if (isValid(message))
			handler.setMsgReplacement(message);

		message = getString("Replace_Whole");
		if (isValid(message))
			handler.setRewriteTo(message);

		List<String> list;
		if (cfg.isSet(sectionName + ".Execute_Commands")) {
			list = cfg.getStringList(sectionName + ".Execute_Commands");
			handler.setCommandsToExecute(list);
		}

		if (cfg.isSet(sectionName + ".Ignored_In_Commands")) {
			list = cfg.getStringList(sectionName + ".Ignored_In_Commands");
			handler.setIgnoredInCommands(list);
		}

		sectionName = null;

		return handler;
	}

	public static boolean isValid(String msg) {
		return msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("none");
	}

	public static String getString(String path) {
		String msg = cfg.getString(sectionName + "." + path);

		return msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("none") ? msg : null;
	}
}
