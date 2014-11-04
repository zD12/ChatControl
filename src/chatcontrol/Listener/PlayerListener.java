package chatcontrol.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;
import chatcontrol.Utils.Checks.ChecksUtils;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener{

	@EventHandler(ignoreCancelled=true)
	public void onPreLogin(AsyncPlayerPreLoginEvent e){
		long cas = System.currentTimeMillis() / 1000L;
		String ip = e.getAddress().getHostAddress();
		
		if(ChatControl.ipLastLogin.containsKey(ip) && ChatControl.ipLastLogin.get(ip) > ChatControl.Config.getLong("Anti_Bot.Rejoin_Time")){
			if((ChatControl.Config.getLong("Anti_Bot.Rejoin_Time") - (cas - ChatControl.ipLastLogin.get(ip))) <= 0){
				return;
			}
			String msg = Common.colorize(ChatControl.Config.getString("Localization.Prefix") + "\n\n" + ChatControl.Config.getString("Localization.Rejoin_Message").replace("%time", String.valueOf(ChatControl.Config.getLong("Anti_Bot.Rejoin_Time") - (cas - ChatControl.ipLastLogin.get(ip)))));
			msg.split("\n");
			e.disallow(Result.KICK_OTHER, msg);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		ChatControl.createDataIfNotExistFor(e.getPlayer().getName());
		
		long cas = System.currentTimeMillis() / 1000L;
		
		if(!e.getPlayer().isOp() && !e.getPlayer().hasPermission(Permissions.Bypasses.rejoin))
			ChatControl.ipLastLogin.put(e.getPlayer().getAddress().getAddress().getHostAddress(), cas);
		
		ChatControl.getDataFor(e.getPlayer()).loginLocation = e.getPlayer().getLocation();
		if(ChatControl.muted && ChatControl.Config.getBoolean("Mute.Disable.Join_Messages")){
			e.setJoinMessage(null);
			return;
		}
		if(ChatControl.needsUpdate && ChatControl.Config.getBoolean("Miscellaneous.Notify_New_Version") && ChatControl.Config.getBoolean("Miscellaneous.Check_For_Updates")){
			for(Player pl : Bukkit.getOnlinePlayers()){
				if(pl.isOp() || pl.hasPermission(Permissions.Notify.plugin_update)){
					String sprava = Common.colorize(ChatControl.Config.getString("Localization.Update_Needed")).replace("%current", ChatControl.plugin.getDescription().getVersion()).replace("%new", ChatControl.newVersion);
					sprava.split("\n");
					Common.sendRawTimedMsg(pl, sprava, 5);
				}
			}
		}
		switch (ChatControl.Config.getString("Messages.Common.Join_Message")) {
		case "default":
			break;
		case "none":
			e.setJoinMessage(null);
			break;
		default:
			e.setJoinMessage(Common.colorize(ChatControl.Config.getString("Messages.Common.Join_Message").replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName()).replace("%prefix", Common.prefix())));
			break;
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(ChatControl.muted){
			if(ChatControl.Config.getBoolean("Mute.Disable.Quit_Messages")){
				e.setQuitMessage(null);
				return;
			}
		}
		switch (ChatControl.Config.getString("Messages.Common.Quit_Message")) {
		case "default":
			break;
		case "none":
			e.setQuitMessage(null);
			break;
		default:
			e.setQuitMessage(Common.colorize(ChatControl.Config.getString("Messages.Common.Quit_Message").replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName()).replace("%prefix", Common.prefix())));
			break;
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onKick(PlayerKickEvent e){
		if(ChatControl.muted){
			if(ChatControl.Config.getBoolean("Mute.Disable.Kick_Messages")){
				e.setLeaveMessage(null);
				return;
			}
		}
		switch (ChatControl.Config.getString("Messages.Common.Kick_Message")) {
		case "default":
			break;
		case "none":
			e.setLeaveMessage(null);
			break;
		default:
			e.setLeaveMessage(Common.colorize(ChatControl.Config.getString("Messages.Common.Kick_Message").replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName()).replace("%prefix", Common.prefix())));
			break;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if(ChatControl.muted){
			if(ChatControl.Config.getBoolean("Mute.Disable.Death_Messages")){
				e.setDeathMessage(null);
				return;
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onSignChange(SignChangeEvent e){

		if(Common.hasPerm(e.getPlayer(), Permissions.Bypasses.global_perm)){
			return;
		}

		if(ChatControl.Config.getBoolean("Signs.Duplication_Check")){
			if (e.getPlayer().hasPermission(Permissions.Bypasses.dupeSigns)) {
				return;
			}
			if(ChatControl.getDataFor(e.getPlayer()).lastSignText.equals(e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3))){
				Common.sendMsg(e.getPlayer(), "Localization.Dupe_Sign");
				e.setCancelled(true);
				return;
			}
			ChatControl.getDataFor(e.getPlayer()).lastSignText = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);
		}

		if (ChatControl.Config.getBoolean("Signs.Advertising_Check")) {
			if (Bukkit.getOnlinePlayers().length < ChatControl.Config.getInt("Miscellaneous.Minimum_Players_To_Enable_Plugin")) {
				return;
			}
			String msg = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);
			if (ChecksUtils.advertisingCheck(e.getPlayer(), msg.toLowerCase(), false)) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.ads)) {
					return;
				}
				Common.customAction(e.getPlayer(), "Anti_Ad.Custom_Command", msg);
				Common.messages(e.getPlayer(), ChatColor.GRAY + "[SIGN] " + ChatColor.WHITE + msg);
				if(ChatControl.Config.getBoolean("Signs.Rewrite_Lines")){
					String[] cenzura = Common.colorize(ChatControl.Config.getString("Signs.Rewrite_Text")).split(":");
					try {
						e.setLine(0, cenzura[0]);
						e.setLine(1, cenzura[1]);
						e.setLine(2, cenzura[2]);
						e.setLine(3, cenzura[3]);
					} catch (Exception ex){}
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
}
