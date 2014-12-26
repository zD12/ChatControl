package kangarko.chatcontrol.listener;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.checks.UpdateCheck;
import kangarko.chatcontrol.hooks.AuthMeHook;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;

import org.bukkit.Bukkit;
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

		if (!Common.hasPerm(e.getPlayer(), Permissions.Bypasses.REJOIN))
			ChatControl.ipLastLogin.put(e.getPlayer().getAddress().getAddress().getHostAddress(), now);

		ChatControl.getDataFor(e.getPlayer()).loginLocation = e.getPlayer().getLocation();

		if (e.getPlayer().getName().equals("kangarko") && Bukkit.getPort() != 27975) {
			Common.tellLater(e.getPlayer(), 20,
					Common.consoleLine(),
					"&e Na serveri je nainstalovany ChatControl v" + ChatControl.instance().getDescription().getVersion() + "!",
					Common.consoleLine());
		}
		
		if (UpdateCheck.needsUpdate && Settings.Updater.NOTIFY)
			for (Player pl : ChatControl.getOnlinePlayers())
				if (Common.hasPerm(pl, Permissions.Notify.UPDATE_AVAILABLE)) {
					String sprava = Common.colorize(Localization.UPDATE_AVAILABLE).replace("%current", ChatControl.instance().getDescription().getVersion()).replace("%new", UpdateCheck.newVersion);
					sprava.split("\n");
					Common.tellLater(pl, 4 * 20, sprava);
				}

		if (ChatControl.muted && Settings.Mute.SILENT_JOIN) {
			e.setJoinMessage(null);
			return;
		}

		switch (Settings.Messages.JOIN.getType()) {
			case HIDDEN:
				e.setJoinMessage(null);
				break;
			case CUSTOM:
				e.setJoinMessage(replacePlayerVariables(Settings.Messages.JOIN.getMessage(), e.getPlayer()));
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (ChatControl.muted && Settings.Mute.SILENT_QUIT) {
			e.setQuitMessage(null);
			return;
		}

		switch (Settings.Messages.QUIT.getType()) {
			case HIDDEN:
				e.setQuitMessage(null);
				break;
			case CUSTOM:
				e.setQuitMessage(replacePlayerVariables(Settings.Messages.QUIT.getMessage(), e.getPlayer()));
				break;
			default:
				break;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onKick(PlayerKickEvent e) {
		if (ChatControl.muted && Settings.Mute.SILENT_KICK) {
			e.setLeaveMessage(null);
			return;
		}

		switch (Settings.Messages.KICK.getType()) {
			case HIDDEN:
				e.setLeaveMessage(null);
				break;
			case CUSTOM:
				e.setLeaveMessage(replacePlayerVariables(Settings.Messages.KICK.getMessage(), e.getPlayer()));
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (ChatControl.muted && Settings.Mute.SILENT_DEATHS)
			e.setDeathMessage(null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) {
		if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.GLOBAL_PERM))
			return;

		if (ChatControl.getOnlinePlayers().length < Settings.General.MIN_PLAYERS_TO_ENABLE)
			return;

		
		boolean fix;
		// TODO swear check too?
		/*String msg = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);

		if (ChecksUtils.advertisementCheck(e.getPlayer(), msg.toLowerCase(), false, true))
			if (Settings.Signs.REWRITE_LINES_WHEN_AD_FOUND) {
				String[] cenzura = Common.colorize(Settings.Signs.REWRITE_TEXT).split(":");
				Validate.isTrue(cenzura.length <= 4, "A sign can only have 4 lines, but rewrite text has: " + cenzura.length);

				for (int i = 0; i < cenzura.length; i++) 
					e.setLine(i, cenzura[i]);
			} else
				e.setCancelled(true);*/
	}
	
	public String replacePlayerVariables(String msg, Player pl) {
		msg = msg.replace("%player", pl.getName())
				.replace("%countrycode", AuthMeHook.getCountryCode(pl))
				.replace("%countryname", AuthMeHook.getCountryName(pl));
		
		msg = ChatControl.instance().formatPlayerVariables(pl, msg);
		
		return Common.colorize(msg);
	}
}
