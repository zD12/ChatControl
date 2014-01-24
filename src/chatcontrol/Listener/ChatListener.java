package chatcontrol.Listener;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;
import chatcontrol.Utils.Writer;
import chatcontrol.Utils.Writer.TypSuboru;

public class ChatListener implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		if (Bukkit.getOnlinePlayers().length < ChatControl.Config.getInt("Miscellaneous.Minimum_Players_To_Enable_Plugin")) {
			return;
		}
		String finalMsg = null;
		if(!Common.playerIsPrivileged(e.getPlayer())){
			if (ChatControl.Config.getBoolean("Miscellaneous.Block_Chat_Until_Moved") && 
					(e.getPlayer().getLocation() == ChatControl.data.get(e.getPlayer()).loginLocation)) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.move)) {
					return;
				}
				Common.sendMsg(e.getPlayer(), "Localization.Cannot_Chat_Until_Moved");
				e.setCancelled(true);
				return;
			}

			if (ChatControl.muted) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.mute)) {
					return;
				}
				Common.sendMsg(e.getPlayer(), "Localization.Cannot_Chat_While_Muted");
				e.setCancelled(true);
				return;
			}

			Long cas = Long.valueOf(new Date().getTime());
			cas = Long.valueOf(cas.longValue() / 1000L);
			if ((cas.longValue() - ChatControl.data.get(e.getPlayer()).lastMessageTime) < ChatControl.Config.getLong("Chat.Message_Delay")) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.time)) {
					return;
				}
				e.getPlayer().sendMessage(ChatControl.Config.getString("Localization.Time_Message").replace("&", "§").replace("%prefix", Common.prefix()).replace("%time", String.valueOf(ChatControl.Config.getLong("Chat.Message_Delay") - (cas.longValue() - ChatControl.data.get(e.getPlayer()).lastMessageTime))));
				e.setCancelled(true);
				return;
			}
			ChatControl.data.get(e.getPlayer()).lastMessageTime = Long.valueOf(cas.longValue());

			if (ChatControl.Config.getBoolean("Chat.Block_Duplicate_Messages")) {
				String sprava;
				if(ChatControl.Config.getBoolean("Chat.Strip_Unicode")) {
					sprava = Common.stripSpecialCharacters(e.getMessage());
				} else {
					sprava = e.getMessage().replaceAll("[.:_,!*÷*><}{&#'$|\\/()]", "").toLowerCase();
				}
				if (ChatControl.data.get(e.getPlayer()).lastMessage.equalsIgnoreCase(sprava) || (Common.stringsAreSimilar(sprava.toLowerCase(), ChatControl.data.get(e.getPlayer()).lastMessage.toLowerCase()) 
						&& ChatControl.Config.getBoolean("Chat.Block_Similar_Messages")) ) {
					if (e.getPlayer().hasPermission(Permissions.Bypasses.dupe)) {
						return;
					}
					Common.sendMsg(e.getPlayer(), "Localization.Dupe_Message");
					e.setCancelled(true);
					return;
				}
				ChatControl.data.get(e.getPlayer()).lastMessage = sprava;
			}

			String msgLowerCase = e.getMessage().toLowerCase();

			if (Common.msgIsAd(e.getPlayer(), msgLowerCase)) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.ads)) {
					return;
				}
				Common.customAction(e.getPlayer(), "Anti_Ad.Custom_Command", e.getMessage());
				Common.messages(e.getPlayer(), e.getMessage());
				e.setCancelled(true);
			}

			if (ChatControl.Config.getBoolean("Anti_Caps.Enabled")) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.caps)) {
					return;
				}
				for (String msg : ChatControl.Config.getStringList("Anti_Caps.Whitelist")) {
					if (e.getMessage().matches(".*" + msg + ".*")) {
						return;
					}
				}
				if(e.getMessage().length() < ChatControl.Config.getInt("Anti_Caps.Minimum_Message_Length")) { 
					return;
				}
				String msg = e.getMessage().replaceAll("(\\W|\\d|_)*", "");
				if (msg.matches(ChatControl.Config.getString("Anti_Caps.Pattern"))) {
					e.setMessage(e.getMessage().toLowerCase());
					if (ChatControl.Config.getBoolean("Anti_Caps.Warn_Player")) {
						Common.sendMsg(e.getPlayer(), "Localization.Caps_Message");
					}
				}
			}

			if (ChatControl.Config.getBoolean("Anti_Swear.Enabled")) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.swear)) {
					return;
				}
				for (String msg : ChatControl.Config.getStringList("Anti_Swear.Word_List")) {
					if (e.getMessage().toLowerCase().contains(msg.toLowerCase()) || e.getMessage().toLowerCase().matches(".*" + msg + ".*")) {
						if (ChatControl.Config.getBoolean("Anti_Swear.Inform_Admins")) {
							for (Player pl : Bukkit.getOnlinePlayers()) {
								if ( pl.isOp() || e.getPlayer().hasPermission(Permissions.Notify.swear) ) {
									pl.sendMessage(ChatControl.Config.getString("Localization.Swear_Admin_Message").replace("&", "§").replace("%prefix", Common.prefix()).replace("%player", e.getPlayer().getName()).replace("%message", e.getMessage()));
								}
							}
						}
						Common.customAction(e.getPlayer(), "Anti_Swear.Custom_Command", e.getMessage());
						if (ChatControl.Config.getBoolean("Anti_Swear.Warn_Player")) {
							Common.sendMsg(e.getPlayer(), "Localization.Do_Not_Swear");
						}
						if (ChatControl.Config.getBoolean("Anti_Swear.Block_Message")) {
							e.setCancelled(true);
							return;
						}
						if (ChatControl.Config.getBoolean("Anti_Swear.Replace_Word")) {
							e.setMessage(ChatControl.Config.getString("Anti_Swear.Replacement").replace("&", "§").replace("%player", e.getPlayer().getName()));
						}
					}
				}
			}

			msgLowerCase = Common.replaceCharacters(e.getPlayer(), msgLowerCase);
			msgLowerCase = Common.capitalize(msgLowerCase);
			msgLowerCase = Common.insertComma(msgLowerCase);
			e.setMessage(msgLowerCase);
			finalMsg = msgLowerCase;
		}
		
		if (ChatControl.Config.getBoolean("Chat.Write_To_File") && !ChatControl.Config.getStringList("Chat.Ignore_Players").contains(e.getPlayer().getName())) {
			Writer.writeToFile(TypSuboru.ZAZNAM_CHATU, e.getPlayer().getName(), (finalMsg != null ? finalMsg : e.getMessage()));
		}
	}
}