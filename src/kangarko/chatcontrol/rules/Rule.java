package kangarko.chatcontrol.rules;

import kangarko.chatcontrol.utils.Common;

/**
 * Represents a single rule
 * @author kangarko
 */
public class Rule {

	/**
	 * Required regular expression used against the checked message
	 */
	public final String match;
	
	/**
	 * Required regular expression used before the message is checked to strip characters
	 */
	public String stripBefore;

	/**
	 * Replace the part of the checked message that matches {@link #match} with provided string
	 */
	public String replace = null;
	
	/**
	 * Rewrite the entire message to specified string
	 */
	public String rewrite = null;

	/**
	 * Optional command executed as the server console
	 */
	public String executeCommand = null;
	
	/**
	 * A message to the player
	 */
	public String warnMessage = null;
	
	/**
	 * A handler that triggers when {@link #match} matches the checked message
	 */
	public RuleHandler handler = null;

	/**
	 * Whenever the message should be cancelled from appearing
	 */
	public boolean deny = false;

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
	
	@Override
	public String toString() {
		return "Rule{\n"
				+ "    Match = \'" + match + "\',\n"
				+ "    Strip Before Match = \'" + stripBefore + "\',\n"
				+ "    Replace With = \'" + rewrite + "\',\n"
				+ "    Execute Command = \'" + executeCommand + "\',\n"
				+ "    Handler = \'" + handler + "\',\n"
				+ "    Warn Message = \'" + warnMessage + "\',\n"
				+ "    Deny = " + deny + "\n"
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
