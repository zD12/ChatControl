package kangarko.chatcontrol.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import kangarko.chatcontrol.ChatControl;

import org.bukkit.Bukkit;

public class Writer {

	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	// Some hostings automatically clean .log files.
	public static enum FileType {
		CHAT_LOG("chat.txt"),
		ERROR_LOG("errors.txt");

		public String fileName;
		FileType (String fileName) {
			this.fileName = fileName;
		}
	}

	/** @param prefix moze byt aj null
	 * @deprecated dont work
	 */
	public static void zapisatDo(FileType typSuboru, String prefix, String msg) {
		File file;

		if (typSuboru == FileType.ERROR_LOG)
			file = ChatControl.instance().getDataFolder();
		else
			file = new File(ChatControl.instance().getDataFolder() + "/logs");

		if (!file.exists()) {
			file.mkdir();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		msg = msg.trim().replaceAll("(" + org.bukkit.ChatColor.COLOR_CHAR + "|&)([0-9a-fk-or])", ""); // TODO Toggleable.

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			for (String line : msg.split("\n")) {
				bw.write("[" + getTime() + "] " + (prefix != null ? prefix + ": " : "") + line);
				bw.newLine();
			}
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "ChatControl was unable to write to " + typSuboru.fileName, ex);
		}
	}

	/**
	 * @deprecated dont work
	 */
	public static void zapisatDo(String filePath, String prefix, String msg) {
		File file = new File(ChatControl.instance().getDataFolder(), filePath);

		if (!file.exists()) {
			file.mkdir();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		msg = msg.trim().replaceAll("(" + org.bukkit.ChatColor.COLOR_CHAR + "|&)([0-9a-fk-or])", ""); // TODO Toggleable.

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			for (String line : msg.split("\n")) {
				bw.write("[" + getTime() + "] " + (prefix != null ? prefix + ": " : "") + line);
				bw.newLine();
			}
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "ChatControl was unable to write to " + filePath, ex);
		}
	}

	public static String getTime() {
		return dateFormat.format(System.currentTimeMillis());
	}

}
