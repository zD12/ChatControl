package chatcontrol;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Filter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import chatcontrol.Config.CustomConfig;
import chatcontrol.Config.FileType;
import chatcontrol.ConsoleFilter.ConsoleFilter;
import chatcontrol.ConsoleFilter.Log4jFilter;
import chatcontrol.Listener.ChatListener;
import chatcontrol.Listener.CommandListener;
import chatcontrol.Listener.PlayerListener;
import chatcontrol.PacketListener.PacketListener;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.ConfigUpdater;

public class ChatControl extends JavaPlugin implements Listener {

	public static ChatControl plugin;

	public static FileConfiguration Config;

	public static CustomConfig ChatConfig = new CustomConfig(FileType.CHAT);
	public static CustomConfig ConsoleConfig = new CustomConfig(FileType.CONSOLE);

	public static HashMap<InetAddress, Long> lastLoginTime = new HashMap<InetAddress, Long>();
	public static HashMap<Player, Storage> data = new HashMap<Player, Storage>();

	public static boolean muted = false;

	public void onEnable(){
		plugin = this;
		Config = getConfig();

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		ChatConfig.saveDefaultConfig();
		ConsoleConfig.saveDefaultConfig();

		ConfigUpdater.configCheck();

		for (Player pl : getServer().getOnlinePlayers()){
			if(!data.containsKey(pl)){
				data.put(pl, new Storage());
			}
		}

		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);


		if(ConsoleConfig.getConfig().getBoolean("Console.Filter_Enabled") || ConsoleConfig.getConfig().getBoolean("Console.Correct_Color_Codes")) {
			if(getServer().getBukkitVersion().startsWith("1.7")) {
				new Log4jFilter().init();
				Common.debug("Console filtering now using Log4j Filter.");
			} else {
				Filter filter = new ConsoleFilter();
				if(ConsoleConfig.getConfig().getBoolean("Console.Filter_Plugin_Messages")){
					for (Plugin p : getServer().getPluginManager().getPlugins()) {
						p.getLogger().setFilter(filter);
					}
				}
				Bukkit.getLogger().setFilter(filter);
				Common.debug("Console filtering initiated (MC 1.6.4 and lower).");
			}
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
			}
			Common.Log("Successfully hooked with ProtocolLib!");
		}

		getCommand("chatcontrol").setExecutor(new CommandsHandler());
	}

	public void onDisable() {
		data.clear();
		lastLoginTime.clear();
	}

}
