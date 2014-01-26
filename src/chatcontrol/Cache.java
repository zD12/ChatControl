package chatcontrol;

import java.util.ArrayList;
import java.util.List;

public class Cache {

	public static List<String> swear_db = new ArrayList<String>();
	
	public static void load() {
		if(ChatControl.Config.getBoolean("Anti_Swear.Enabled")) {
			for (String swear : ChatControl.Config.getStringList("Anti_Swear.Word_List")) {
				Cache.swear_db.add(swear);
			}
		}
	}
	
	public static void clear() {
		swear_db.clear();
	}
}
