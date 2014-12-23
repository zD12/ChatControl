package kangarko.chatcontrol.utils;

public class MissingResourceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public final String file;
	
	public MissingResourceException(String msg, String file) {
		super(msg.replace("%file", file));
		this.file = file;
	}
}
