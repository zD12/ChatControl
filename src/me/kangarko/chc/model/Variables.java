package me.kangarko.chc.model;

// TODO consider move to Settings
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
