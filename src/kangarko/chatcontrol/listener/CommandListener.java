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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (ChatControl.getOnlinePlayers().length < Settings.General.MIN_PLAYERS_TO_ENABLE)
			return;

		Player pl = e.getPlayer();

		if (!Common.hasPerm(pl, Permissions.Bypasses.GLOBAL_PERM)) {
			if (Variables.muted) {
				if (Common.hasPerm(pl, Permissions.Bypasses.MUTE))
					return;

				if (Settings.Mute.DISABLED_CMDS_WHEN_MUTED.contains(e.getMessage().split(" ")[0].replaceFirst("/", ""))) {
					Common.tell(pl, Localization.CANNOT_COMMAND_WHILE_MUTED);
					e.setCancelled(true);
					return;
				}
			}

			long cas = System.currentTimeMillis() / 1000L;

			timeCheck: if ((cas - ChatControl.getDataFor(pl).lastCommandTime) < Settings.AntiSpam.Commands.DELAY) {
				if (Common.hasPerm(pl, Permissions.Bypasses.DELAY_COMMANDS))
					break timeCheck;

				if (Settings.AntiSpam.Commands.WHITELIST_DELAY.contains(e.getMessage().split(" ")[0].replaceFirst("/", "")))
					break timeCheck;

				long time = Settings.AntiSpam.Commands.DELAY - (cas - ChatControl.getDataFor(pl).lastCommandTime);

				Common.tell(pl, Localization.COMMAND_WAIT_MESSAGE.replace("%time", String.valueOf(time)).replace("%seconds", Localization.Parts.SECONDS.formatNumbers(time)));
				e.setCancelled(true);
				return;
			} else {
				ChatControl.getDataFor(pl).lastCommandTime = cas;
			}

			dupeCheck: if (Settings.AntiSpam.Commands.SIMILARITY > 0 && Settings.AntiSpam.Commands.SIMILARITY < 100) {
				String strippedMsg = e.getMessage().toLowerCase();
				String[] args = strippedMsg.split(" ");

				// Strip from messages like /tell <player> <msg> the player name, making the check less less annoying.
				if (args.length > 2 && (args[0].equals("/tell") || args[0].equals("/msg")))
					strippedMsg = strippedMsg.replace(args[1], "");

				strippedMsg = Common.prepareForSimilarityCheck(strippedMsg);

				if (ChecksUtils.similarity(strippedMsg, ChatControl.getDataFor(pl).lastCommand) > Settings.AntiSpam.Commands.SIMILARITY) {
					if (Common.hasPerm(pl, Permissions.Bypasses.SIMILAR_COMMANDS))
						break dupeCheck;

					if (Settings.AntiSpam.Commands.WHITELIST_SIMILARITY.contains(e.getMessage().split(" ")[0].replaceFirst("/", "")))
						break dupeCheck;

					Common.tell(pl, Localization.ANTISPAM_SIMILAR_COMMAND);
					e.setCancelled(true);
					return;
				}
				ChatControl.getDataFor(pl).lastCommand = strippedMsg;
			}

			if (Settings.AntiAd.ENABLED_IN_COMMANDS && !Common.hasPerm(pl, Permissions.Bypasses.ADVERTISING)) {
				if (ChecksUtils.isAdvertisement(pl, e.getMessage(), true, false))
					e.setCancelled(true);
			}

			swearCheck: if (Settings.AntiSwear.ENABLED_IN_COMMANDS && !Common.hasPerm(pl, Permissions.Bypasses.SWEARING)) {
				for (String ignoredCmd : Settings.Commands.WHITELIST_SWEAR)
					if (e.getMessage().startsWith("/" + ignoredCmd))
						break swearCheck;

				String finalMessage = ChecksUtils.isSwear(pl, e.getMessage(), Common.prepareForSwearCheck(e.getMessage()));

				if (finalMessage != e.getMessage()) {
					//if (Settings.AntiSwear.BLOCK_MESSAGE) // FIXME This, this is buggy on commands, handle.
						e.setCancelled(true);

					/*else if (Settings.AntiSwear.REPLACE_MESSAGE)
						e.setMessage(finalMessage);*/
				}
			}
		}

		if (Settings.Writer.ENABLED && !Settings.Writer.WHITELIST_PLAYERS.contains(pl.getName().toLowerCase())) {
			for (String prikaz : Settings.Writer.INCLUDE_COMMANDS)
				if (e.getMessage().toLowerCase().startsWith("/" + prikaz.toLowerCase()))
					Writer.zapisatDo(FileType.CHAT_LOG, "[CMD] " + pl.getName(), e.getMessage());
		}

		String[] args = e.getMessage().split(" ");

		if (Settings.SoundNotify.ENABLED_IN_FOLLOWING_COMMANDS.contains(args[0].replaceFirst("/", ""))) {
			if (args.length > 2) {
				Player player = Bukkit.getPlayer(args[1]);
				if (player == null || !player.isOnline())
					return;

				player.playSound(player.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
			} else if (e.getMessage().startsWith("/r ") || e.getMessage().startsWith("/reply ")) {
				Player reply = ChatControl.instance().getReplyTo(pl);

				if (reply != null && Common.hasPerm(reply, Permissions.Notify.WHEN_MENTIONED))
					reply.playSound(reply.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
			}
		}
	}
}