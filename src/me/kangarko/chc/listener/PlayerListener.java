package me.kangarko.chc.listener;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.Variables;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;
import me.kangarko.chc.utils.checks.ChecksUtils;

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

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		long now = System.currentTimeMillis() / 1000L;
		String ip = e.getAddress().getHostAddress();

		if (ChatControl.ipLastLogin.containsKey(ip) && ChatControl.ipLastLogin.get(ip) > Settings.AntiBot.REJOIN_TIME) {
			if ((Settings.AntiBot.REJOIN_TIME - (now - ChatControl.ipLastLogin.get(ip))) <= 0)
				return;

			String msg = Common.colorize(Localization.ANTIBOT_REJOIN_TOO_QUICKLY.replace("%time", String.valueOf(Settings.AntiBot.REJOIN_TIME - (now - ChatControl.ipLastLogin.get(ip)))));
			msg.split("\n");
			e.disallow(Result.KICK_OTHER, msg);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		ChatControl.createDataIfNotExistFor(e.getPlayer().getName());
		long now = System.currentTimeMillis() / 1000L;

		if (!e.getPlayer().isOp() && !e.getPlayer().hasPermission(Permissions.Bypasses.rejoin))
			ChatControl.ipLastLogin.put(e.getPlayer().getAddress().getAddress().getHostAddress(), now);

		ChatControl.getDataFor(e.getPlayer()).loginLocation = e.getPlayer().getLocation();

		if (Variables.needsUpdate && Settings.Updater.NOTIFY) {

			for (Player pl : Bukkit.getOnlinePlayers()) {

				if (pl.isOp() || pl.hasPermission(Permissions.Notify.plugin_update)) {
					String sprava = Common.colorize(Localization.UPDATE_AVAILABLE).replace("%current", ChatControl.instance().getDescription().getVersion()).replace("%new", Variables.newVersion);
					sprava.split("\n");
					Common.tellTimed(pl, sprava, 5);
				}
			}
		}

		if (Variables.muted && Settings.Mute.SILENT_JOIN) {
			e.setJoinMessage(null);
			return;
		}

		switch (Settings.Messages.JOIN) {
			case HIDDEN:
				e.setJoinMessage(null);
				break;
			case CUSTOM:
				e.setJoinMessage(Common.colorize(Settings.Messages.JOIN.getMsg().replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName()).replace("%prefix", Common.prefix())));
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (Variables.muted && Settings.Mute.SILENT_QUIT) {
			e.setQuitMessage(null);
			return;
		}

		switch (Settings.Messages.QUIT) {
			case HIDDEN:
				e.setQuitMessage(null);
				break;
			case CUSTOM:
				e.setQuitMessage(Common.colorize(Settings.Messages.QUIT.getMsg().replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName()).replace("%prefix", Common.prefix())));
				break;
			default:
				break;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onKick(PlayerKickEvent e) {
		if (Variables.muted && Settings.Mute.SILENT_KICK) {
			e.setLeaveMessage(null);
			return;
		}

		switch (Settings.Messages.KICK) {
			case HIDDEN:
				e.setLeaveMessage(null);
				break;
			case CUSTOM:
				e.setLeaveMessage(Common.colorize(Settings.Messages.KICK.getMsg().replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName()).replace("%prefix", Common.prefix())));
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (Variables.muted && Settings.Mute.SILENT_DEATHS)
			e.setDeathMessage(null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.global_perm))
			return;

		if (Bukkit.getOnlinePlayers().length < Settings.MIN_PLAYERS_TO_ENABLE)
			return;
		
		if (Settings.Signs.CHECK_FOR_DUPLICATION) {
			if (e.getPlayer().hasPermission(Permissions.Bypasses.dupeSigns))
				return;

			if (ChatControl.getDataFor(e.getPlayer()).lastSignText.equals(e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3))) {
				Common.tell(e.getPlayer(), Localization.ANTIBOT_DUPE_SIGN);
				e.setCancelled(true);
				return;
			}
			ChatControl.getDataFor(e.getPlayer()).lastSignText = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);
		}

		if (Settings.Signs.CHECK_FOR_ADS) {
			// TODO swear check too?
			String msg = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);

			if (ChecksUtils.advertisingCheck(e.getPlayer(), msg.toLowerCase(), false)) {
				if (e.getPlayer().hasPermission(Permissions.Bypasses.ads))
					return;

				Common.customAction(e.getPlayer(), "Anti_Ad.Custom_Command", msg);
				Common.messages(e.getPlayer(), ChatColor.GRAY + "[SIGN] " + ChatColor.WHITE + msg);

				if (Settings.Signs.REWRITE_LINES_WHEN_AD_FOUND) {
					String[] cenzura = Common.colorize(Settings.Signs.REWRITE_TEXT).split(":");
					try {
						e.setLine(0, cenzura[0]);
						e.setLine(1, cenzura[1]);
						e.setLine(2, cenzura[2]);
						e.setLine(3, cenzura[3]);
					} catch (Exception ex) {
					} // TODO this is crappy
				} else
					e.setCancelled(true);
			}
		}
	}
}
