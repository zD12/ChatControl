package me.kangarko.chc;

import static me.kangarko.chc.packetlistener.PacketListener.initPacketListener;

import java.util.logging.Filter;
import java.util.logging.Level;

import me.kangarko.chc.cmd.CommandsHandler;
import me.kangarko.chc.filter.ConsoleFilter;
import me.kangarko.chc.filter.Log4jFilter;
import me.kangarko.chc.listener.ChatListener;
import me.kangarko.chc.listener.CommandListener;
import me.kangarko.chc.listener.PlayerListener;
import me.kangarko.chc.model.ConfHelper;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.SettingsConsole;
import me.kangarko.chc.model.Variables;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.checks.UpdateCheck;
import me.kangarko.chc.utils.safety.SafeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;

@SuppressWarnings("deprecation")
// TODO common.class overhaul, catch up all broadcast from bukkit and colorize it
// TODO implement death messages
// TODO implement timed messages
// TODO replace remap and pwnfilter
// TODO replace hasPermission with Common. hasPerm
public class ChatControl extends JavaPlugin {

	private static ChatControl instance;

	// Player IP, Time
	public static SafeMap<String, Long> ipLastLogin = new SafeMap<>();

	// Player Name, Player Cache
	private static SafeMap<String, PlayerCache> playerData = new SafeMap<>();

	private Essentials ess;

	public void onEnable() {
		instance = this;

		try {
			ConfHelper.loadAll();
		} catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Error loading configuration, plugin disabled.", t);
			getPluginLoader().disablePlugin(this);
			return;
		}

		if (Common.debugEnabled()) {
			ipLastLogin.displayWarnings();
			playerData.displayWarnings();
		}

		for (Player pl : getServer().getOnlinePlayers())
			createDataIfNotExistFor(pl.getName());

		if (Bukkit.getPluginManager().getPlugin("Essentials") != null)
			ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);

		if (SettingsConsole.filterEnabled || SettingsConsole.filterColorToAnsi) {
			try {
				new Log4jFilter().init();
				Common.debug("Console filtering now using Log4j Filter.");
			} catch (Throwable t) {
				Filter filter = new ConsoleFilter();
				if (SettingsConsole.filterPluginMessages)
					for (Plugin p : getServer().getPluginManager().getPlugins())
						p.getLogger().setFilter(filter);

				Bukkit.getLogger().setFilter(filter);
				Common.debug("Console filtering initiated (MC 1.6.4 and lower).");
			}
		}

		if (Settings.Packets.PREVENT_TAB_COMPLETE) {
			if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
				getLogger().warning("*** Cannot enable tab complete!");
				getLogger().warning("Required plugin missing: ProtocolLib");
				Thread.dumpStack();
			} else {
				initPacketListener();
				Common.Log("Hooked with ProtocolLib (TAB complete listener)!");
			}
		}

		if (Settings.Chat.Formatter.ENABLED) {
			if (getServer().getPluginManager().getPlugin("PermissionsEx") == null) {
				getLogger().warning("You need PermissionEx to enable ChatFormatter.");
				Thread.dumpStack();
			} else {
				if (getServer().getPluginManager().getPlugin("ChatManager") != null) {
					getLogger().severe("Detected ChatManager, please copy settings from it to ChatControl and remove it then!");
					Thread.dumpStack();
				} else
					getServer().getPluginManager().registerEvents(new ChatFormatter(), this);
				Common.Log("Hooked with PermissionsEx (Chat Formatter)!");
			}
		}

		getCommand("chatcontrol").setExecutor(new CommandsHandler());

		if (Settings.Updater.ENABLED)
			getServer().getScheduler().runTaskAsynchronously(this, new UpdateCheck("https://raw.github.com/kangarko/ChatControl/master/plugin.yml"));

		boolean removeMe;
		getLogger().severe("**********************************");
		getLogger().severe("WARNING: This version of plugin is incomplete, broken and NOT WORKING! Downgrade to 4x");
		getLogger().severe("**********************************");
	}

	public void onDisable() {
		playerData.clear();
		ipLastLogin.clear();

		Variables.reset();

		ess = null;
		instance = null;
	}

	public boolean checkForAfk(String name) {
		if (ess == null)
			return true;

		if (!Settings.SoundNotify.ONLY_WHEN_AFK)
			return true;

		if (ess.getUserMap().getUser(name) == null)
			return true;

		return ess.getUserMap().getUser(name).isAfk();
	}

	public Player getReplyTo(Player pl) {
		if (ess == null)
			return null;

		CommandSource cmdSource = ess.getUserMap().getUser(pl.getName()).getReplyTo();
		if (cmdSource == null || !cmdSource.isPlayer())
			return null;

		Player source = cmdSource.getPlayer();
		if (source == null || !source.isOnline())
			return null;

		return source;
	}

	public static void createDataIfNotExistFor(String pl) {
		playerData.putIfAbsent(pl, new PlayerCache());
	}

	public static PlayerCache getDataFor(Player pl) {
		return getDataFor(pl.getName());
	}

	public static PlayerCache getDataFor(String pl) {
		createDataIfNotExistFor(pl);

		return playerData.get(pl);
	}

	public static ChatControl instance() {
		if (instance == null) {
			instance = new ChatControl();

			Common.Warn("ChatControl instance is null! Was the plugin reloaded?");
		}

		return instance;
	}
}
