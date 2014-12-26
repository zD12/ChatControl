package kangarko.chatcontrol.hooks;

import kangarko.chatcontrol.ChatControl;
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
		manager.addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Client.TAB_COMPLETE) {

			@Override
			public void onPacketReceiving(PacketEvent e) {
				if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.TAB_COMPLETE))
					return;

				String msg = e.getPacket().getStrings().read(0);

				if (msg.startsWith("/") && !msg.contains(" "))
					e.setCancelled(true);
			}
		});
	}
}
