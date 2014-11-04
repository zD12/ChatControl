package chatcontrol;

import static chatcontrol.PacketListener.PacketListener.initPacketListener;

import java.util.logging.Filter;

import kangarko.api.util.safety.Mapa;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import chatcontrol.Config.CustomConfig;
import chatcontrol.Config.FileType;
import chatcontrol.ConsoleFilter.ConsoleFilter;
import chatcontrol.ConsoleFilter.Log4jFilter;
import chatcontrol.Listener.ChatListener;
import chatcontrol.Listener.CommandListener;
import chatcontrol.Listener.PlayerListener;
import chatcontrol.Utils.Common;
import chatcontrol.Utils.Checks.ConfigUpdateCheck;
import chatcontrol.Utils.Checks.UpdateCheck;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;

@SuppressWarnings("deprecation")
public class ChatControl extends JavaPlugin {

	public static ChatControl plugin;
	public static boolean needsUpdate = false;
	public static String newVersion;

	public static FileConfiguration Config;
	public static CustomConfig ChatConfig = new CustomConfig(FileType.CHAT);
	public static CustomConfig ConsoleConfig = new CustomConfig(FileType.CONSOLE);

	// Player IP, Time
	public static Mapa<String, Long> ipLastLogin = new Mapa<>();
	
	// Player Name, Player Cache
	private static Mapa<String, PlayerCache> playerData = new Mapa<>();

	public static boolean muted = false;

	private Essentials ess;
	
	public void onEnable(){		
		plugin = this;
		Config = getConfig();

		if (Common.debugEnabled()) {
			ipLastLogin.zobrazovavatVarovania();
			playerData.zobrazovavatVarovania();
		}
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		ChatConfig.saveDefaultConfig();
		ConsoleConfig.saveDefaultConfig();

		ConfigUpdateCheck.configCheck();

		for (Player pl : getServer().getOnlinePlayers())
			createDataIfNotExistFor(pl.getName());

		if (Bukkit.getPluginManager().getPlugin("Essentials") != null)
			ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);


		if(ConsoleConfig.getConfig().getBoolean("Console.Filter_Enabled") || ConsoleConfig.getConfig().getBoolean("Console.Correct_Color_Codes")) {
			if(getServer().getBukkitVersion().startsWith("1.7")) {
				new Log4jFilter().init();
				Common.debug("Console filtering now using Log4j Filter.");
			} else {
				Filter filter = new ConsoleFilter();
				if(ConsoleConfig.getConfig().getBoolean("Console.Filter_Plugin_Messages"))
					for (Plugin p : getServer().getPluginManager().getPlugins())
						p.getLogger().setFilter(filter);

				Bukkit.getLogger().setFilter(filter);
				Common.debug("Console filtering initiated (MC 1.6.4 and lower).");
			}
		}

		if(getConfig().getBoolean("Protect.Prevent_Tab_Complete")){
			if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
				getLogger().warning("In order to prevent tab complete you need ProtocolLib.");
				getConfig().set("Protect.Prevent_Tab_Complete", false);
				saveConfig();
			} else {
				initPacketListener();
				Common.Log("Hooked with ProtocolLib (TAB complete listener)!");
			}
		}

		if(getConfig().getBoolean("Chat_Formatter.Enabled")) {
			if (getServer().getPluginManager().getPlugin("PermissionsEx") == null) {
				getLogger().warning("You need PermissionEx to enable ChatFormatter.");
				Thread.dumpStack();
			} else {
				if (getServer().getPluginManager().getPlugin("ChatManager") != null) 
					getLogger().severe("Detected ChatManager, please copy settings from it to ChatControl and remove it then!");
				else 
					getServer().getPluginManager().registerEvents(new ChatFormatter(), this);
				Common.Log("Hooked with PermissionsEx (Chat Formatter)!");
			}
		}

		getCommand("chatcontrol").setExecutor(new CommandsHandler());

		if(getConfig().getBoolean("Miscellaneous.Check_For_Updates"))
			getServer().getScheduler().runTaskAsynchronously(this, new UpdateCheck("https://raw.github.com/kangarko/ChatControl/master/plugin.yml"));
	}

	public void onDisable() {
		playerData.clear();
		ipLastLogin.clear();

		muted = false;
		needsUpdate = false;

		ess = null;
		plugin = null;
		Config = null;
	}

	public boolean checkForAfk(String name) {
		if(ess == null)
			return true;

		if(!getConfig().getBoolean("Chat.Notify_Player_When_Mentioned.Only_Notify_When_Afk"))
			return true;

		if(ess.getUserMap().getUser(name) == null)
			return true;

		return ess.getUserMap().getUser(name).isAfk();
	}

	public Player getReplyTo(Player pl) {
		if(ess == null)
			return null;

		CommandSource cmdSource = ess.getUserMap().getUser(pl.getName()).getReplyTo();
		if(cmdSource == null || !cmdSource.isPlayer())
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
}
