package chatcontrol.Utils;

import java.io.InputStream;
import java.net.URL;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import chatcontrol.ChatControl;

public class UpdaterThread extends BukkitRunnable {

	private String fileUrl;
	private String localNewVer;

	public UpdaterThread(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Override
	public void run() {
		String currVer = ChatControl.plugin.getDescription().getVersion();

		if(currVer.contains("SNAPSHOT") || currVer.contains("DEV"))
			return;

		try {
			InputStream input =  new URL(fileUrl).openConnection().getInputStream();
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(input);
			localNewVer = conf.getString("version");
		} catch (Exception ex){
			Common.error("Cannot fetch latest version of ChatControl", ex);
		}
		
		if(localNewVer.contains("SNAPSHOT") || localNewVer.contains("DEV"))
			return;

		if(cislo(localNewVer) > cislo(currVer)){
			ChatControl.needsUpdate = true;
			ChatControl.newVersion = localNewVer;
			Common.Log("&eA new version of ChatControl is available!");
			Common.Log("&eCurrent version: " + ChatControl.plugin.getDescription().getVersion() + " New version: " + localNewVer);
		}
	}


	int cislo(String s) {
		return Integer.valueOf(s.replace(".", ""));
	}
}
