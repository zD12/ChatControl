package me.kangarko.chc.packetlistener;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class PacketListener {

	/**
	 * @deprecated consider remove, spigot has it already in spigot.yml and it's more advanced
	 */
	public static void initPacketListener(){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Client.TAB_COMPLETE){
			@Override
			public void onPacketReceiving(PacketEvent e){
				if (e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
					if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.tab_complete))
						return;
					
					String message = e.getPacket().getStrings().read(0);
					if ((message.startsWith("/")) && (!message.contains(" ")))
						e.setCancelled(true);
					
				}
			}

		});
	}
}
