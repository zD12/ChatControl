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
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Writer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Custom rule engine. Reads a set of rules from a file
 * @author kangarko
 * @since 5.0.0
 * @deprecated add support for multiple rules - chat, commands, signs, protocol chat remap
 */
public final class ChatCeaser {

	/**
	 * Stored rules. Can only be modified in {@link #load()} method.
	 */
	private final List<Rule> rules = new ArrayList<>();

	/**
	 * The file with rules.
	 */
	private final File file;

	/**
	 * Creates new instance which can be used to read rules from a file
	 *
	 * @param fileName file name, which location is automatically set to the plugin's directory
	 */
	public ChatCeaser(String fileName) {
		file = Writer.Extract(fileName);
	}

	/**
	 * Clears {@link #rules} and fill them with all rules found in the specified {@link #file}
	 */
	public void load() {
		rules.clear();

		try {
			Rule rule = null; // The rule being created.
			String previousRuleName = null;
			List<String> rawLines = Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);

			for (int i = 0; i < rawLines.size(); i++) {
				String line = rawLines.get(i).trim();

				if (!line.isEmpty() && !line.startsWith("#"))
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

						else if ("then log".equals(line))
							rule.setLog();

						else if (line.startsWith("strip "))
							rule.setStripBefore(line.replaceFirst("strip ", ""));

						else if (line.startsWith("id "))
							rule.setId(line.replaceFirst("id ", ""));

						else if (line.startsWith("ignore string "))
							rule.setIgnoredMessage(line.replaceFirst("ignore string ", ""));

						else if (line.startsWith("ignore event "))
							rule.setIgnoreEvent(line.replaceFirst("ignore event ", ""));

						else if (line.startsWith("then rewrite "))
							rule.setRewrite(line.replaceFirst("then rewrite ", ""));

						else if (line.startsWith("then replace "))
							rule.setReplacement(line.replaceFirst("then replace ", ""));

						else if (line.startsWith("then console "))
							rule.setCommandToExecute(line.replaceFirst("then console ", ""));

						else if (line.startsWith("then warn "))
							rule.setWarnMessage(line.replaceFirst("then warn ", ""));

						else if (line.startsWith("then alert "))
							rule.setCustomAlert(line.replaceFirst("then alert ", ""));

						else if (line.startsWith("then fine "))
							rule.setFine(Double.parseDouble(line.replaceFirst("then fine ", "")));

						else if (line.startsWith("handle as "))
							rule.setHandler(HandlerLoader.loadHandler(line.replaceFirst("handle as ", ""), rule.getId()));

						else
							throw new NullPointerException("Unknown operator: " + line);
					}

				if (i + 1 == rawLines.size() && rule != null) // Reached end of the file but a rule is being created, finishing it
					rules.add(rule);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (Settings.VERBOSE)
			for (Rule rule : rules)
				Common.Log("Loaded rule:\n" + rule);

		Common.Log("&fLoaded " + rules.size() + " Rules.");
	}

	/**
	 * Check the message against all rules.
	 * Can cancel the event or return modified message.
	 *
	 * @param e the event - must be cancellable
	 * @param pl the player that triggered filtering
	 * @param msg the message that is being checked
	 * @return the message that was initially put, might be changed
	 */
	public <T extends Cancellable> String parseRules(T e, Player pl, String msg) {
		int flag = Handler.CHAT;

		if (e instanceof PlayerCommandPreprocessEvent)
			flag = Handler.COMMAND;
		else if (e instanceof SignChangeEvent)
			flag = Handler.SIGN;

		for (Rule rule : rules) {
			if (rule.getIgnoredEvent() != null && rule.getIgnoredEvent() == flag)
				continue;

			if (rule.matches(msg)) {
				Common.Debug(rule.toShortString() + " &bcatched message: &f" + msg);

				if (rule.log()) {
					Common.Log(org.bukkit.ChatColor.RED + (flag == Handler.SIGN ? "[SIGN at " + Common.shortLocation(pl.getLocation()) + "] " : "") + pl.getName() + " violated " + rule.toShortString() + " with message: &f" + msg);
					Writer.Write("logs/rules_log.txt", pl.getName(), (flag == Handler.SIGN ? "[SIGN at " + Common.shortLocation(pl.getLocation()) + "] " : "") + rule.toShortString() + " caught message: " + msg);
				}

				if (rule.getCustomAlertMessage() != null) {
					Objects.requireNonNull(rule.getCustomAlertPermission(), "Custom alert permission cannot be null!");

					for (Player online : ChatControl.getOnlinePlayers())
						if (Common.hasPerm(online, rule.getCustomAlertPermission()))
							Common.tellLater(online, 1, rule.getCustomAlertMessage().replace("%player", pl.getName()).replace("%message", msg).replace("%ruleID", rule.getId()));
				}

				if (rule.getHandler() != null)
					msg = handle(e, pl, rule.getMatch(), msg, rule.getHandler(), flag);

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

				if (rule.getFine() != null && ChatControl.instance().vault != null)
					ChatControl.instance().vault.takeMoney(pl.getName(), rule.getFine());

				if (rule.cancelEvent()) {
					e.setCancelled(true);
					return msg; // The message will not appear in the chat, no need to continue.
				}
			}
		}

		HandlerCache.reset();

		return msg;
	}

