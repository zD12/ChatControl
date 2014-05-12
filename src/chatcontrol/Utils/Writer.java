package chatcontrol.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import chatcontrol.ChatControl;

public class Writer {

	public static enum TypSuboru {
		REKLAMY("advertisements.log"),
		ZAZNAM_CHATU("chat.log");

		public String typ;
		TypSuboru (String typ_suboru) {
			typ = typ_suboru;
		}
	}

	/**
	 * @param prefix moze byt aj null
	 */
	public static void writeToFile(TypSuboru typSuboru, String prefix, String msg) {
		BufferedWriter bw = null;
		File file = new File(ChatControl.plugin.getDataFolder() + "/logs");

		if(!file.exists())
			file.mkdir();

		try {
			try {
				bw = new BufferedWriter(new FileWriter(file + "/" + typSuboru.typ, true));
				bw.write("[" + getTime() + "] " + (prefix != null ? prefix + ": " : "") + msg);
				bw.newLine();
			} finally {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			}
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.WARNING, "ChatControl was unable to write to " + typSuboru.typ, ex);
		}
	}

	public static String getTime() {
		DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return date.format(System.currentTimeMillis());
	}

}
