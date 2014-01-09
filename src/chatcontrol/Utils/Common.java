package chatcontrol.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import chatcontrol.ChatControl;

public class Common {

	public static void sendMsg(CommandSender pl, String str){
		try {
			pl.sendMessage(ChatControl.Config.getString(str).replace("&", "§").replace("%prefix", prefix()).replace("%player", resolvedSender(pl)));
		} catch (Exception ex){
			pl.sendMessage("§e<Missing language key: §6\"" + str + "\"§e>");
		}
	}

	public static void sendMsg(Player pl, String str){
		try {
			pl.sendMessage(ChatControl.Config.getString(str).replace("&", "§").replace("%prefix", prefix()).replace("%player", pl.getName()));
		} catch (Exception ex){
			pl.sendMessage("§e<Missing language key: §6\"" + str + "\"§e>");
		}
	}

	public static String prefix(){
		String prefix;
		try {
			prefix = ChatControl.Config.getString("Localization.Prefix").replace("&", "§");
		} catch (Exception ex){
			prefix = "§c[§6ChatControl§c]§7 ";
		}
		return prefix;
	}

	public static String console(){
		String prefix;
		try {
			prefix = ChatControl.Config.getString("Localization.Console").replace("&", "§");
		} catch (Exception ex){
			prefix = "§4server";
		}
		return prefix;
	}

	public static void broadcastMsg(CommandSender pl, String cfg, String str){
		try{
			if(!ChatControl.Config.getBoolean(cfg)){
				return;
			}
		} catch (NullPointerException ex){
			ChatControl.Config.set(cfg, false);
			Bukkit.broadcastMessage("§e<Missing config key: §6\"" + cfg + "\"§e>");
			return;
		}
		try{
			String prefix = ChatControl.Config.getString("Localization.Prefix").replace("&", "§");
			Bukkit.broadcastMessage(ChatControl.Config.getString(str).replace("&", "§").replace("%prefix", prefix).replace("%player", resolvedSender(pl)));
		} catch (NullPointerException ex){
			Bukkit.broadcastMessage("§e<Missing language key: §6\"" + str + "\"§e>");
		}
	}

	public static boolean msgIsAd(Player pl, String msg){
		String finalMsg = msg.replaceAll(ChatControl.Config.getString("Anti_Ad.Filter_Pre_Process"), "");
		if(!ChatControl.Config.getBoolean("Anti_Ad.Enabled_In_Commands")){
			return false;
		}
		for(String ip : ChatControl.Config.getStringList("Anti_Ad.Whitelisted.IP")){
			if(msg.matches(".*" + ip + ".*")){
				return false;
			}
		}  
		for(String domeny : ChatControl.Config.getStringList("Anti_Ad.Whitelisted.Domains")){
			if(msg.toLowerCase().matches(".*" + domeny + ".*")){
				return false;
			}
		}
		if (pl.hasPermission("chatcontrol.bypass.ad")){
			return false;
		} else if (finalMsg.matches((".*" + ChatControl.Config.getString("Anti_Ad.IP_Filter")) + ".*") || (finalMsg.matches(".*" + ChatControl.Config.getString("Anti_Ad.Domain_Filter") + ".*"))){
			return true;
		} else if (!ChatControl.Config.getString("Anti_Ad.Custom_Filter").equalsIgnoreCase("none")){
			if(finalMsg.matches(".*"  + ChatControl.Config.getString("Anti_Ad.Custom_Filter") + ".*")){
				return true;
			}
		}
		return false;		
	}

