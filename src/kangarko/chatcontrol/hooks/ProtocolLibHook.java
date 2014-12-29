package kangarko.chatcontrol.hooks;

import java.io.File;
import java.util.Map.Entry;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.model.SettingsRemap;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ProtocolLibHook {

	private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	private static final JSONParser parser = new JSONParser();

	public static void init() {
		if (Settings.Packets.DISABLE_TAB_COMPLETE) {
			if (new File("spigot.yml").exists())
				Common.LogInFrame(false, "&aIf you want to disable tab complete, set", "&bcommands.tab-complete &ato 0 in &fspigot.yml &afile.", "&aFunction in ChatControl was disabled.");
			else {
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

		manager.addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Server.CHAT) {

			@Override
			public void onPacketSending(PacketEvent e) {
				StructureModifier<WrappedChatComponent> chat = e.getPacket().getChatComponents();

				try {
					JSONObject json = (JSONObject) parser.parse(chat.read(0).getJson());
					replaceAll(json);

					chat.write(0, WrappedChatComponent.fromJson(json.toJSONString()));
				} catch (ParseException ex) {
					Common.Error("Unable to parse chat packet", ex);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static void replaceAll(Object input) {
		if (input instanceof JSONObject) {
			JSONObject objects = (JSONObject) input;

			for (Object key : objects.keySet()) {
				Object value = objects.get(key);

				if (value instanceof JSONObject) 
					replaceAll((JSONObject) value);

				else if (value instanceof JSONArray)
					replaceAll((JSONArray) value);

				else if (value instanceof String)
					objects.put(key, replace(value.toString()));
			}

		} else if (input instanceof JSONArray) {
			JSONArray array = (JSONArray) input;

			for (int i = 0; i < array.size(); i++) {
				Object value = array.get(i);

				if (value instanceof JSONObject)
					replaceAll((JSONObject) value);

				else if (value instanceof JSONArray)
					replaceAll((JSONArray) value);

				else if (value instanceof String) 
					array.set(i, replace(value.toString()));
			}
		} else
			System.out.println("Skipping unknown object: " + input.getClass().getTypeName());
	}

	private static String replace(String msg) {
		for (Entry<String, String> entry : SettingsRemap.REPLACE_PROTOCOL_MAP.entrySet())
			msg = msg.replaceAll(entry.getKey(), entry.getValue());

		return msg;
	}
}
