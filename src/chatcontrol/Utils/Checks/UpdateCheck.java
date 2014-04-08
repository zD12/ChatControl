package chatcontrol.Utils.Checks;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;

public class UpdateCheck extends BukkitRunnable {

	private String fileUrl;

	public UpdateCheck(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Override 
	public void run() {
		String staraVer = ChatControl.plugin.getDescription().getVersion();

		if (staraVer.contains("SNAPSHOT") || staraVer.contains("DEV"))
			return;

		String novaVerzia = staraVer;

		try {
			InputStream input = new URL(fileUrl).openConnection().getInputStream();
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(input);
			novaVerzia = conf.getString("version");
		} catch (Exception ex) {
			Common.error("Cannot fetch latest version of ChatControl", ex);
		}
		
		if (novaVerzia.contains("SNAPSHOT") || novaVerzia.contains("DEV"))
			return;

		if (cislo(novaVerzia) > cislo(staraVer)) {
			if (ChatControl.Config.getBoolean("Miscellaneous.Download_Automatically")) {
				try {
					Common.Log("&bChatControl is updating! Downloading v" + novaVerzia);

					URL adresa = new URL("http://www.spigotmc.org/resources/chatcontrol.271/download?version=1745"); // FIXME Update after every release

					Common.Log("Got file of size: " + ((double) adresa.openConnection().getContentLengthLong() / 1000) + " kb");

					FileUtils.copyURLToFile(adresa, new File(Bukkit.getUpdateFolder() + "/ChatControl.jar"));

					Common.Log("Downloaded! File uploaded into the " + Bukkit.getUpdateFolder() + " folder. Please copy it to plugins folder.");
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				ChatControl.needsUpdate = true;
				ChatControl.newVersion = novaVerzia;
				Common.Log("&eA new version of ChatControl is available!");
				Common.Log("&eCurrent version: " + ChatControl.plugin.getDescription().getVersion() + " New version: " + novaVerzia);
			}
		}
	}

	int cislo(String s) {
		return Integer.valueOf(s.replace(".", ""));
	}
}