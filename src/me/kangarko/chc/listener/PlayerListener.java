package me.kangarko.chc.listener;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.Variables;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;
import me.kangarko.chc.utils.checks.ChecksUtils;

import org.apache.commons.lang3.Validate;
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
		long now = System.currentTimeMillis() / 1000L;

		if (!Common.hasPerm(e.getPlayer(), Permissions.Bypasses.rejoin))
			ChatControl.ipLastLogin.put(e.getPlayer().getAddress().getAddress().getHostAddress(), now);

		ChatControl.getDataFor(e.getPlayer()).loginLocation = e.getPlayer().getLocation();

		if (Variables.needsUpdate && Settings.Updater.NOTIFY)
			for (Player pl : Bukkit.getOnlinePlayers())
				if (Common.hasPerm(pl, Permissions.Notify.plugin_update)) {
					String sprava = Common.colorize(Localization.UPDATE_AVAILABLE).replace("%current", ChatControl.instance().getDescription().getVersion()).replace("%new", Variables.newVersion);
					sprava.split("\n");
					Common.tellTimed(pl, sprava, 5);
				}

		if (Variables.muted && Settings.Mute.SILENT_JOIN) {
			e.setJoinMessage(null);
			return;
		}

		switch (Settings.Messages.JOIN.getType()) {
			case HIDDEN:
				e.setJoinMessage(null);
				break;
			case CUSTOM:
				e.setJoinMessage(Common.colorize(Settings.Messages.JOIN.getMessage().replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName())));
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

		switch (Settings.Messages.QUIT.getType()) {
			case HIDDEN:
				e.setQuitMessage(null);
				break;
			case CUSTOM:
				e.setQuitMessage(Common.colorize(Settings.Messages.QUIT.getMessage().replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName())));
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

		switch (Settings.Messages.KICK.getType()) {
			case HIDDEN:
				e.setLeaveMessage(null);
				break;
			case CUSTOM:
				e.setLeaveMessage(Common.colorize(Settings.Messages.KICK.getMessage().replace("%player", e.getPlayer().getName()).replace("%displayname", e.getPlayer().getDisplayName())));
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

		if (Bukkit.getOnlinePlayers().size() < Settings.MIN_PLAYERS_TO_ENABLE)
			return;

		if (Settings.Signs.CHECK_FOR_ADS) {
			// TODO swear check too?
			String msg = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);

			if (ChecksUtils.advertisingCheck(e.getPlayer(), msg.toLowerCase(), false)) {
				if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.ads))
					return;

				Common.customAction(e.getPlayer(), "Anti_Ad.Custom_Command", msg);
				Common.messages(e.getPlayer(), "&7[" + Localization.Parts.SIGN + "&7] " + ChatColor.WHITE + msg);

				if (Settings.Signs.REWRITE_LINES_WHEN_AD_FOUND) {
					String[] cenzura = Common.colorize(Settings.Signs.REWRITE_TEXT).split(":");
					Validate.isTrue(cenzura.length <= 4, "A sign can only have 4 lines, but rewrite text has: " + cenzura.length);
					
					for (int i = 0; i < cenzura.length; i++) 
						e.setLine(i, cenzura[i]);
				} else
					e.setCancelled(true);
			}
		}
	}
}
