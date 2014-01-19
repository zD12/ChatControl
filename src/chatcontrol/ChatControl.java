package chatcontrol;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Filter;
import java.util.logging.Logger;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import chatcontrol.Listener.ChatListener;
import chatcontrol.Listener.CommandListener;
import chatcontrol.Listener.PlayerListener;
import chatcontrol.Misc.Permissions;
import chatcontrol.PacketListener.PacketListener;
import chatcontrol.Utils.Common;
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
	}

	public void onDisable() {
		plugin = null;
		Config = null;
		data.clear();
		lastLoginTime.clear();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String cL, String[] args){
		if (args.length == 0){
			sender.sendMessage(ChatColor.DARK_AQUA + "ChatControl §8// §f" + "Running §7v" + getDescription().getVersion());
			sender.sendMessage(ChatColor.DARK_AQUA + "ChatControl §8// §f" + "By §7kangarko §f© 2013");    
			sender.sendMessage(ChatColor.DARK_AQUA + "ChatControl §8// §f" + "Website: §7www.ultracraft.6f.sk");  	  
		} else if(args.length == 1){
			if (args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("m")) {
				if(!sender.hasPermission(Permissions.Commands.mute)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				if(muted){
					muted = false;
					Common.broadcastMsg(sender, "Mute.Broadcast","Localization.Broadcast_Unmute");
					Common.sendMsg(sender, "Localization.Successful_Unmute");						
				} else {
					muted = true;
					Common.broadcastMsg(sender, "Mute.Broadcast", "Localization.Broadcast_Mute");
					Common.sendMsg(sender, "Localization.Successful_Mute");
				}
			} else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {
				if(!sender.hasPermission(Permissions.Commands.clear)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				for(Player pl : getServer().getOnlinePlayers()){
					if(getConfig().getBoolean("Clear.Do_Not_Clear_For_Staff") && (pl.isOp() || pl.hasPermission(Permissions.Bypasses.chat_clear))){
						pl.sendMessage(getConfig().getString("Localization.Staff_Chat_Clear_Message").replace("&", "§").replace("%prefix", Common.prefix()).replace("%player", Common.resolvedSender(sender)));
						return false;
					}
					for(int i = 0; i < 130; i++){
						pl.sendMessage("§r       ");
					}
				}
				Common.broadcastMsg(sender, "Clear.Broadcast", "Localization.Broadcast_Clear");
			} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
				if(!sender.hasPermission(Permissions.Commands.reload)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				Bukkit.getPluginManager().disablePlugin(this);
				Bukkit.getPluginManager().enablePlugin(this);
				try {getConfig().load(new File("plugins/ChatControl/config.yml"));} 
				catch (Exception e) {
					Common.Log("&cUnable to reload configuration. Error:" + e);
					Common.sendMsg(sender, "Localization.Reload_Failed");	
				}
				Common.sendMsg(sender, "Localization.Reload_Complete");
			} else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
				if(!sender.hasPermission(Permissions.Commands.help)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				chatControlHelp(sender);
			} else {
				if(!sender.hasPermission(Permissions.Commands.global_perm)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				Common.sendMsg(sender, "Localization.Wrong_Args");
			}
		} else if(args.length >= 2){
			String dovod = "";
			for (int msg = 1; msg < args.length; msg++) {
				dovod = dovod + " " + args[msg];
			}
			if (args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("m")) {
				if(!sender.hasPermission(Permissions.Commands.mute)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				if(muted){
					muted = false;
					Common.broadcastMsg(sender, "Mute.Broadcast", "Localization.Broadcast_Unmute");
					Common.sendMsg(sender, "Localization.Successful_Unmute");						
				} else {
					muted = true;
					if(getConfig().getBoolean("Mute.Broadcast")){
						getServer().broadcastMessage(getConfig().getString("Localization.Broadcast_Mute").replace("&", "§").replace("%prefix", getConfig().getString("Localization.Prefix").replace("&", "§")).replace("%player", sender.getName()) + " " + getConfig().getString("Localization.Reason").replace("&", "§").replace("%reason", dovod.replace("&", "§")));
					}
					Common.sendMsg(sender, "Localization.Successful_Mute");
				}
			} else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {
				if(!sender.hasPermission(Permissions.Commands.clear)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				if(args[1].equalsIgnoreCase("console")){
					for(int i = 0; i < getConfig().getInt("Clear.Amount_Of_Lines_To_Clear_In_Console", 300); i++){
						Common.Log("§r           ");
					}
					Common.sendMsg(sender, "Localization.Successful_Console_Clear");
				} else {
					for(Player pl : getServer().getOnlinePlayers()){
						if(getConfig().getBoolean("Clear.Do_Not_Clear_For_Staff") && (pl.isOp() || pl.hasPermission(Permissions.Bypasses.chat_clear))){
							pl.sendMessage(getConfig().getString("Localization.Staff_Chat_Clear_Message").replace("&", "§").replace("%prefix", Common.prefix()).replace("%player", Common.resolvedSender(sender)));
							return false;
						}
						for(int i = 0; i < 120; i++){
							pl.sendMessage("§r       ");
						}
					}
					if(getConfig().getBoolean("Clear.Broadcast")){
						getServer().broadcastMessage(getConfig().getString("Localization.Broadcast_Clear").replace("&", "§").replace("%prefix", getConfig().getString("Localization.Prefix").replace("&", "§")).replace("%player", sender.getName()) + " " + getConfig().getString("Localization.Reason").replace("&", "§").replace("%reason", dovod.replace("&", "§")));
					}
				}
			} else {
				if(!sender.hasPermission(Permissions.Commands.global_perm)){
					Common.sendMsg(sender, "Localization.No_Permission");
					return false;
				}
				Common.sendMsg(sender, "Localization.Wrong_Args");
			}
		}

		return true;
	}

	public void chatControlHelp(CommandSender pl){
		pl.sendMessage("§8== §6ChatControl commands §8==");
		pl.sendMessage("§3 - §f/chatcontrol mute §8[§7Mute the chat§8]");
		pl.sendMessage("§3 - §f/chatcontrol clear §8[§7Clear the chat§8]");
		pl.sendMessage("§3 - §f/chatcontrol reload §8[§7Reload the plugin§8]");
		pl.sendMessage("§3 - §f/chatcontrol help §8[§7List all the commands§8]");
	}
}
