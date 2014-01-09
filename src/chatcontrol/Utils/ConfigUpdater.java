package chatcontrol.Utils;

import java.io.File;

import chatcontrol.ChatControl;

public class ConfigUpdater {

	public static Status status;
	public static String latestVersion = ChatControl.description.getVersion();

	public enum Status {
		SUCCESS("§aConfiguration was updated for version " + ChatControl.description.getVersion()),
		ERROR("&c Config was NOT updated! Please regenerate it."),
		TOO_OLD("§4Your ChatControl version is too old. We cannot update your configuration. Consider regenerating your config.yml."),
		INVALID("&4!!Critical Warning!! &cYour configuration seems to be invalid! Consider regenerating it before any damage occur."),
		//UNKNOWN("&eWarning: Configuration version was not recognized by ChatControl updater! Consider regenerating it!"),
		DISABLED(),
		UPDATE_NOT_NECESSARY();

		String msg;
		private Status(String msg) {
			this.msg = msg;
		}
		private Status() {
		}
	}

	public static void configCheck() {
		String plVersion = ChatControl.Config.getString("Do_Not_Change_Version_Number");

		if(!ChatControl.Config.getBoolean("Miscellaneous.Automatically_Update_Config")){
			status = Status.DISABLED;
			return;
		}

		if (!latestVersion.equals(plVersion)) {
			if (plVersion.equals("4.1.5")) {
				updateConfigTo416();
				updateConfigTo418();
				updateConfigTo419();
				updateConfigTo422();
				updateConfigTo423();
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equals("4.1.6")) {
				updateConfigTo418();
				updateConfigTo419();
				updateConfigTo422();
				updateConfigTo423();
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equals("4.1.7")) {
				updateConfigTo418();
				updateConfigTo419();
				updateConfigTo422();
				updateConfigTo423();
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equals("4.1.8")) {
				updateConfigTo419();
				updateConfigTo422();
				updateConfigTo423();
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equalsIgnoreCase("4.1.9") || plVersion.equalsIgnoreCase("4.2.1")) {
				updateConfigTo422();
				updateConfigTo423();
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equalsIgnoreCase("4.2.2")) {
				updateConfigTo423();
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equalsIgnoreCase("4.2.3")) {
				updateConfigTo424();
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equalsIgnoreCase("4.2.4")) {
				updateConfigTo430();
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equalsIgnoreCase("4.3.0") || plVersion.equalsIgnoreCase("4.3.1")) {
				updateConfigTo432();
				updateConfigTo436();
			} else if (plVersion.equalsIgnoreCase("4.3.2") || plVersion.equalsIgnoreCase("4.3.3") || plVersion.equalsIgnoreCase("4.3.4") || plVersion.equalsIgnoreCase("4.3.5")) {
				updateConfigTo436();
			} else if (Integer.valueOf(plVersion.replace(".", "")) < 415) {
				status = Status.TOO_OLD;
			} else if (Integer.valueOf(plVersion.replace(".", "")) > Integer.valueOf(latestVersion.replace(".", ""))) {
				status = Status.INVALID;
			}
		} else {
			status = Status.UPDATE_NOT_NECESSARY;
		}
		if(status == null){
			return;
		}
		if (!status.equals(Status.DISABLED) && !status.equals(Status.UPDATE_NOT_NECESSARY))
			Common.Log(status.msg);
			if(status.equals(Status.SUCCESS))
				updateVersionMark();			
	}

	public static void updateConfigTo416() {
		try {
			ChatControl.Config.set("Anti_Ad.Command_Whitelist", "[]");
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo418() {
		try {
			ChatControl.Config.set("Console.Filter_Plugin_Messages", true);
			ChatControl.Config.set("Console.Filter_Messages", "[]");
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo419() {
		try {
			ChatControl.Config.set("Signs.Advertising_Check", true);
			ChatControl.Config.set("Signs.Rewrite_Lines", true);
			ChatControl.Config.set("Signs.Rewrite_Text", "Advertising was:&4detected.:Please do not:advertise.");
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo422() {
		try {
			ChatControl.Config.set("Protect.Prevent_Tab_Complete", true);
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo423() {
		try {
			ChatControl.Config.set("Localization.Cannot_Command_While_Muted", "&7You cannot use this command while the chat is muted.");
			ChatControl.Config.set("Mute.Disabled_Commands_During_Mute", "[]");
			ChatControl.Config.set("Localization.Dupe_Sign", "&cPlease do not repeat the same text on sign.");
			ChatControl.Config.set("Signs.Duplication_Check", false);
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo424() {
		try {
			ChatControl.Config.set("Anti_Ad.Filter_Pre_Process", "[(\\[\\])]");			
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo430() {
		try {
			ChatControl.Config.set("Messages.Common.Join_Message", "default");
			ChatControl.Config.set("Messages.Common.Quit_Message", "default");
			ChatControl.Config.set("Messages.Common.Kick_Message", "default");			
			ChatControl.Config.set("Clear.Do_Not_Clear_For_Staff", "false");			
			ChatControl.Config.set("Localization.Staff_Chat_Clear_Message", "&7^----- [ == &fChat was cleared by %player &7== ] -----^");
			ChatControl.Config.set("Localization.Console", "&4server");			
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo432() {
		try {
			ChatControl.Config.set("Grammar.Replace_Characters", true);			
			ChatControl.Config.set("Grammar.Replace_With_Smileys", true);

			ChatControl.Config.set("Grammar.Replace_List.dis", "this");
			ChatControl.Config.set("Grammar.Replace_List.wanna", "want");
			ChatControl.Config.set("Grammar.Replace_List.gonna", "going");
			ChatControl.Config.set("Grammar.Replace_List.(can|may|would you like if) i (have|be|become|get|has) (op|admin|mod|builder)", "can i has nodus?");
			ChatControl.Config.set("Grammar.Replace_List.(do|are) you (need|wish|looking for) (any|some|one|good) (op|ops|operators|admins|mods|builders|new people|ateam)", "u need some pig zaps");
			ChatControl.Config.set("Grammar.Replace_List.this server (is bad|sucks)", "i just griefed some crap castle /w nodus u noob kiddos");
			ChatControl.Config.set("Grammar.Replace_List.owner (fucks|sucks) (kids|dicks|birds)", "piewdiepie sucks");

			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}

	public static void updateConfigTo436() {
		try {
			ChatControl.Config.set("Localization.Successful_Console_Clear", "%prefix &7Console was successfuly cleared.");			
			ChatControl.Config.set("Clear.Amount_Of_Lines_To_Clear_In_Console", 300);

			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
			status = Status.SUCCESS;
		} catch (Exception ex) {
			status = Status.ERROR;
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}
	
	public static void updateVersionMark() {
		try {		
			ChatControl.Config.set("Do_Not_Change_Version_Number", latestVersion);
			ChatControl.Config.save(new File(ChatControl.directory, "config.yml"));
		} catch (Exception ex) {
			Common.Log("&cUnable to update configuration. Error:" + ex);
		}
	}	
}