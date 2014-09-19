package chatcontrol.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;
import chatcontrol.Utils.Writer;
import chatcontrol.Utils.Writer.TypSuboru;
import chatcontrol.Utils.Checks.ChecksUtils;

public class CommandListener implements Listener{

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e){
		if(!(Bukkit.getOnlinePlayers().length >= ChatControl.Config.getInt("Miscellaneous.Minimum_Players_To_Enable_Plugin"))){
			return;
		}

		Player pl = e.getPlayer();
		
		if(!Common.hasPerm(pl, Permissions.Bypasses.global_perm) ) {
			if(ChatControl.muted){
				if (pl.hasPermission(Permissions.Bypasses.mute)) {
					return;
				}
				for (String msg : ChatControl.Config.getStringList("Mute.Disabled_Commands_During_Mute")){
					if(e.getMessage().startsWith("/" + msg)){
						Common.sendMsg(pl, "Localization.Cannot_Command_While_Muted");
						e.setCancelled(true);
						return;
					}
				}
			}

			long cas = System.currentTimeMillis() / 1000L;

			timeCheck: if((cas - ChatControl.data.get(pl).lastCommandTime) < ChatControl.Config.getLong("Commands.Command_Delay")){
				if(pl.hasPermission(Permissions.Bypasses.timeCmd)){
					break timeCheck;
				}
				for (String sprava : ChatControl.Config.getStringList("Commands.Whitelist_Time")){
					if(e.getMessage().startsWith("/" + sprava)){
						break timeCheck;
					}
				}
				Common.sendRawMsg(pl, ChatControl.Config.getString("Localization.Command_Message").replace("%time", String.valueOf(ChatControl.Config.getLong("Commands.Command_Delay") - (cas - ChatControl.data.get(pl).lastCommandTime))));
				e.setCancelled(true);
				return;
			} else {
				ChatControl.data.get(pl).lastCommandTime = cas;
			}

			dupeCheck: if(ChatControl.Config.getBoolean("Commands.Block_Duplicate_Commands")){
				String sprava = e.getMessage().toLowerCase();
				
				// Strip from messages like /tell <player> <msg> the player, making the check less less annoying.
				if(e.getMessage().split(" ").length > 2) {
					Player reciever = Bukkit.getPlayer(e.getMessage().split(" ")[1]);
					if(reciever != null && reciever.isOnline()) {
						sprava = sprava.replace(reciever.getName() + " ", "");
					}
				}
				
				if(ChatControl.Config.getBoolean("Commands.Strip_Unicode")) {
					sprava = Common.stripSpecialCharacters(sprava);
				}
				if(ChatControl.data.get(pl).lastCommand.equals(sprava) || (Common.stringsAreSimilar(sprava, ChatControl.data.get(pl).lastCommand) && ChatControl.Config.getBoolean("Commands.Block_Similar_Commands")) ){
					if(pl.hasPermission(Permissions.Bypasses.dupeCmd))
						break dupeCheck;
					
					for (String whitelistedMsg : ChatControl.Config.getStringList("Commands.Whitelist_Duplication")){
						if(e.getMessage().startsWith("/" + whitelistedMsg)){
							break dupeCheck;
						}
					}
					Common.sendMsg(pl, "Localization.Dupe_Command");
					e.setCancelled(true);
					return;
				}
				ChatControl.data.get(pl).lastCommand = sprava;
			}

			adCheck: if(ChatControl.Config.getBoolean("Anti_Ad.Enabled_In_Commands") && !pl.hasPermission(Permissions.Bypasses.ads)){
				if(ChecksUtils.advertisingCheck(pl, e.getMessage(), true)){
					for(String whitelist : ChatControl.Config.getStringList("Anti_Ad.Command_Whitelist")){
						if(e.getMessage().startsWith(whitelist)){
							break adCheck;
						}
					}
					Common.customAction(pl, "Anti_Ad.Custom_Command", e.getMessage());
					Common.messages(pl, e.getMessage());
					e.setCancelled(true);
				}
			}

			swearCheck: if(ChatControl.Config.getBoolean("Anti_Swear.Enabled_In_Commands") && !pl.hasPermission(Permissions.Bypasses.swear)){
				for(String ignoredCmd : ChatControl.Config.getStringList("Anti_Swear.Command_Whitelist")) {
					if(e.getMessage().startsWith(ignoredCmd)) {
						break swearCheck;
					}
				}
				String finalMessage = ChecksUtils.swearCheck(pl, e.getMessage(), Common.prepareForSwearCheck(e.getMessage()));

				if(finalMessage != e.getMessage()) {
					if (ChatControl.Config.getBoolean("Anti_Swear.Block_Message")) {
						e.setCancelled(true);
						return;
					}
					if(ChatControl.Config.getBoolean("Anti_Swear.Replace_Word")) {
						e.setMessage(finalMessage);
					}
				}
			}
		}

		if (ChatControl.Config.getBoolean("Chat.Write_To_File") && !ChatControl.Config.getStringList("Chat.Ignore_Players").contains(pl.getName())) {
			for(String prikaz : ChatControl.Config.getStringList("Chat.Include_Commands") ) {
				prikaz = prikaz.toLowerCase();
				if(e.getMessage().toLowerCase().startsWith(prikaz)) {
					Writer.zapisatDo(TypSuboru.ZAZNAM_CHATU, "[CMD] " + pl.getName(), e.getMessage());
				}
			}
		}
		
		if (ChatControl.Config.getBoolean("Chat.Notify_Player_When_Mentioned.Enabled_In_Commands")) {
			String[] args = e.getMessage().split(" ");
			
			if (ChatControl.Config.getStringList("Chat.Notify_Player_When_Mentioned.In_Commands").contains(args[0])) {
				
				if(args.length > 2) {
					Player player = Bukkit.getPlayer(args[1]);
					if (player == null || !player.isOnline())
						return;

					player.playSound(player.getLocation(), Sound.valueOf(ChatControl.Config.getString("Chat.Notify_Player_When_Mentioned.Sound")), 1.5F, 1.5F);
					
				} else if (e.getMessage().startsWith("/r ") || e.getMessage().startsWith("/reply ")) {
					Player reply = ChatControl.plugin.getReplyTo(pl);
					
					if(reply != null && reply.hasPermission(Permissions.Notify.whenMentioned))
						reply.playSound(reply.getLocation(), Sound.valueOf(ChatControl.Config.getString("Chat.Notify_Player_When_Mentioned.Sound")), 1.5F, 1.5F);
				}
			}
		}
	}
}