	public <T extends Cancellable> String handle(T e, Player pl, String match, String msg, Handler handler, int flag) {
		if (handler.getBypassPermission() != null && Common.hasPerm(pl, handler.getBypassPermission()))
			return msg;

		if (flag == Handler.COMMAND)
			for (String ignored : handler.getIgnoredInCommands())
				if (msg.startsWith(ignored))
					return msg;

		String warnMessage = handler.getPlayerWarnMsg();

		if (warnMessage != null && !HandlerCache.lastWarnMsg.equals(warnMessage)) {
			Common.tellLater(pl, 1, replaceVariables(handler, warnMessage));
			HandlerCache.lastWarnMsg = warnMessage;
		}

		String broadcastMessage = handler.getBroadcastMsg();

		if (broadcastMessage != null && !HandlerCache.lastBroadcastMsg.equals(broadcastMessage)) {
			Common.broadcastWithPlayer(replaceVariables(handler, broadcastMessage).replace("%message", msg), pl.getName());
			HandlerCache.lastBroadcastMsg = broadcastMessage;
		}

		if (handler.getStaffAlertMsg() != null) {
			Objects.requireNonNull(handler.getStaffAlertPermission(), "Staff alert permission is null for: " + this);

			for (Player online : ChatControl.getOnlinePlayers())
				if (Common.hasPerm(online, handler.getStaffAlertPermission()))
					Common.tell(online, (flag == Handler.SIGN ? "[SIGN at " + Common.shortLocation(pl.getLocation()) + "] " : "") + replaceVariables(handler, handler.getStaffAlertMsg()).replace("%message", msg), pl.getName());
		}

		if (handler.getConsoleMsg() != null)
			Common.Log(replaceVariables(handler, handler.getConsoleMsg()).replace("%player", pl.getName()).replace("%message", msg));

		if (handler.getCommandsToExecute() != null)
			for (String cmd : handler.getCommandsToExecute())
				Common.customAction(pl, cmd, msg);

		if (handler.getWriteToFileName() != null)
			Writer.Write(handler.getWriteToFileName(), pl.getName(), replaceVariables(handler, "[Handler=%handler, Rule ID=%ruleID] ") + msg);

		if (handler.blockMessage() || (flag == Handler.SIGN && Settings.Signs.BLOCK_WHEN_VIOLATES_RULE))
			e.setCancelled(true);
		else if (handler.getMsgReplacement() != null)
			return msg.replaceAll(match, Common.colorize(handler.getMsgReplacement()));
		else if (handler.getRewriteTo() != null)
			return Common.colorize(replaceVariables(handler, handler.getRewriteTo()).replace("%player", pl.getName()).replace("%message", msg));

		return msg;
	}

	/**
	 * Replaces rule ID (if set) and handler name (if set) in the message.
	 *
	 * @param handler the handler the variables will be taken from
	 * @param message the message to replace variables in
	 * @returns message with modified variables
	 */
	private String replaceVariables(Handler handler, String message) {
		return message.replace("%ruleID", handler.getRuleID()).replace("%handler", handler.getName());
	}
}

class HandlerLoader {

	private static YamlConfiguration cfg;
	private static String sectionName;

	static Handler loadHandler(String name, String ruleID) {
		File file = Writer.Extract("handlers.yml");
		cfg = YamlConfiguration.loadConfiguration(file);

		if (!cfg.isConfigurationSection(name))
			throw new NullPointerException("Unknown handler: " + name);

		sectionName = cfg.getConfigurationSection(name).getName();

		Handler handler = new Handler(sectionName, ruleID);
		String message;

		message = getString("Bypass_With_Permission");
		if (isValid(message))
			handler.setBypassPermission(message);

		message = getString("Player_Warn_Message");
		if (isValid(message))
			handler.setPlayerWarnMsg(message);

		message = getString("Broadcast_Message");
		if (isValid(message))
			handler.setBroadcastMsg(message);

		message = getString("Staff_Alert_Message");
		if (isValid(message))
			handler.setStaffAlertMsg(message);

		message = getString("Staff_Alert_Permission");
		if (isValid(message))
			handler.setStaffAlertPermission(message);

		message = getString("Console_Message");
		if (isValid(message))
			handler.setConsoleMsg(message);

		message = getString("Write_To_File");
		if (isValid(message))
			handler.setWriteToFileName(message);

		Boolean block = cfg.getBoolean(sectionName + ".Block_Message");
		if (block != null && block)
			handler.setBlockMessage();

		message = getString("Replace_Word");
		if (isValid(message))
			handler.setMsgReplacement(message);

		message = getString("Replace_Whole");
		if (isValid(message))
			handler.setRewriteTo(message);

		List<String> list;
		if (cfg.isSet(sectionName + ".Execute_Commands")) {
			list = cfg.getStringList(sectionName + ".Execute_Commands");
			handler.setCommandsToExecute(list);
		}

		if (cfg.isSet(sectionName + ".Ignored_In_Commands")) {
			list = cfg.getStringList(sectionName + ".Ignored_In_Commands");
			handler.setIgnoredInCommands(list);
		}

		sectionName = null;

		return handler;
	}

	private static boolean isValid(String msg) {
		return msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("none");
	}

	private static String getString(String path) {
		String msg = cfg.getString(sectionName + "." + path);

		return msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("none") ? msg : null;
	}
}

/**
 * Caches last messages displayed to the player and broadcasted to everyone,
 * prevents duplicate displaying when multiple rules are violated at once.
 */
class HandlerCache {
	static String lastWarnMsg = "";
	static String lastBroadcastMsg = "";

	static void reset() {
		lastWarnMsg = "";
		lastBroadcastMsg = "";
	}
}