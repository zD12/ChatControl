package me.kangarko.chc.listener;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.Variables;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;
import me.kangarko.chc.utils.Writer;
import me.kangarko.chc.utils.Writer.FileType;
import me.kangarko.chc.utils.checks.ChecksUtils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (Bukkit.getOnlinePlayers().size() < Settings.MIN_PLAYERS_TO_ENABLE)
			return;

		Player pl = e.getPlayer();
		String theMessage = e.getMessage();

		if (!Common.hasPerm(pl, Permissions.Bypasses.global_perm)) {
			if (Settings.Chat.BLOCK_CHAT_UNTIL_MOVED && pl.getLocation().equals(ChatControl.getDataFor(pl).loginLocation)) {
				if (!Common.hasPerm(pl, Permissions.Bypasses.move)) {
					Common.tell(pl, Localization.CANNOT_CHAT_UNTIL_MOVED);
					e.setCancelled(true);
					return;
				}
			}

			if (Variables.muted && !Common.hasPerm(pl, Permissions.Bypasses.mute)) {
				Common.tell(pl, Localization.CANNOT_CHAT_WHILE_MUTED);
				e.setCancelled(true);
				return;
			}

			long cas = System.currentTimeMillis() / 1000L;
			if ((cas - ChatControl.getDataFor(pl).lastMessageTime) < Settings.Chat.MESSAGE_DELAY) {
				if (!Common.hasPerm(pl, Permissions.Bypasses.timeChat)) {
					long time = Settings.Chat.MESSAGE_DELAY - (cas - ChatControl.getDataFor(pl).lastMessageTime);
					
					Common.tell(pl, Localization.CHAT_WAIT_MESSAGE.replace("%time", String.valueOf(time)).replace("%seconds", Localization.Parts.SECONDS.formatNumbers(time)));
					e.setCancelled(true);
					return;
				}
			}
			ChatControl.getDataFor(pl).lastMessageTime = cas;

			if (Settings.Chat.BLOCK_SIMILAR_MORE_THAN > 0) {
				String sprava = theMessage;

				if (Settings.Chat.STRIP_UNICODE_IN_CHECKS)
					sprava = Common.stripSpecialCharacters(sprava);

				if (ChecksUtils.similarityCheck(sprava, ChatControl.getDataFor(pl).lastMessage) > Settings.Chat.BLOCK_SIMILAR_MORE_THAN) { // TODO
					if (!Common.hasPerm(pl, Permissions.Bypasses.dupeChat)) {
						Common.tell(pl, Localization.ANTISPAM_SIMILAR_MESSAGE);
						e.setCancelled(true);
						return;
					}
				}
				ChatControl.getDataFor(pl).lastMessage = sprava;
			}

			if (!Common.hasPerm(pl, Permissions.Bypasses.replace))
				theMessage = Common.replaceCharacters(pl, theMessage);

			if (Settings.AntiAd.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.ads)) {
				if (ChecksUtils.advertisingCheck(pl, theMessage.toLowerCase(), false)) {
					Common.customAction(pl, "Anti_Ad.Custom_Command", theMessage);
					Common.messages(pl, theMessage);
					e.setCancelled(true);
				}
			}

			if (Settings.AntiCaps.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.caps)) {
				if (theMessage.length() >= Settings.AntiCaps.MIN_MESSAGE_LENGTH) {

					int[] newMessage = Common.checkCaps(theMessage);
					if ((Common.percentageCaps(newMessage) >= Settings.AntiCaps.MIN_CAPS_PERCENTAGE) || (Common.checkCapsInRow(newMessage) >= Settings.AntiCaps.MIN_CAPS_IN_A_ROW)) {

						String[] parts = e.getMessage().split(" ");
						boolean capsAllowed = false;
						for (int i = 0; i < parts.length; i++) {
							boolean isOnWhitelist = false;
							for (String whitelist : Settings.AntiCaps.WHITELIST) {
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

						if (Settings.AntiCaps.WARN_PLAYER)
							Common.tell(pl, Localization.ANTISPAM_CAPS_MESSAGE);
					}
				}
			}

			if (Settings.AntiSwear.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.swear)) {

				String censoredMessage = ChecksUtils.swearCheck(pl, theMessage, Common.prepareForSwearCheck(theMessage));

				if (censoredMessage != theMessage) {
					if (Settings.AntiSwear.BLOCK_MESSAGE) {
						e.setCancelled(true);
						return;
					}
					if (Settings.AntiSwear.REPLACE_MESSAGE)
						theMessage = censoredMessage;
				}
			}
		}

		if (!Common.hasPerm(pl, Permissions.Bypasses.capitalize))
			theMessage = Common.capitalize(theMessage);
		if (!Common.hasPerm(pl, Permissions.Bypasses.insertDot))
			theMessage = Common.insertDot(theMessage);

		if (!theMessage.equals(e.getMessage()))
			e.setMessage(theMessage);

		if (Settings.Writer.ENABLED && !Settings.Writer.WHITELIST_PLAYERS.contains(pl.getName().toLowerCase()))
			Writer.zapisatDo(FileType.CHAT_LOG, pl.getName(), theMessage);

		if (Settings.SoundNotify.ENABLED) {
			if (Settings.SoundNotify.CHAT_PREFIX.equalsIgnoreCase("none")) {
				for (Player online : Bukkit.getOnlinePlayers())
					if (theMessage.toLowerCase().contains(online.getName().toLowerCase()) && ChatControl.instance().checkForAfk(online.getName()) && Common.hasPerm(online, Permissions.Notify.whenMentioned))
						online.playSound(online.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);

			} else {
				for (Player online : Bukkit.getOnlinePlayers())
					if (theMessage.toLowerCase().contains(Settings.SoundNotify.CHAT_PREFIX + online.getName().toLowerCase()) && ChatControl.instance().checkForAfk(online.getName()) && Common.hasPerm(online, Permissions.Notify.whenMentioned))
						online.playSound(online.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
			}
		}
	}
}