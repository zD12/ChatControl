package kangarko.chatcontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.Settings;

public class Writer {

	public static final String ERROR_FILE_PATH = "errors/errors.txt";
	public static final String CHAT_FILE_PATH = "logs/chat.txt";

	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public static void writeToFile(String filePath, String prefix, String msg) {
		int lastIndex = filePath.lastIndexOf('/');
		File dir = new File(ChatControl.instance().getDataFolder(), filePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(ChatControl.instance().getDataFolder(), filePath);

		if (Settings.Writer.STRIP_COLORS)
			msg = msg.replaceAll("(" + org.bukkit.ChatColor.COLOR_CHAR + "|&)([0-9a-fk-or])", "");

		try (FileWriter bw = new FileWriter(file, true)) {
			for (String line : msg.trim().split("\n"))
				bw.write("[" + getTime() + "] " + (prefix != null ? prefix + ": " : "") + line + System.lineSeparator());
		} catch (Exception ex) {
			ex.printStackTrace();
			Common.LogInFrame(false, "Error writing to: " + filePath, "Error: " + ex.getMessage());
		}
	}

	public static String getTime() {
		return dateFormat.format(System.currentTimeMillis());
	}

}
