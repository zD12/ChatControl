package kangarko.chatcontrol.listener;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.PlayerCache;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
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

		String message = e.getMessage();
		String[] args = message.split(" ");
		
		Player pl = e.getPlayer();
		PlayerCache plData = ChatControl.getDataFor(pl);
		
		if (!Common.hasPerm(pl, Permissions.Bypasses.GLOBAL_PERM)) {
			muteCheck: if (ChatControl.muted) {
				if (Common.hasPerm(pl, Permissions.Bypasses.MUTE))
					break muteCheck;

				if (Settings.Mute.DISABLED_CMDS_WHEN_MUTED.contains(args[0].replaceFirst("/", ""))) {
					Common.tell(pl, Localization.CANNOT_COMMAND_WHILE_MUTED);
					e.setCancelled(true);
					return;
				}
			}

			long cas = System.currentTimeMillis() / 1000L;

			timeCheck: if ((cas - plData.lastCommandTime) < Settings.AntiSpam.Commands.DELAY) {
				if (Common.hasPerm(pl, Permissions.Bypasses.DELAY_COMMANDS))
					break timeCheck;

				if (Settings.AntiSpam.Commands.WHITELIST_DELAY.contains(args[0].replaceFirst("/", "")))
					break timeCheck;

				long time = Settings.AntiSpam.Commands.DELAY - (cas - plData.lastCommandTime);

				Common.tell(pl, Localization.COMMAND_WAIT_MESSAGE.replace("%time", String.valueOf(time)).replace("%seconds", Localization.Parts.SECONDS.formatNumbers(time)));
				e.setCancelled(true);
				return;
			} else
				plData.lastCommandTime = cas;

			dupeCheck: if (Settings.AntiSpam.Commands.SIMILARITY > 0 && Settings.AntiSpam.Commands.SIMILARITY < 100) {
				String strippedMsg = message.toLowerCase();
				String[] strippedArgs = strippedMsg.split(" ");

				// Strip from messages like /tell <player> <msg> the player name, making the check less less annoying.
				if (strippedArgs.length > 2 && (strippedArgs[0].equals("/tell") || strippedArgs[0].equals("/msg")))
					strippedMsg = strippedMsg.replace(strippedArgs[1], "");

				strippedMsg = Common.prepareForSimilarityCheck(strippedMsg);

				if (Common.similarity(strippedMsg, plData.lastCommand) > Settings.AntiSpam.Commands.SIMILARITY) {
					if (Common.hasPerm(pl, Permissions.Bypasses.SIMILAR_COMMANDS))
						break dupeCheck;

					if (Settings.AntiSpam.Commands.WHITELIST_SIMILARITY.contains(args[0].replaceFirst("/", "")))
						break dupeCheck;

					Common.tell(pl, Localization.ANTISPAM_SIMILAR_COMMAND);
					e.setCancelled(true);
					return;
				}
				plData.lastCommand = strippedMsg;
			}

			message = ChatControl.instance().chatCeaser.handleRules(e, pl, message);

			if (e.isCancelled()) // some of the rule or handler has cancelled it
				return;
		}
		
		if (!message.equals(e.getMessage()))
			e.setMessage(message);
			
		if (Settings.Writer.ENABLED && !Settings.Writer.WHITELIST_PLAYERS.contains(pl.getName().toLowerCase())) {
			for (String prikaz : Settings.Writer.INCLUDE_COMMANDS)
				if (message.toLowerCase().startsWith("/" + prikaz.toLowerCase()))
					Writer.zapisatDo(FileType.CHAT_LOG, "[CMD] " + pl.getName(), message);
		}

		if (Settings.SoundNotify.ENABLED_IN_FOLLOWING_COMMANDS.contains(args[0].replaceFirst("/", ""))) {
			if (args.length > 2) {
				Player player = Bukkit.getPlayer(args[1]);
				if (player == null || !player.isOnline())
					return;

				player.playSound(player.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
			} else if (message.startsWith("/r ") || message.startsWith("/reply ")) {
				Player reply = ChatControl.instance().getReplyTo(pl);

				if (reply != null && Common.hasPerm(reply, Permissions.Notify.WHEN_MENTIONED))
					reply.playSound(reply.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
			}
		}
	}
}