package chatcontrol;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Permissions;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class ChatFormatter implements Listener {

	private Pattern COLOR_REGEX = Pattern.compile("(?i)&([0-9A-F])");
	private Pattern MAGIC_REGEN = Pattern.compile("(?i)&([K])");
	private Pattern BOLD_REGEX = Pattern.compile("(?i)&([L])");
	private Pattern STRIKETHROUGH_REGEX = Pattern.compile("(?i)&([M])");
	private Pattern UNDERLINE_REGEX = Pattern.compile("(?i)&([N])");
	private Pattern ITALIC_REGEX = Pattern.compile("(?i)&([O])");
	private Pattern RESET_REGEX = Pattern.compile("(?i)&([R])");

	private MultiverseCore multiVerse;
	
	public ChatFormatter() {
		if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) {
			multiVerse = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
			
			Common.Log("Hooked with Multiverse 2 (World Alias)!");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		Player pl = e.getPlayer();

		String world = pl.getWorld().getName();

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(pl);

		if (user == null) 
			return;

		String messageFormat = user.getOption("message-format", world, ChatControl.Config.getString("Chat_Formatter.Message_Format"));

		boolean localChat = user.getOptionBoolean("force-ranged-mode", world, ChatControl.Config.getBoolean("Chat_Formatter.Ranged_Mode"));

		String theMessage = e.getMessage();

		if ((theMessage.startsWith("!")) && (user.has(Permissions.Formatter.globalChat, world))) {
			localChat = false;
			theMessage = theMessage.substring(1);

			messageFormat = user.getOption("global-message-format", world, ChatControl.Config.getString("Chat_Formatter.Global_Message_Format"));
		}

		messageFormat = formatColor(messageFormat);

		theMessage = formatColor(theMessage, user, world);

		messageFormat = messageFormat.replace("%message", "%2$s").replace("%displayname", "%1$s");
		messageFormat = replacePlayerVariables(pl, messageFormat);
		messageFormat = replaceTime(messageFormat);

		e.setFormat(messageFormat);
		e.setMessage(theMessage);

		if (localChat) {
			double range = user.getOptionDouble("chat-range", world, ChatControl.Config.getDouble("Chat_Formatter.Chat_Range"));

			e.getRecipients().clear();
			e.getRecipients().addAll(getLocalRecipients(pl, messageFormat, range));
		}
	}

	protected void updateDisplayNames() {
		for (Player pl : Bukkit.getServer().getOnlinePlayers())
			updateDisplayName(pl);
	}

	protected void updateDisplayName(Player pl) {
		PermissionUser user = PermissionsEx.getPermissionManager().getUser(pl);

		if (user == null)
			return;

		String world = pl.getWorld().getName();
		pl.setDisplayName(formatColor(replacePlayerVariables(pl, user.getOption("display-name-format", world, "%prefix%player%suffix"))));
	}

	@SuppressWarnings("deprecation")
	protected String replacePlayerVariables(Player pl, String format) {

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(pl);
		String world = pl.getWorld().getName();

		return format.replace("%prefix", formatColor(user.getPrefix(world))).replace("%suffix", formatColor(user.getSuffix(world))).replace("%world", getWorldAlias(world)).replace("%player", pl.getDisplayName()).replace("%group", user.getGroupsNames()[0]);
	}

	protected List<Player> getLocalRecipients(Player pl, String message, double range) {
		Location playerLocation = pl.getLocation();
		List<Player> recipients = new LinkedList<Player>();

		double squaredDistance = Math.pow(range, 2.0D);
		PermissionManager manager = PermissionsEx.getPermissionManager();

		for (Player recipient : Bukkit.getServer().getOnlinePlayers())
			if (recipient.getWorld().equals(pl.getWorld()))
				if ((playerLocation.distanceSquared(recipient.getLocation()) <= squaredDistance) || (manager.has(pl, Permissions.Formatter.overrideRanged)))
					recipients.add(recipient);

		return recipients;
	}

	protected String replaceTime(String msg) {
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

	protected String formatColor(String string) {
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

	protected String formatColor(String string, PermissionUser user, String worldName) {
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
		if (multiVerse != null)
			return getColoredAlias(world);

		return world;
	}

	public String getColoredAlias(String world) {
		MultiverseWorld mvWorld = multiVerse.getMVWorldManager().getMVWorld(world);
		
		if (mvWorld != null)
			return mvWorld.getColoredWorldString();
		
		return world;
	}
}