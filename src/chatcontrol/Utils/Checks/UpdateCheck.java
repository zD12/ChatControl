package chatcontrol.Utils.Checks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;

public class UpdateCheck implements Runnable {

	private String fileUrl;

	public UpdateCheck(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@SuppressWarnings("deprecation")
	@Override 
	public void run() {
		String staraVer = ChatControl.plugin.getDescription().getVersion();

		if (staraVer.contains("SNAPSHOT") || staraVer.contains("DEV"))
			return;

		String novaVerzia = staraVer;
		
		try {
			InputStreamReader input = new InputStreamReader(new URL(fileUrl).openConnection().getInputStream());
			YamlConfiguration conf;
			try {
				conf = YamlConfiguration.loadConfiguration(input);
			} catch (NoSuchMethodError ex) {
				conf = YamlConfiguration.loadConfiguration(new URL(fileUrl).openConnection().getInputStream());
			}
			novaVerzia = conf.getString("version");
		} catch (UnknownHostException | MalformedURLException ex) {
			Common.Warn("ChatControl failed update check, could not connect to: " + fileUrl);
		} catch (IOException ex) {
			Common.error("Could not fetch latest version of ChatControl from: " + fileUrl, ex);
		}

		if (novaVerzia.contains("SNAPSHOT") || novaVerzia.contains("DEV"))
			return;

		if (cislom(novaVerzia) > cislom(staraVer)) {
			if (ChatControl.Config.getBoolean("Miscellaneous.Download_Automatically")) {
				URL adresa = null;
				
				try {
					Common.Log("&bChatControl is updating! Downloading v" + novaVerzia);

					adresa = new URL("https://raw.githubusercontent.com/kangarko/ChatControl/master/precompiled/ChatControl_v" + novaVerzia + ".jar");

					Common.Log("Got file of size: " + ((double) adresa.openConnection().getContentLengthLong() / 1000) + " kb");		
					
					File file = new File(Bukkit.getUpdateFolder(), "ChatControl.jar");
					
					if (!file.exists())
						file.mkdirs();
					
					Files.copy(adresa.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					Common.Log("Downloaded! File uploaded into the " + Bukkit.getUpdateFolder() + " folder. Please copy it to plugins folder.");
				} catch (FileNotFoundException ex) {
					Common.Warn("Cannot download file from " + adresa.toString() + " (Malformed URL / file not uploaded yet)");
				} catch (IOException ex) {
					Common.Warn("Cannot download file from " + adresa.toString() + " (check console for error)");
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

	private int cislom(String s) {
		return Integer.valueOf(s.replace(".", ""));
	}
}