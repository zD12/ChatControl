package kangarko.chatcontrol.listener;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.checks.ChecksUtils;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.model.Variables;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;
import kangarko.chatcontrol.utils.Writer;
import kangarko.chatcontrol.utils.Writer.FileType;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (ChatControl.getOnlinePlayers().length < Settings.General.MIN_PLAYERS_TO_ENABLE)
			return;

		Player pl = e.getPlayer();
		String rawMessage = e.getMessage(); // TODO careful

		if (!Common.hasPerm(pl, Permissions.Bypasses.GLOBAL_PERM)) {
			if (Settings.AntiSpam.BLOCK_CHAT_UNTIL_MOVED && pl.getLocation().equals(ChatControl.getDataFor(pl).loginLocation)) {
				if (!Common.hasPerm(pl, Permissions.Bypasses.MOVE)) {
					Common.tell(pl, Localization.CANNOT_CHAT_UNTIL_MOVED);
					e.setCancelled(true);
					return;
				}
			}

			if (Variables.muted && !Common.hasPerm(pl, Permissions.Bypasses.MUTE)) {
				Common.tell(pl, Localization.CANNOT_CHAT_WHILE_MUTED);
				e.setCancelled(true);
				return;
			}

			long cas = System.currentTimeMillis() / 1000L;
			if ((cas - ChatControl.getDataFor(pl).lastMessageTime) < Settings.AntiSpam.Messages.DELAY) {
				if (!Common.hasPerm(pl, Permissions.Bypasses.DELAY_CHAT)) {
					long time = Settings.AntiSpam.Messages.DELAY - (cas - ChatControl.getDataFor(pl).lastMessageTime);
					
					Common.tell(pl, Localization.CHAT_WAIT_MESSAGE.replace("%time", String.valueOf(time)).replace("%seconds", Localization.Parts.SECONDS.formatNumbers(time)));
					e.setCancelled(true);
					return;
				}
			}
			ChatControl.getDataFor(pl).lastMessageTime = cas;
		
			if (Settings.AntiSpam.Messages.SIMILARITY > 0 && Settings.AntiSpam.Messages.SIMILARITY < 100) {
				String strippedMsg = Common.prepareForSimilarityCheck(rawMessage);

				if (ChecksUtils.similarity(strippedMsg, ChatControl.getDataFor(pl).lastMessage) > Settings.AntiSpam.Messages.SIMILARITY) {
					if (!Common.hasPerm(pl, Permissions.Bypasses.SIMILAR_CHAT)) {
						Common.tell(pl, Localization.ANTISPAM_SIMILAR_MESSAGE);
						e.setCancelled(true);
						return;
					}
				}
				ChatControl.getDataFor(pl).lastMessage = strippedMsg;
			}

			if (!Common.hasPerm(pl, Permissions.Bypasses.CHARACTER_REPLACE))
				rawMessage = Common.replaceCharacters(pl, rawMessage);

			if (Settings.AntiAd.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.ADVERTISING)) {
				if (ChecksUtils.isAdvertisement(pl, rawMessage.toLowerCase(), false, false))
					e.setCancelled(true);
			}

			if (Settings.AntiCaps.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.CAPS)) {
				if (rawMessage.length() >= Settings.AntiCaps.MIN_MESSAGE_LENGTH) {

					int[] newMessage = Common.checkCaps(rawMessage);
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

						rawMessage = StringUtils.join(parts, " ");

						if (Settings.AntiCaps.WARN_PLAYER)
							Common.tell(pl, Localization.ANTISPAM_CAPS_MESSAGE);
					}
				}
			}

			if (Settings.AntiSwear.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.SWEARING)) {

				String censoredMessage = ChecksUtils.isSwear(pl, rawMessage, Common.prepareForSwearCheck(rawMessage));

				if (censoredMessage != rawMessage) {
					if (Settings.AntiSwear.BLOCK_MESSAGE) {
						e.setCancelled(true);
						return;
					}
					if (Settings.AntiSwear.REPLACE_MESSAGE)
						rawMessage = censoredMessage;
				}
			}
		}

		if (!Common.hasPerm(pl, Permissions.Bypasses.CAPITALIZE))
			rawMessage = Common.capitalize(rawMessage);
		if (!Common.hasPerm(pl, Permissions.Bypasses.PUNCTUATE))
			rawMessage = Common.insertDot(rawMessage);

		if (!rawMessage.equals(e.getMessage()))
			e.setMessage(rawMessage);

		if (Settings.Writer.ENABLED && !Settings.Writer.WHITELIST_PLAYERS.contains(pl.getName().toLowerCase()))
			Writer.zapisatDo(FileType.CHAT_LOG, pl.getName(), rawMessage);

		if (Settings.SoundNotify.ENABLED) {
			if (Settings.SoundNotify.CHAT_PREFIX.equalsIgnoreCase("none")) {
				for (Player online : ChatControl.getOnlinePlayers())
					if (rawMessage.toLowerCase().contains(online.getName().toLowerCase()) && ChatControl.instance().canSoundNotify(online.getName()) && Common.hasPerm(online, Permissions.Notify.WHEN_MENTIONED))
						online.playSound(online.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);

			} else {
				for (Player online : ChatControl.getOnlinePlayers())
					if (rawMessage.toLowerCase().contains(Settings.SoundNotify.CHAT_PREFIX + online.getName().toLowerCase()) && ChatControl.instance().canSoundNotify(online.getName()) && Common.hasPerm(online, Permissions.Notify.WHEN_MENTIONED))
						online.playSound(online.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
			}
		}
	}
}