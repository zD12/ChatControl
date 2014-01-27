package chatcontrol.Config;

public enum FileType {

	CHAT("chat.yml"),
	CONSOLE("console.yml");

	public String string;

	FileType(String str) {
		string = str;
	}

	public String getName() {
		return string;
	}

}
