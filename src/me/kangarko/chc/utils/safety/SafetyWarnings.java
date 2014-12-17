package me.kangarko.chc.utils.safety;

import me.kangarko.chc.utils.Common;


abstract class SafetyWarnings {
	
	protected boolean warn = false;
	
	protected SafetyWarnings() {		
	}
	
	public void displayWarnings() {
		warn = true;
	}
	
	protected void warn(String msg) {
		if (warn)
			Common.Warn("[" + getClass().getSimpleName() + "] " + msg);
	}
}
