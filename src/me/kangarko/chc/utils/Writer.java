package me.kangarko.chc.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.kangarko.chc.ChatControl;

public class Writer {

	public static enum TypSuboru {
		REKLAMY("advertisements.txt"), // Some of the hostings automatically cleans .log files.
		ZAZNAM_CHATU("chat.txt"),
		ZAZNAM_CHYB("errors.txt");

		public String nazovSuboru;
		TypSuboru (String nazovSuboru) {
			this.nazovSuboru = nazovSuboru;
		}
	}

	/** @param prefix moze byt aj null */
	public static void zapisatDo(TypSuboru typSuboru, String prefix, String msg) {
		BufferedWriter bw = null;
		File file;
		if (typSuboru == TypSuboru.ZAZNAM_CHYB)
			file = ChatControl.instance().getDataFolder();
		else
			file = new File(ChatControl.instance().getDataFolder() + "/logs");

		if(!file.exists())
			file.mkdir();

		try {
			try {
				bw = new BufferedWriter(new FileWriter(file + "/" + typSuboru.nazovSuboru, true));					
				for (String line : msg.split("\n")) {
					bw.write("[" + getTime() + "] " + (prefix != null ? prefix + ": " : "") + line);
					bw.newLine();
				}
			} finally {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			}
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.WARNING, "ChatControl was unable to write to " + typSuboru.nazovSuboru, ex);
		}
	}

	public static String getTime() {
		DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return date.format(System.currentTimeMillis());
	}

}
