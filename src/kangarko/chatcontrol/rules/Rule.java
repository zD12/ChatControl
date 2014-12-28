package kangarko.chatcontrol.rules;

import kangarko.chatcontrol.utils.Common;

import org.apache.commons.lang.Validate;

/**
 * Represents a single rule
 * @author kangarko
 */
public class Rule {

	/**
	 * Required regular expression used against the checked message
	 */
	private final String match;

	/**
	 * A name/id of the rule. Used in some messages.
	 */
	private String id;

	/**
	 * Ignore following string/regular expression
	 */
	private String ignoredMessage;

	/**
	 * Ignore events. Currently can be: chat, command or sign
	 */
	private Integer ignoredEvent;

	/**
	 * Required regular expression used before the message is checked to strip characters
	 */
	private String stripBefore;

	/**
	 * Replace the part of the checked message that matches {@link #match} with provided string
	 */
	private String replacement = null;

	/**
	 * Rewrite the entire message to specified string
	 */
	private String rewrite = null;

	/**
	 * Optional command executed as the server console
	 */
	private String commandToExecute = null;

	/**
	 * A message to the player
	 */
	private String warnMessage = null;
	
	/**
	 * A permission required to get message {@link #customAlertMessage}
	 */
	private String customAlertPermission = null;

	/**
	 * A message broadcasted to players with {@link #customAlertPermission}
	 */
	private String customAlertMessage = null;
	
	/**
	 * A handler that triggers when {@link #match} matches the checked message
	 */
	private Handler handler = null;

	/**
	 * Whenever the message should be cancelled from appearing
	 */
	private boolean cancel = false;

	/**
	 * Whenever the message should be logged and saved into a file.
	 */
	private boolean log = false;
	
	/**
	 * How much money to take from the player (Vault must be loaded)
	 */
	private Double fine;

	/**
	 * Creates a new rule with provided regular expression
	 * @param match the regular expression used against the checked message
	 */
	public Rule(String match) {
		this.match = match;
	}

	/**
	 * Checks specified message against regex {@link #match}
	 * @param message The checked message
	 * @return if the message matches the {@link #match} regular expression
	 */
	public boolean matches(String message) {
		if (stripBefore != null)
			message = message.replaceAll(stripBefore, "");

		if (ignoredMessage != null && Common.regExMatch(ignoredMessage, message)) {
			Common.Log("&fMessage \'" + message + "\' ignored by \'" + ignoredMessage + "\'");
			return false;
		}

		return Common.regExMatch(match, message);
	}

	public String getMatch() {
		return match;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		Validate.isTrue(this.id == null, "ID already set on: " + this);

		this.id = id;
	}

	public void setIgnoredMessage(String ignoredMessage) {
		Validate.isTrue(this.ignoredMessage == null, "Ignored message already set on: " + this);

		this.ignoredMessage = ignoredMessage;
	}

	public String getStripBefore() {
		return stripBefore;
	}

	public Integer getIgnoredEvent() {
		return ignoredEvent;
	}

	public void setIgnoreEvent(String ignoreEvent) {
		Validate.isTrue(this.ignoredEvent == null, "Ignored event already set on: " + this);

		this.ignoredEvent = getEventFromName(ignoreEvent);
	}

	public void setStripBefore(String stripBefore) {
		Validate.isTrue(this.stripBefore == null, "Strip before already set on: " + this);

		this.stripBefore = stripBefore;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		Validate.isTrue(this.replacement == null, "Replacement already set on: " + this);

		this.replacement = replacement;
	}

	public String getRewrite() {
		return rewrite;
	}

	public void setRewrite(String rewrite) {
		Validate.isTrue(this.rewrite == null, "Rewrite message already set on: " + this);

		this.rewrite = rewrite;
	}

	public String getCommandToExecute() {
		return commandToExecute;
	}

	public void setCommandToExecute(String commandToExecute) {
		Validate.isTrue(this.commandToExecute == null, "Command to execute already set on: " + this);

		this.commandToExecute = commandToExecute;
	}

	public String getWarnMessage() {
		return warnMessage;
	}

	public void setWarnMessage(String warnMessage) {
		Validate.isTrue(this.warnMessage == null, "Warn message already set on: " + this);

		this.warnMessage = warnMessage;
	}
	
	public void setCustomAlert(String raw) {
		String[] parts = raw.split(" ");
		Validate.isTrue(parts.length > 0, "Malformed then alert - must specify permission and a message.");
	
		String permission = parts[0];
		
		this.customAlertPermission = permission;
		this.customAlertMessage = raw.replace(permission + " ", "");
	}
	
	public String getCustomAlertMessage() {
		return customAlertMessage;
	}
	
	public String getCustomAlertPermission() {
		return customAlertPermission;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		Validate.isTrue(this.handler == null, "Handler already set on: " + this);

		this.handler = handler;
	}

	public boolean cancelEvent() {
		return cancel;
	}

	public void setCancelEvent() {
		Validate.isTrue(!cancel, "Message already set to be cancelled on: " + this);

		cancel = true;
	}

	public boolean log() {
		return log;
	}

	public void setLog() {
		Validate.isTrue(!log, "Message already being logged on: " + this);

		this.log = true;
	}

	public Double getFine() {
		return fine;
	}
	
	public void setFine(Double fine) {
		Validate.isTrue(this.fine == null, "Fine already set on: " + this);
		
		this.fine = fine;
	}
	
	@Override
	public String toString() {
		return "Rule{\n"
				+ (id != null ? "    Id = " + id + "\n" : "")
				+ "    Match = \'" + match + "\',\n"
				+ (stripBefore != null ? "    Strip Before Match = \'" + stripBefore + "\',\n" : "")
				+ (ignoredMessage != null ? "    Ignore Message = \'" + ignoredMessage + "\',\n"  : "")
				+ (ignoredEvent != null ? "    Ignore Event = \'" + ignoredEvent + "\',\n"  : "")
				+ (replacement != null ? "    Replace With = \'" + replacement + "\',\n"  : "")
				+ (rewrite != null ? "    Rewrite = \'" + rewrite + "\',\n"  : "")
				+ (commandToExecute != null ? "    Execute Command = \'" + commandToExecute + "\',\n"  : "")
				+ (handler != null ? "    Handler = \'" + handler + "\',\n"  : "")
				+ (warnMessage != null ? "    Warn Message = \'" + warnMessage + "\',\n"  : "")
				+ (cancel ? "    Deny = " + cancel + "\n"  : "")
				+ (log ? "    Log = " + log + "\n"  : "")
				+ "}";
	}

	/**
	 * Short version of {@link #toString()} that returns only regex used in this rule.
	 * @return rule's regular expression
	 */
	public String toShortString() {
		return "Rule {" + (id != null ? "ID=" + id + ",": "") + "Match=\'" + match + "\'}";
	}

	private int getEventFromName(String str) {
		switch (str.toLowerCase().replace(" ", "")) {
		case "chat":
		case "asyncplayerchatevent":
			return Handler.CHAT;
		case "command":
		case "commands":
		case "playercommandpreprocessevent":
			return Handler.COMMAND;
		case "sign":
		case "signs":
		case "signchangeevent":
			return Handler.SIGN;
		default:
			throw new NullPointerException("Unknown ignore event: " + str);
		}
	}
}
