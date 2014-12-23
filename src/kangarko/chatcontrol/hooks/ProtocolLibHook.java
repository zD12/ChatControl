package kangarko.chatcontrol.hooks;

import java.io.File;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class ProtocolLibHook {

	private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

	public static void init() {
		/**
		 * TODO colored nick over head and in tab
		 */
		/*manager.addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
			public void onPacketSending(PacketEvent e) {
				WrapperPlayServerNamedEntitySpawn entity = new WrapperPlayServerNamedEntitySpawn(e.getPacket());
				String name = entity.getPlayerName();

				if (name == null || name.length() > 14)
					return;

				Player pl = Bukkit.getPlayer(name);

				if (pl == null)
					return;

				if (pl.hasPermission("chatcontrol.color.darkred"))
					entity.setPlayerName(ChatColor.DARK_RED + pl.getName());

				else if (pl.hasPermission("chatcontrol.color.darkgreen"))
					entity.setPlayerName(ChatColor.DARK_GREEN + pl.getName());

				else if (pl.hasPermission("chatcontrol.color.blue"))
					entity.setPlayerName(ChatColor.BLUE + pl.getName());

				else if (pl.hasPermission("chatcontrol.color.darkaqua"))
					entity.setPlayerName(ChatColor.DARK_AQUA + pl.getName());

				else if (pl.hasPermission("chatcontrol.darkpurple"))
					entity.setPlayerName(ChatColor.DARK_PURPLE + pl.getName());

				else if (pl.hasPermission("chatcontrol.yellow"))
					entity.setPlayerName(ChatColor.YELLOW + pl.getName());
			}
		});*/

		if (Settings.Packets.DISABLE_TAB_COMPLETE) {
			if (new File("spigot.yml").exists()) {
				Common.Log(Common.consoleLine());
				Common.Log(" &aIf you want to disable tab complete, set");
				Common.Log(" &bcommands.tab-complete &ato 0 in &fspigot.yml &afile.");
				Common.Log(" &aFunction in ChatControl was disabled.");
				Common.Log(Common.consoleLine());
				return;
			}
			
			manager.addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Client.TAB_COMPLETE){
				@Override
				public void onPacketReceiving(PacketEvent e){					
					if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.TAB_COMPLETE))
						return;

					String msg = e.getPacket().getStrings().read(0);

					if (msg.startsWith("/") && !msg.contains(" "))
						e.setCancelled(true);
				}
			});
		}
	}
}
