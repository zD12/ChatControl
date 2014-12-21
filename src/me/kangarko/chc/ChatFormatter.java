package me.kangarko.chc;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import me.kangarko.chc.hooks.MultiverseHook;
import me.kangarko.chc.hooks.TownyHook;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatFormatter implements Listener {

	private final Pattern COLOR_REGEX = Pattern.compile("(?i)&([0-9A-F])");
	private final Pattern MAGIC_REGEN = Pattern.compile("(?i)&([K])");
	private final Pattern BOLD_REGEX = Pattern.compile("(?i)&([L])");
	private final Pattern STRIKETHROUGH_REGEX = Pattern.compile("(?i)&([M])");
	private final Pattern UNDERLINE_REGEX = Pattern.compile("(?i)&([N])");
	private final Pattern ITALIC_REGEX = Pattern.compile("(?i)&([O])");
	private final Pattern RESET_REGEX = Pattern.compile("(?i)&([R])");

	private MultiverseHook mvHook;
	private TownyHook townyHook;

	public ChatFormatter() {
		if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null)
			mvHook = new MultiverseHook();

		if (Bukkit.getPluginManager().getPlugin("Towny") != null)
			townyHook = new TownyHook();
	}

	@EventHandler(ignoreCancelled = true)
	public void onChatFormat(AsyncPlayerChatEvent e) {

		Player pl = e.getPlayer();
		String world = pl.getWorld().getName();
		PermissionUser user = PermissionsEx.getPermissionManager().getUser(pl);

		if (user == null)
			return;

		String messageFormat = Settings.Chat.Formatter.FORMAT;
		boolean rangedMode = Settings.Chat.Formatter.RANGED_MODE;
		String theMessage = e.getMessage();

		if (rangedMode && theMessage.startsWith("!") && Common.hasPerm(pl, Permissions.Formatter.globalChat)) {
			rangedMode = false;
			theMessage = theMessage.substring(1);

			messageFormat = Settings.Chat.Formatter.GLOBAL_FORMAT;
		}

		messageFormat = formatColor(messageFormat);

		theMessage = formatColor(theMessage, user, world);

		messageFormat = messageFormat.replace("%message", "%2$s").replace("%displayname", "%1$s");
		messageFormat = replacePlayerVariables(pl, messageFormat);
		messageFormat = replaceTime(messageFormat);

		e.setFormat(messageFormat);
		e.setMessage(theMessage);

		if (rangedMode) {
			double range = Settings.Chat.Formatter.RANGE;

			e.getRecipients().clear();
			e.getRecipients().addAll(getLocalRecipients(pl, messageFormat, range));
		}
	}

	private String replacePlayerVariables(Player pl, String format) {

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(pl);
		String world = pl.getWorld().getName();

		return format.replace("%prefix", formatColor(user.getPrefix(world))).replace("%suffix", formatColor(user.getSuffix(world))).replace("%world", getWorldAlias(world)).replace("%player", pl.getDisplayName()).replace("%group", user.getGroupsNames()[0]).replace("%town", getTown(pl)).replace("%nation", getNation(pl));
	}

	private String getNation(Player pl) {
		if (townyHook == null)
			return "";

		return townyHook.getNation(pl);
	}

	private String getTown(Player pl) {
		if (townyHook == null)
			return "";

		return townyHook.getTownName(pl);
	}

	private List<Player> getLocalRecipients(Player pl, String message, double range) {
		List<Player> recipients = new LinkedList<Player>();
		PermissionManager manager = PermissionsEx.getPermissionManager();
		try {
			Location playerLocation = pl.getLocation();
			double squaredDistance = Math.pow(range, 2.0D);

			for (Player recipient : Bukkit.getServer().getOnlinePlayers())
				if (recipient.getWorld().equals(pl.getWorld()))
					if ((playerLocation.distanceSquared(recipient.getLocation()) <= squaredDistance) || (manager.has(pl, Permissions.Formatter.overrideRanged)))
						recipients.add(recipient);

			return recipients;
		} catch (ArrayIndexOutOfBoundsException ex) {
			Common.Debug("(ChatFormat-rangeChat) Got " + ex.getMessage() + ", trying backup.");

			if (manager.has(pl, Permissions.Formatter.overrideRanged)) {
				for (Player recipient : Bukkit.getServer().getOnlinePlayers()) {
					if (recipient.getWorld().equals(pl.getWorld()))
						recipients.add(recipient);
				}
				return recipients;
			}

			for (Entity en : pl.getNearbyEntities(range, range, range)) {
				if (en.getType() == EntityType.PLAYER)
					recipients.add((Player) en);
			}

			return recipients;
		}
	}

	private String replaceTime(String msg) {
		Calendar c = Calendar.getInstance();

		if (msg.contains("%h"))
			msg = msg.replace("%h", String.format("%02d", c.get(10)));

		if (msg.contains("%H"))
			msg = msg.replace("%H", String.format("%02d", c.get(11)));

		if (msg.contains("%g"))
			msg = msg.replace("%g", Integer.toString(c.get(10)));

		if (msg.contains("%G"))
			msg = msg.replace("%G", Integer.toString(c.get(11)));

		if (msg.contains("%i"))
			msg = msg.replace("%i", String.format("%02d", c.get(12)));

		if (msg.contains("%s"))
			msg = msg.replace("%s", String.format("%02d", c.get(13)));

		if (msg.contains("%a"))
			msg = msg.replace("%a", c.get(9) == 0 ? "am" : "pm");

		if (msg.contains("%A"))
			msg = msg.replace("%A", c.get(9) == 0 ? "AM" : "PM");

		return msg;
	}

	private String formatColor(String string) {
		if (string == null)
			return "";

		String str = string;
		str = COLOR_REGEX.matcher(str).replaceAll("\u00A7$1");
		str = MAGIC_REGEN.matcher(str).replaceAll("\u00A7$1");
		str = BOLD_REGEX.matcher(str).replaceAll("\u00A7$1");
		str = STRIKETHROUGH_REGEX.matcher(str).replaceAll("\u00A7$1");
		str = UNDERLINE_REGEX.matcher(str).replaceAll("\u00A7$1");
		str = ITALIC_REGEX.matcher(str).replaceAll("\u00A7$1");
		str = RESET_REGEX.matcher(str).replaceAll("\u00A7$1");
		return str;
	}

	public String formatColor(String string, PermissionUser user, String worldName) {
		if (string == null)
			return "";

		String str = string;
		if (user.has(Permissions.Formatter.color, worldName))
			str = COLOR_REGEX.matcher(str).replaceAll("\u00A7$1");

		if (user.has(Permissions.Formatter.magic, worldName))
			str = MAGIC_REGEN.matcher(str).replaceAll("\u00A7$1");

		if (user.has(Permissions.Formatter.bold, worldName))
			str = BOLD_REGEX.matcher(str).replaceAll("\u00A7$1");

		if (user.has(Permissions.Formatter.strikethrough, worldName))
			str = STRIKETHROUGH_REGEX.matcher(str).replaceAll("\u00A7$1");

		if (user.has(Permissions.Formatter.underline, worldName))
			str = UNDERLINE_REGEX.matcher(str).replaceAll("\u00A7$1");

		if (user.has(Permissions.Formatter.italic, worldName))
			str = ITALIC_REGEX.matcher(str).replaceAll("\u00A7$1");

		str = RESET_REGEX.matcher(str).replaceAll("\u00A7$1");
		return str;
	}

	private String getWorldAlias(String world) {
		if (mvHook == null)
			return world;

		return mvHook.getColoredAlias(world);
	}
}