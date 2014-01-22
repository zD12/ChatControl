package chatcontrol;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Filter;
import java.util.logging.Logger;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import chatcontrol.Listener.ChatListener;
import chatcontrol.Listener.CommandListener;
import chatcontrol.Listener.PlayerListener;
import chatcontrol.PacketListener.PacketListener;
import chatcontrol.Utils.ConfigUpdater;

public class ChatControl extends JavaPlugin implements Listener {

	public static FileConfiguration Config;
	public static ChatControl plugin;

	public static HashMap<InetAddress, Long> lastLoginTime = new HashMap<InetAddress, Long>();
	public static HashMap<Player, Storage> data = new HashMap<Player, Storage>();

	public static boolean muted = false;

	public void onEnable(){
		plugin = this;
		Config = getConfig();
		
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		ConfigUpdater.configCheck();

		if(getConfig().getBoolean("Console.Filter_Enabled")){
			Filter filter = new ConsoleFilter();
			if(getConfig().getBoolean("Console.Filter_Plugin_Messages")){
				for (Plugin p : getServer().getPluginManager().getPlugins()) {
					p.getLogger().setFilter(filter);
				}
			}			
            getLogger().setFilter(filter);
            Bukkit.getLogger().setFilter(filter);
            Logger.getLogger("Minecraft").setFilter(filter);

            org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new ConsoleFilter());
		}

		if(getConfig().getBoolean("Protect.Prevent_Tab_Complete")){
			if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
				getLogger().warning("In order to prevent tab complete you need to have ProtocolLib installed. Disabling fuction ...");
				getConfig().set("Protect.Prevent_Tab_Complete", false);
				saveConfig();
				return;
			}
			if(getServer().getBukkitVersion().startsWith("1.7")) {
				new PacketListener().initPacketListener();
			} else {
				new PacketListener().initOlderPacketListener();
				getLogger().info("Detected Minecraft older than 1.7.2, using older packet listener.");
			}		
			getLogger().info("Successfully hooked with ProtocolLib!");
		}

		for (Player pl : getServer().getOnlinePlayers()){
			if(!data.containsKey(pl)){
				data.put(pl, new Storage());
			}
		}
		
		getCommand("chatcontrol").setExecutor(new CommandsHandler());
	}

	public void onDisable() {
		data.clear();
		lastLoginTime.clear();
	}

}
