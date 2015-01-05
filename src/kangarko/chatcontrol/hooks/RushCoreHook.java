package kangarko.chatcontrol.hooks;

import kangarko.rushcore.misc.PlayerInfo;


// A hook with my custom plugin on my server.
public class RushCoreHook {

	public static boolean zapnute = false;
	
	public static boolean moznoPrehratPre(String hraca) {
		if (!zapnute)
			return true;
		
		return PlayerInfo.of(hraca).zvukRp;
	}
}
