package kangarko.chatcontrol.utils;

import kangarko.chatcontrol.model.Localization;



public class InsufficientPermissionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InsufficientPermissionException(String perm) {
		super(Localization.NO_PERMISSION.replace("%perm", perm));
	}
}
