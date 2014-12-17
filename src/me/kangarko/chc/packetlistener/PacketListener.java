package me.kangarko.chc.packetlistener;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Permissions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class PacketListener {

	public static void initPacketListener(){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Client.TAB_COMPLETE){
			@Override
			public void onPacketReceiving(PacketEvent e){
				if (e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
					if (e.getPlayer().hasPermission(Permissions.Bypasses.tab_complete) || e.getPlayer().isOp())
						return;
					
					String message = e.getPacket().getStrings().read(0);
					if ((message.startsWith("/")) && (!message.contains(" ")))
						e.setCancelled(true);
					
				}
			}

		});
	}
}
