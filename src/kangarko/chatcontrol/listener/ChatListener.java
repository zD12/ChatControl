package kangarko.chatcontrol.listener;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.PlayerCache;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
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
		PlayerCache playerData = ChatControl.getDataFor(pl);
		String rawMessage = e.getMessage();

		if (!Common.hasPerm(pl, Permissions.Bypasses.GLOBAL_PERM)) {
			if (Settings.AntiSpam.BLOCK_CHAT_UNTIL_MOVED && pl.getLocation().equals(playerData.loginLocation)) {
				if (!Common.hasPerm(pl, Permissions.Bypasses.MOVE)) {
					Common.tell(pl, Localization.CANNOT_CHAT_UNTIL_MOVED);
					e.setCancelled(true);
					return;
				}
			}

			if (ChatControl.muted && !Common.hasPerm(pl, Permissions.Bypasses.MUTE)) {
				Common.tell(pl, Localization.CANNOT_CHAT_WHILE_MUTED);
				e.setCancelled(true);
				return;
			}

			long cas = System.currentTimeMillis() / 1000L;
			if ((cas - playerData.lastMessageTime) < Settings.AntiSpam.Messages.DELAY) {
				if (!Common.hasPerm(pl, Permissions.Bypasses.DELAY_CHAT)) {
					long time = Settings.AntiSpam.Messages.DELAY - (cas - playerData.lastMessageTime);

					Common.tell(pl, Localization.CHAT_WAIT_MESSAGE.replace("%time", String.valueOf(time)).replace("%seconds", Localization.Parts.SECONDS.formatNumbers(time)));
					e.setCancelled(true);
					return;
				}
			}
			playerData.lastMessageTime = cas;

			if (Settings.AntiSpam.Messages.SIMILARITY > 0 && Settings.AntiSpam.Messages.SIMILARITY < 100) {
				String strippedMsg = Common.prepareForSimilarityCheck(rawMessage);

				if (Common.similarity(strippedMsg, playerData.lastMessage) > Settings.AntiSpam.Messages.SIMILARITY) {
					if (!Common.hasPerm(pl, Permissions.Bypasses.SIMILAR_CHAT)) {
						Common.tell(pl, Localization.ANTISPAM_SIMILAR_MESSAGE);
						e.setCancelled(true);
						return;
					}
				}
				playerData.lastMessage = strippedMsg;
			}

			if (!Common.hasPerm(pl, Permissions.Bypasses.CHARACTER_REPLACE))
				rawMessage = Common.replaceCharacters(pl, rawMessage);

			rawMessage = ChatControl.instance().chatCeaser.handleRules(e, pl, rawMessage);
			
			if (e.isCancelled()) // cancelled from chat ceaser
				return;

			if (Settings.AntiCaps.ENABLED && !Common.hasPerm(pl, Permissions.Bypasses.CAPS)) {
				if (rawMessage.length() >= Settings.AntiCaps.MIN_MESSAGE_LENGTH) {

					int[] newMessage = Common.checkCaps(rawMessage);
					if ((Common.percentageCaps(newMessage) >= Settings.AntiCaps.MIN_CAPS_PERCENTAGE) || (Common.checkCapsInRow(newMessage) >= Settings.AntiCaps.MIN_CAPS_IN_A_ROW)) {

						String[] parts = rawMessage.split(" ");
						boolean capsAllowed = false;
						boolean whitelisted = false;

						for (int i = 0; i < parts.length; i++) {
							for (String whitelist : Settings.AntiCaps.WHITELIST) {
								if (whitelist.equalsIgnoreCase(parts[i])) {
									whitelisted = true;
									capsAllowed = true;
									break;
								}
							}

							if (!whitelisted) {
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