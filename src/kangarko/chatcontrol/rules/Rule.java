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
	 * A handler that triggers when {@link #match} matches the checked message
	 */
	private RuleHandler handler = null;

	/**
	 * Whenever the message should be cancelled from appearing
	 */
	private boolean cancel = false;

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
		
		return Common.regExMatch(match, message);
	}
	
	public String getMatch() {
		return match;
	}
	
	public String getStripBefore() {
		return stripBefore;
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
	
	public RuleHandler getHandler() {
		return handler;
	}
	
	public void setHandler(RuleHandler handler) {
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
	
	@Override
	public String toString() {
		return "Rule{\n"
				+ "    Match = \'" + match + "\',\n"
				+ "    Strip Before Match = \'" + stripBefore + "\',\n"
				+ "    Replace With = \'" + rewrite + "\',\n"
				+ "    Execute Command = \'" + commandToExecute + "\',\n"
				+ "    Handler = \'" + handler + "\',\n"
				+ "    Warn Message = \'" + warnMessage + "\',\n"
				+ "    Deny = " + cancel + "\n"
				+ "}";
	}
	
	/**
	 * Short version of {@link #toString()} that returns only regex used in this rule.
	 * @return rule's regular expression
	 */
	public String toShortString() {
		return "Rule=\'" + match + "\'";
	}
}
