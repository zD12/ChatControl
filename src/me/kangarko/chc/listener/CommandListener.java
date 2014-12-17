package me.kangarko.chc.listener;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.Variables;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;
import me.kangarko.chc.utils.Writer;
import me.kangarko.chc.utils.Writer.TypSuboru;
import me.kangarko.chc.utils.checks.ChecksUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (Bukkit.getOnlinePlayers().length < Settings.MIN_PLAYERS_TO_ENABLE)
			return;

		Player pl = e.getPlayer();

		if (!Common.hasPerm(pl, Permissions.Bypasses.global_perm)) {
			if (Variables.muted) {
				if (pl.hasPermission(Permissions.Bypasses.mute))
					return;

				if (Settings.Mute.DISABLED_CMDS_WHEN_MUTED.contains(e.getMessage().split(" ")[0])) { // TODO check if working
					Common.tell(pl, Localization.CANNOT_COMMAND_WHILE_MUTED);
					e.setCancelled(true);
					return;
				}
			}

			long cas = System.currentTimeMillis() / 1000L;

			timeCheck: if ((cas - ChatControl.getDataFor(pl).lastCommandTime) < Settings.Commands.TIME_DELAY) {
				if (pl.hasPermission(Permissions.Bypasses.timeCmd))
					break timeCheck;

				if (Settings.Commands.WHITELIST_TIME.contains(e.getMessage().split(" ")[0]))
					break timeCheck;

				Common.tell(pl, Localization.COMMAND_WAIT_MESSAGE.replace("%time", String.valueOf(Settings.Commands.TIME_DELAY - (cas - ChatControl.getDataFor(pl).lastCommandTime))));
				e.setCancelled(true);
				return;
			} else {
				ChatControl.getDataFor(pl).lastCommandTime = cas;
			}

			dupeCheck: if (Settings.Commands.BLOCK_SIMILAR_MORE_THAN > 0) {
				String sprava = e.getMessage().toLowerCase();
				String[] args = sprava.split(" ");

				// Strip from messages like /tell <player> <msg> the player name, making the check less less annoying.
				if (args.length > 2 && (args[0].equals("/tell") || args[0].equals("/msg")))
					sprava = sprava.replace(args[1], "");

				if (Settings.Commands.STRIP_UNICODE_IN_CHECKS)
					sprava = Common.stripSpecialCharacters(sprava);

				if (ChecksUtils.similarityCheck(sprava, ChatControl.getDataFor(pl).lastCommand) > Settings.Commands.BLOCK_SIMILAR_MORE_THAN) {
					if (pl.hasPermission(Permissions.Bypasses.dupeCmd))
						break dupeCheck;

					if (Settings.Commands.WHITELIST_DUPLICATION.contains(e.getMessage().split(" ")[0]))
						break dupeCheck;

					Common.tell(pl, Localization.ANTISPAM_SIMILAR_COMMAND);
					e.setCancelled(true);
					return;
				}
				ChatControl.getDataFor(pl).lastCommand = sprava;
			}

			adCheck: if (Settings.Commands.CHECK_FOR_ADS && !pl.hasPermission(Permissions.Bypasses.ads)) {
				if (ChecksUtils.advertisingCheck(pl, e.getMessage(), true)) {
					for (String whitelist : Settings.Commands.WHITELIST_ADS)
						if (e.getMessage().startsWith("/" + whitelist))
							break adCheck;

					Common.customAction(pl, "Anti_Ad.Custom_Command", e.getMessage());
					Common.messages(pl, e.getMessage());
					e.setCancelled(true);
				}
			}

			swearCheck: if (Settings.Commands.CHECK_FOR_SWEARS && !pl.hasPermission(Permissions.Bypasses.swear)) {
				for (String ignoredCmd : Settings.Commands.WHITELIST_SWEAR)
					if (e.getMessage().startsWith("/" + ignoredCmd))
						break swearCheck;

				String finalMessage = ChecksUtils.swearCheck(pl, e.getMessage(), Common.prepareForSwearCheck(e.getMessage()));

				if (finalMessage != e.getMessage()) {
					if (Settings.AntiSwear.BLOCK_MESSAGE)
						e.setCancelled(true);

					else if (Settings.AntiSwear.REPLACE_MESSAGE)
						e.setMessage(finalMessage);

				}
			}
		}

		if (Settings.Writer.ENABLED && !Settings.Writer.WHITELIST_PLAYERS.contains(pl.getName().toLowerCase())) {
			for (String prikaz : Settings.Writer.WHITELIST_COMMANDS)
				if (e.getMessage().toLowerCase().startsWith("/" + prikaz.toLowerCase()))
					Writer.zapisatDo(TypSuboru.ZAZNAM_CHATU, "[CMD] " + pl.getName(), e.getMessage());
		}

		if (Settings.SoundNotify.ENABLED_IN_COMMANDS) {
			String[] args = e.getMessage().split(" ");

			if (Settings.SoundNotify.ENABLED_IN_FOLLOWING_COMMANDS.contains(args[0].replaceFirst("/", ""))) {

				if (args.length > 2) {
					Player player = Bukkit.getPlayer(args[1]);
					if (player == null || !player.isOnline())
						return;

					player.playSound(player.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
				} else if (e.getMessage().startsWith("/r ") || e.getMessage().startsWith("/reply ")) {
					Player reply = ChatControl.instance().getReplyTo(pl);

					if (reply != null && reply.hasPermission(Permissions.Notify.whenMentioned))
						reply.playSound(reply.getLocation(), Settings.SoundNotify.SOUND.sound, Settings.SoundNotify.SOUND.volume, Settings.SoundNotify.SOUND.pitch);
				}
			}
		}
	}
}