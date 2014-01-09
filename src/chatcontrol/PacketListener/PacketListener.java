package chatcontrol.PacketListener;

import chatcontrol.ChatControl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class PacketListener {

	public void initPacketListener(){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ChatControl.plugin, PacketType.Play.Client.CHAT){
			@Override
			public void onPacketReceiving(PacketEvent e){
				if (e.getPacketType() == PacketType.Play.Client.CHAT)
					
						if (e.getPlayer().hasPermission("chatcontrol.bypass.tabcomplete") || e.getPlayer().hasPermission("chatcontrol.admin") || e.getPlayer().isOp()){
							return;
						}
				
						PacketContainer packet = e.getPacket();
						String message = packet.getSpecificModifier(String.class).read(0);

						if ((message.startsWith("/")) && (!message.contains(" "))){
							e.setCancelled(true);
						}

			}
		});
	}
}
