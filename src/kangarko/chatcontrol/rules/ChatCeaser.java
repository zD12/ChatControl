package kangarko.chatcontrol.rules;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Custom rule engine. Reads a set of rules from a file
 * @author kangarko
 * @since 5.0.0
 */
public final class ChatCeaser {

	/**
	 * Stored rules. Can only be modified in {@link #load()} method.
	 */
	private final List<Rule> rules = new ArrayList<>();

	/**
	 * The name of the file rules are located in.
	 */
	private final String fileName;

	/**
	 * The file with rules.
	 */
	private final File file;

	/**
	 * Creates new instance which can be used to read rules from a file
	 * @param fileName file name, which location is automatically set to the plugin's directory
	 */
	public ChatCeaser(String fileName) {
		fileName = "rules/" + fileName;
		
		this.fileName = fileName;
		this.file = new File(ChatControl.instance().getDataFolder(), fileName);
	}

	/**
	 * Clears {@link #rules} and fill them with all rules found in the specified {@link #file}
	 */
	public void load() {
		rules.clear();

		// Copy the provided file with some example values.
		if (!file.exists()) {
			ChatControl.instance().saveResource(fileName, false);
			Common.Log("&fCreated default rules file: " + fileName);
		}

		try {
			Rule rule = null; // The rule being created.
			String previousRuleName = null;
			List<String> rawLines = Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);

			for (int i = 0; i < rawLines.size(); i++) {
				String line = rawLines.get(i).trim();

				if (!line.isEmpty() && !line.startsWith("#")) { // Ignore blank lines and comments.

					// If a line starts with 'match ' then assume a new rule is found and start creating it. This makes a new instance of 'rule' variable.
					if (line.startsWith("match ")) {
						if (rule != null) // Found another match, assuming previous rule is finished creating.
							rules.add(rule);

						rule = new Rule(line.replaceFirst("match ", ""));
						previousRuleName = rule.toShortString();
					} else {
						Objects.requireNonNull(rule, "Cannot define an operator when no rule is being created! Previous rule: \'" + previousRuleName + "\'");
						// If a rule is being created then attempt to parse operators.
						
						if ("then deny".equals(line))
							rule.setCancelEvent();

						else if (line.startsWith("strip "))
							rule.setStripBefore(line.replaceFirst("strip ", ""));
						
						else if (line.startsWith("id "))
							rule.setId(line.replaceFirst("id ", ""));
						
						else if (line.startsWith("then rewrite "))
							rule.setRewrite(line.replaceFirst("then rewrite ", ""));

						else if (line.startsWith("then replace "))
							rule.setReplacement(line.replaceFirst("then replace ", ""));
						
						else if (line.startsWith("then console "))
							rule.setCommandToExecute(line.replaceFirst("then console ", ""));

						else if (line.startsWith("then warn "))
							rule.setWarnMessage(line.replaceFirst("then warn ", ""));
						
						else if (line.startsWith("handle as "))
							rule.setHandler(Handlers.loadHandler(line.replaceFirst("handle as ", ""), rule.getId()));
						
						else
							throw new NullPointerException("Unknown operator: " + line);
					}
				}

				if (i + 1 == rawLines.size() && rule != null) // Reached end of the file but a rule is being created, finishing it
					rules.add(rule);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		for (Rule rule : rules) // TODO Remove debug.
			System.out.print("Loaded rule:\n" + rule);
		
		Common.Log("&fLoaded " + rules.size() + " Rules.");
	}
	
	/**
	 * Check the message agains all rules.
	 * Can cancel the event or return modified message.
	 * @param e the event - must be cancellable
	 * @param pl the player that triggered filtering
	 * @param msg the message that is being checked
	 * @return the message that was initially put, might be changed
	 */
	public <T extends Cancellable> String handleRules(T e, Player pl, String msg) {
		boolean command = false; // is the event fired when player entered a command?
		boolean sign = false; // is the event fired when player edits a sign?
		
		if (e instanceof PlayerCommandPreprocessEvent)
			command = true;
		else if (e instanceof SignChangeEvent)
			sign = true;
		
		for (Rule rule : rules) {
			if (rule.matches(msg)) {
				Common.Log(rule.toShortString() + " &bcatched message: &f" + msg);
				
				if (rule.getHandler() != null)
					msg = rule.getHandler().handle(e, pl, rule.getMatch(), msg, command, sign);
				
				if (e.isCancelled())
					return msg; // The message will not appear in the chat, no need to continue.
				
				if (rule.getRewrite() != null)
					msg = Common.colorize(rule.getRewrite());

				if (rule.getReplacement() != null)
					msg = msg.replaceAll(rule.getMatch(), Common.colorize(rule.getReplacement()));
				
				if (rule.getCommandToExecute() != null)
					Common.customAction(pl, rule.getCommandToExecute(), msg);
				
				if (rule.getWarnMessage() != null)
					Common.tell(pl, Common.colorize(rule.getWarnMessage()));

				if (rule.cancelEvent()) {
					e.setCancelled(true);
					return msg; // The message will not appear in the chat, no need to continue.
				}
			}
		}
		return msg;
	}
}