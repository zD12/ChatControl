package me.kangarko.chc.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import me.kangarko.chc.ChatControl;

import org.bukkit.Bukkit;

public class Writer {

	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	// Some hostings automatically clean .log files.
	public static enum FileType {
		ADVERTISEMENTS("advertisements.txt"),
		CHAT_LOG("chat.txt"),
		ERROR_LOG("errors.txt");

		public String fileName;
		FileType (String fileName) {
			this.fileName = fileName;
		}
	}

	/** @param prefix moze byt aj null */
	public static void zapisatDo(FileType typSuboru, String prefix, String msg) {
		File file;

		if (typSuboru == FileType.ERROR_LOG)
			file = ChatControl.instance().getDataFolder();
		else
			file = new File(ChatControl.instance().getDataFolder() + "/logs");

		if (!file.exists())
			file.mkdir();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file + "/" + typSuboru.fileName, true))) {
			for (String line : msg.split("\n")) {
				bw.write("[" + getTime() + "] " + (prefix != null ? prefix + ": " : "") + line);
				bw.newLine();
			}
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "ChatControl was unable to write to " + typSuboru.fileName, ex);
		}
	}

	public static String getTime() {
		return dateFormat.format(System.currentTimeMillis());
	}

}
