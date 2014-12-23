package kangarko.chatcontrol.model;

// TODO consider move to Settings
// TODO fix unknown class error when reloading
public class Variables {

	public static boolean muted = false;
	public static boolean needsUpdate = false;
	public static String newVersion;
	
	public static void reset() {
		muted = false;
		needsUpdate = false;
		newVersion = null;
	}
}
