package kangarko.chatcontrol.rules;

import java.util.List;
import java.util.Objects;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Writer;

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Custom handler that handles message caught by {@link Rule}
 * @author kangarko
 */
public class Handler {

	/**
	 * The name of the handler.
	 * It's automatically set from the handler name in handlers file.
	 */
	private final String name;
	
	/**
	 * The name/id of the rule associated with this handler.
	 */
	private String ruleID = "UNSET";
	
	/**
	 * A permission that makes player bypass the checks.
	 */
	private String bypassPermission;

	/**
	 * A message displayed to the player that triggered the handler.
	 * Set to 'none' to disable.
	 */
	private String playerWarnMsg;

	/**
	 * A message broadcasted to everyone.
	 * Set to 'none' to disable.
	 */
	private String broadcastMsg;

	/**
	 * People with specified permission will recieve {@link #staffAlertMsg}.
	 * Functional only when {@link #staffAlertMsg} is 'none'.
	 */
	private String staffAlertPermission;

	/**
	 * A message broadcasted to staff with {@link #staffAlertPermission}.
	 * Set to 'none' to disable.
	 */
	private String staffAlertMsg;

	/**
	 * A message logged in the server's console.
	 * Set to 'none' to disable.
	 */
	private String consoleMsg;

	/**
	 * A list of commands to be executed.
	 * Variables: %player %message
	 * Can be empty.
	 */
	private List<String> commandsToExecute;

	/**
	 * A name of a file that message will be writed in.
	 * Variables: %time %player %message
	 * Set to 'none' to disable writing.
	 */
	private String writeToFileName;

	/**
	 * Should the message be blocked from appearing?
	 * This cancels for instance player chat event or command event.
	 */
	private boolean blockMessage = false;

	/**
	 * A replacement that replaces only part of the message caught by a rule.
	 */
	private String msgReplacement;

	/**
	 * A message that replaces the entire message caught by a rule.
	 */
	private String rewriteTo;

	/**
	 * Creates a new handler instance.
	 * @param name the name of this handler
	 */
	public Handler(String name, String ruleID) {
		this.name = name;
		
		if (ruleID != null)
			this.ruleID = ruleID;
	}

	public String getName() {
		return name;
	}

	public void setBypassPermission(String bypassPermission) {
		Validate.isTrue(this.bypassPermission == null, "Bypass permission already set for: " + this);
		
		this.bypassPermission = bypassPermission;
	}

	public void setPlayerWarnMsg(String playerWarnMsg) {
		Validate.isTrue(this.playerWarnMsg == null, "Player warn message already set for: " + this);

		this.playerWarnMsg = playerWarnMsg;
	}

	public void setBroadcastMsg(String broadcastMsg) {
		Validate.isTrue(this.broadcastMsg == null, "Broadcast message already set for: " + this);

		this.broadcastMsg = broadcastMsg;
	}

	public void setStaffAlertMsg(String staffAlertMsg) {
		Validate.isTrue(this.staffAlertMsg == null, "Staff alert message already set for: " + this);

		this.staffAlertMsg = staffAlertMsg;
	}

	public void setStaffAlertPermission(String staffAlertPermission)  {
		Objects.requireNonNull(staffAlertMsg, "Staff alert message is null, cannot get staff permission! Handler: " + this);

		this.staffAlertPermission = staffAlertPermission;
	}

	public void setConsoleMsg(String consoleMsg) {
		Validate.isTrue(this.consoleMsg == null, "Console message already set for: " + this);

		this.consoleMsg = consoleMsg;
	}

	public void setCommandsToExecute(List<String> commandsToExecute) {
		Validate.isTrue(this.commandsToExecute == null, "Commands to execute already set for: " + this);
		
		this.commandsToExecute = commandsToExecute;
	}

	public void setWriteToFileName(String writeToFileName) {
		Validate.isTrue(this.writeToFileName == null, "Write to file path already set for: " + this);

		this.writeToFileName = writeToFileName;
	}
	
