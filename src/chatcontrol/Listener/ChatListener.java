package chatcontrol.Listener;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import chatcontrol.ChatControl;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;
import chatcontrol.Utils.Writer;
import chatcontrol.Utils.Writer.TypSuboru;
import chatcontrol.Utils.Checks.ChecksUtils;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {

	@EventHandler(priority=EventPriority.LOW,ignoreCancelled=true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		if (Bukkit.getOnlinePlayers().length < ChatControl.Config.getInt("Miscellaneous.Minimum_Players_To_Enable_Plugin"))
			return;

		Player pl = e.getPlayer();
		String theMessage = e.getMessage();

		if(!Common.hasPerm(pl, Permissions.Bypasses.global_perm)) {
			if (ChatControl.Config.getBoolean("Miscellaneous.Block_Chat_Until_Moved") && pl.getLocation().equals(ChatControl.playerData.get(pl).loginLocation)) {
				if (!pl.hasPermission(Permissions.Bypasses.move)) {
					Common.sendMsg(pl, "Localization.Cannot_Chat_Until_Moved");
					e.setCancelled(true);
					return;
				}
			}

			if (ChatControl.muted && !pl.hasPermission(Permissions.Bypasses.mute)) {
				Common.sendMsg(pl, "Localization.Cannot_Chat_While_Muted");
				e.setCancelled(true);
				return;
			}

			long cas = System.currentTimeMillis() / 1000L;
			if ((cas - ChatControl.playerData.get(pl).lastMessageTime) < ChatControl.Config.getLong("Chat.Message_Delay")) {
				if (!pl.hasPermission(Permissions.Bypasses.timeChat)) {
					Common.sendRawMsg(pl, ChatControl.Config.getString("Localization.Time_Message").replace("%time", String.valueOf(ChatControl.Config.getLong("Chat.Message_Delay") - (cas - ChatControl.playerData.get(pl).lastMessageTime))));
					e.setCancelled(true);
					return;
				}
			}
			ChatControl.playerData.get(pl).lastMessageTime = cas;

			if (ChatControl.Config.getBoolean("Chat.Block_Duplicate_Messages")) {
				String sprava = theMessage;
				if(ChatControl.Config.getBoolean("Chat.Strip_Unicode")) {
					sprava = Common.stripSpecialCharacters(sprava);
				}
				if ((ChatControl.playerData.get(pl).lastMessage.equals(sprava) || Common.stringsAreSimilar(sprava, ChatControl.playerData.get(pl).lastMessage) && ChatControl.Config.getBoolean("Chat.Block_Similar_Messages")) ) {
					if (!pl.hasPermission(Permissions.Bypasses.dupeChat)) {
						Common.sendMsg(pl, "Localization.Dupe_Message");
						e.setCancelled(true);
						return;
					}
				}
				ChatControl.playerData.get(pl).lastMessage = sprava;
			}
			
			if(!pl.hasPermission(Permissions.Bypasses.replace))
				theMessage = Common.replaceCharacters(pl, theMessage);
			
			if(ChatControl.Config.getBoolean("Anti_Ad.Enabled") && !pl.hasPermission(Permissions.Bypasses.ads)) {
				if (ChecksUtils.advertisingCheck(pl, theMessage.toLowerCase(), false)) {
					Common.customAction(pl, "Anti_Ad.Custom_Command", theMessage);
					Common.messages(pl, theMessage);
					e.setCancelled(true);
				}
			}

			if (ChatControl.Config.getBoolean("Anti_Caps.Enabled") && !pl.hasPermission(Permissions.Bypasses.caps)) {
				if(theMessage.length() >= ChatControl.Config.getInt("Anti_Caps.Minimum_Message_Length")) {

					int[] newMessage = Common.checkCaps(theMessage);
					if ((Common.percentageCaps(newMessage) >= ChatControl.Config.getInt("Anti_Caps.Total_Caps_Percentage")) || (Common.checkCapsInRow(newMessage) >= ChatControl.Config.getInt("Anti_Caps.Caps_In_A_Row"))){

						String[] parts = e.getMessage().split(" ");
						boolean capsAllowed = false;
						for (int i = 0; i < parts.length; i++) {
							boolean isOnWhitelist = false;
							for (String whitelist : ChatControl.Config.getStringList("Anti_Caps.Whitelist")) {
								if (whitelist.equalsIgnoreCase(parts[i])) {
									isOnWhitelist = true;
									capsAllowed = true;
									break;
								}
							}

							if (!isOnWhitelist) {
								if (!capsAllowed) {
									char firstChar = parts[i].charAt(0);
									parts[i] = (firstChar + parts[i].toLowerCase().substring(1));
								} else {
									parts[i] = parts[i].toLowerCase();
								}

								capsAllowed = (!parts[i].endsWith(".")) && (!parts[i].endsWith("!"));
							}
						}

						theMessage = StringUtils.join(parts, " ");

						if (ChatControl.Config.getBoolean("Anti_Caps.Warn_Player"))
							Common.sendMsg(pl, "Localization.Caps_Message");
					}
				}
			}

			if (ChatControl.Config.getBoolean("Anti_Swear.Enabled") && !pl.hasPermission(Permissions.Bypasses.swear)) {

				String censoredMessage = ChecksUtils.swearCheck(pl, theMessage, Common.prepareForSwearCheck(theMessage));

				if(censoredMessage != theMessage) {
					if (ChatControl.Config.getBoolean("Anti_Swear.Block_Message")) {
						e.setCancelled(true);
						return;
					}
					if(ChatControl.Config.getBoolean("Anti_Swear.Replace_Word"))
						theMessage = censoredMessage;					
				}
			}
		}

		if(!pl.hasPermission(Permissions.Bypasses.capitalize))
			theMessage = Common.capitalize(theMessage);
		if(!pl.hasPermission(Permissions.Bypasses.insertDot))
			theMessage = Common.insertDot(theMessage);
		
		if (!theMessage.equals(e.getMessage()))		
			e.setMessage(theMessage);

		if (ChatControl.Config.getBoolean("Chat.Write_To_File") && !ChatControl.Config.getStringList("Chat.Ignore_Players").contains(pl.getName()))
			Writer.zapisatDo(TypSuboru.ZAZNAM_CHATU, pl.getName(), theMessage);	
		
		if (ChatControl.Config.getBoolean("Chat.Notify_Player_When_Mentioned.Enabled")) {
			if (ChatControl.Config.getString("Chat.Notify_Player_When_Mentioned.In_Chat_When_Prefixed_With").equalsIgnoreCase("none")) {		
				for (Player online : Bukkit.getOnlinePlayers())	
					if (theMessage.toLowerCase().contains(online.getName().toLowerCase()) && ChatControl.plugin.checkForAfk(online.getName()) && online.hasPermission(Permissions.Notify.whenMentioned))
						online.playSound(online.getLocation(), Sound.valueOf(ChatControl.Config.getString("Chat.Notify_Player_When_Mentioned.Sound", "CHICKEN_EGG_POP")), 1.5F, 1.2F);
					
			} else {				
				for (Player online : Bukkit.getOnlinePlayers()) 
					if (theMessage.toLowerCase().contains(ChatControl.Config.getString("Chat.Notify_Player_When_Mentioned.In_Chat_When_Prefixed_With") + online.getName().toLowerCase()) 
							&& ChatControl.plugin.checkForAfk(online.getName()) && online.hasPermission(Permissions.Notify.whenMentioned))					
						online.playSound(online.getLocation(), Sound.valueOf(ChatControl.Config.getString("Chat.Notify_Player_When_Mentioned.Sound", "CHICKEN_EGG_POP")), 1.5F, 1.2F);
			}
		}
	}
}