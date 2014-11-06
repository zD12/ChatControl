package chatcontrol.Utils.Safety;

import chatcontrol.Utils.Common;


abstract class BezpecnostneVarovania {
	
	protected boolean varovat = false;
	
	protected BezpecnostneVarovania() {		
	}
	
	public void zobrazovavatVarovania() {
		varovat = true;
	}
	
	protected void varovat(String msg) {
		if (varovat)
			Common.Warn("[" + getClass().getSimpleName() + "] " + msg);
	}
}