	public void setBlockMessage() {
		Validate.isTrue(!this.blockMessage, "Message is already blocked for: " + this);
		
		this.blockMessage = true;
	}
	
	public void setMsgReplacement(String msgReplacement) {
		Validate.isTrue(!this.blockMessage, "Replacement cannot be defined when the message is blocked: " + this);
		Validate.isTrue(this.rewriteTo == null, "Whole message replacement already defined for: " + this);
		
		this.msgReplacement = msgReplacement;
	}

	public void setRewriteTo(String wholeMsgReplacement) {
		Validate.isTrue(!this.blockMessage, "Whole replacement cannot be defined when the message is blocked: " + this);
		Validate.isTrue(this.msgReplacement == null, "Part message replacement already defined for: " + this);
		
		this.rewriteTo = wholeMsgReplacement;
	}
	
	public <T extends Cancellable> String handle(T e, Player pl, String match, String msg, boolean command, boolean sign) {
		if (bypassPermission != null && Common.hasPerm(pl, bypassPermission))
			return msg;

		if (playerWarnMsg != null)
			Common.tell(pl, replaceVariables(playerWarnMsg));

		if (broadcastMsg != null)
			Common.broadcastWithPlayer(replaceVariables(broadcastMsg).replace("%message", msg), pl.getName());
		
		if (staffAlertMsg != null) {
			Objects.requireNonNull(staffAlertPermission, "Staff alert permission is null for: " + this);
			
			for (Player online : ChatControl.getOnlinePlayers())
				if (Common.hasPerm(online, staffAlertPermission))
					Common.tell(online, replaceVariables(staffAlertMsg).replace("%message", msg), pl.getName());
		}
			
		if (consoleMsg != null)
			Common.Log(replaceVariables(consoleMsg).replace("%player", pl.getName()).replace("%message", msg));
		
		if (commandsToExecute != null)
			for (String commandToExecute : commandsToExecute)
				Common.customAction(pl, commandToExecute, msg);
		
		if (writeToFileName != null)
			Writer.zapisatDo(writeToFileName, pl.getName(), msg);

		if (blockMessage)
			e.setCancelled(true);
		else {
			if (msgReplacement != null)
				return msg.replaceAll(match, Common.colorize(msgReplacement));
			else if (rewriteTo != null)
				return Common.colorize(replaceVariables(rewriteTo).replace("%player", pl.getName()).replace("%message", msg));
		}
		
		return msg;
	}

	private String replaceVariables(String str) {
		return str.replace("%ruleID", ruleID).replace("%handler", name);
	}
	
	@Override
	public String toString() {
		return "    Handler{\n"
				+ "        Name: \'" + name + "\'\n"
				+ (playerWarnMsg != null ? "        Player Warn Msg: \'" + playerWarnMsg + "\'\n" : "")
				+ (broadcastMsg != null ? "        Broadcast Msg: \'" + broadcastMsg + "\'" : "")
				+ (staffAlertPermission != null ? "        Staff Alert Permission: \'" + staffAlertPermission + "\'\n" : "")
				+ (staffAlertMsg != null ? "        Staff Alert Msg: \'" + staffAlertMsg + "\'\n" : "")
				+ (consoleMsg != null ? "        Console Msg: \'" + consoleMsg + "\'\n" : "")
				+ (commandsToExecute != null ? "        Commands To Execute: \'" + printCommands() + "\'\n" : "")
				+ (writeToFileName != null ? "        Write To File Name: \'" + writeToFileName + "\'\n" : "")
				+ (blockMessage ? "        Block Message: \'" + blockMessage + "\'\n" : "")
				+ (msgReplacement != null ? "        Replace Part With: \'" + msgReplacement + "\'\n" : "")
				+ (rewriteTo != null ? "        Replace Whole With: \'" + rewriteTo + "\'\n" : "")
				+ "    }";
	}

	private String printCommands() {
		String commands = "(" + commandsToExecute.size() + ")";
		for (String command : commandsToExecute)
			commands+= command;

		return commands;
	}
}