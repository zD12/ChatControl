package chatcontrol.PacketListener;

import chatcontrol.ChatControl;
import chatcontrol.Misc.Permissions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class PacketListener {

	public void initPacketListener(){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ChatControl.plugin, PacketType.Play.Client.TAB_COMPLETE){
			@Override
			public void onPacketReceiving(PacketEvent e){
				if (e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
					if (e.getPlayer().hasPermission(Permissions.Bypasses.tab_complete) || e.getPlayer().isOp()){
						return;
					}
					String message = e.getPacket().getStrings().read(0);
					if ((message.startsWith("/")) && (!message.contains(" "))){
						e.setCancelled(true);
					}
				}	
			}

		});
	}

	@SuppressWarnings("deprecation")
	public void initOlderPacketListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ChatControl.plugin, ConnectionSide.CLIENT_SIDE, ListenerPriority.NORMAL, 203) {
			public void onPacketReceiving(PacketEvent e) {
				if (e.getPacketID() == 203) {
					
						if (e.getPlayer().hasPermission(Permissions.Bypasses.tab_complete) || e.getPlayer().isOp()) {
							return;
						}
						PacketContainer packet = e.getPacket();
						String message = (String)packet.getSpecificModifier(String.class).read(0);

						if (message.startsWith("/") && !message.contains(" ")) {
							e.setCancelled(true);
						}
				}
			}
		});
	}
}