	public static void messages(Player pl, String msg){
		if(ChatControl.Config.getBoolean("Anti_Ad.Inform_Admins")){
			for(Player hrac : Bukkit.getOnlinePlayers()){
				if(hrac.isOp() || hrac.hasPermission("chatcontrol.notify.ad")){
					hrac.sendMessage(ChatControl.Config.getString("Localization.Ad_Staff_Message").replace("&", "§").replace("%player", pl.getName()).replace("%message", msg));
				}
			}
		}
		sendMsg(pl, "Localization.Ad_Message");
		if(ChatControl.Config.getBoolean("Anti_Ad.Broadcast")){
			for(Player hrac : Bukkit.getOnlinePlayers()){
				if(!hrac.isOp() && !hrac.getName().equals(pl.getName())){
					hrac.sendMessage(ChatControl.Config.getString("Localization.Ad_Broadcast_Message").replace("&", "§").replace("%player", pl.getName()).replace("%message", msg));
				}
			}
		}
		if(ChatControl.Config.getBoolean("Anti_Ad.Console_Message")){
			System.out.println(ChatControl.Config.getString("Localization.Ad_Console_Message").replace("&", "§").replace("%player", pl.getName()).replace("%message", msg));
		}		
		if(ChatControl.Config.getBoolean("Anti_Ad.Write_To_File")){
			chatcontrol.Utils.Writer.writeAd(pl, msg);
		}
	}

	public static boolean hasPermission(Player pl, String str){
		if(pl.hasPermission(str) || (pl.isOp() && playerIsPrivileged(pl))){
			return true;
		}
		return false;
	}

	public static boolean playerIsPrivileged(Player pl){
		if(pl.hasPermission("chatcontrol.bypass")) {
			return true;
		}
		if(pl.isOp()){
			if(ChatControl.Config.getBoolean("Miscellaneous.Op_Has_Permissions", true)){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static void customAction(final Player pl, final String action, final String msg){
		if(!ChatControl.Config.getString(action).equalsIgnoreCase("none")){
			Bukkit.getScheduler().scheduleSyncDelayedTask(ChatControl.plugin, new Runnable() {
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatControl.Config.getString(action).replace("&", "§").replace("%player", pl.getName()).replace("%message", msg.replace("&", "§")));
				}
			});			
		}		
	}

	public static String insertComma(String msg){
		if(!ChatControl.Config.getBoolean("Grammar.Insert_Dot")){
			return msg;
		}
		String lastChar = msg.substring(msg.length() - 1);
		String[] words = msg.split("\\s");
		String lastWord = words[(words.length - 1)];
		if ((lastChar.matches("(?i)[a-z]")) && (!jeOdkaz(lastWord))) {
			msg = msg + ".";
		}
		return msg;
	}

	public static String capitalise(String msg){
		if(!ChatControl.Config.getBoolean("Grammar.Capitalise")){
			return msg;
		}
		String[] sentences = msg.split("(?<=[!?\\.])\\s");
		String tempMessage = "";
		for (String sentence : sentences) {
			String slovo = msg.split("\\s")[0];
			if (!jeOdkaz(slovo)) {
				sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
			}
			tempMessage = tempMessage + sentence + " ";
		}
		return tempMessage.trim();
	}

	public static String replaceCharacters(Player pl, String msg){
		if(!ChatControl.Config.getBoolean("Grammar.Replace_Characters")){
			return osmajlikovat(msg);
		}				
		for (String character : ChatControl.Config.getConfigurationSection("Grammar.Replace_List").getKeys(false)) {
			msg = msg.replaceAll(character, ChatControl.Config.getString("Grammar.Replace_List." + character));			
		}

		return osmajlikovat(msg);
	}

	public static String osmajlikovat(String msg){
		if(!ChatControl.Config.getBoolean("Grammar.Replace_With_Smileys")){
			return msg;
		}
		msg = msg.replace(":)", "☺").replace(":-)", "☺").replace(":(", "☹").replace(":-(", "☹").replace(";)", "㋡").replace(";-)", "㋡").replace(":love:", "♥")
				.replace(":square:", "■").replace(":rectangle:", "█").replace("<3", "♥");
		return msg;
	}

	private static boolean jeOdkaz(String str){
		return str.matches(ChatControl.Config.getString("Anti_Ad.Domain_Filter"));
	}

	public static void debug(String str){
		if(ChatControl.Config.getBoolean("Miscellaneous.Debug")){
			System.out.println("(ChatControl) " + str.replace("&", "§"));
		}
	}

	public static void Log(String str){
		System.out.println("(ChatControl) " + str.replace("&", "§"));
	}

	public static String resolvedSender(CommandSender sender){
		if (sender instanceof Player)
			return sender.getName();
		return console();
	}
}
