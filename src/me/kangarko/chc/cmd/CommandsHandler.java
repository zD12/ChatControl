package me.kangarko.chc.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.model.ChatMessageType;
import me.kangarko.chc.model.ConfHelper;
import me.kangarko.chc.model.Localization;
import me.kangarko.chc.model.Settings;
import me.kangarko.chc.model.Variables;
import me.kangarko.chc.utils.Common;
import me.kangarko.chc.utils.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CommandsHandler implements CommandExecutor {

	// TODO get rid of this
	@Deprecated
	List<String> validParameters = Arrays.asList("-silent", "-s", "-anonymous", "-a", "-console", "-c"); // TODO Keep updated with the parameters below.

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			handleCommand(sender, args);
		} catch (InsufficientPermissionException ex) {
			Common.tell(sender, Localization.NO_PERMISSION);
		}
		
		return true;
	}

	private void handleCommand(CommandSender sender, String[] args) throws InsufficientPermissionException {
		if (args.length == 0) {
			boolean local = Bukkit.getIp().equalsIgnoreCase("93.91.250.138") && Bukkit.getPort() == 27975;

			Common.tell(sender, 
					"&3ChatControl &8// &fRunning &7v" + ChatControl.instance().getDescription().getVersion(), 
					"&3ChatControl &8// &fBy &7kangarko &f© 2013 - 2015", 
					"&3ChatControl &8// &fWebsite: &7http://rushmine.6f.sk" + (!local && new Random().nextInt(7) == 1 ? " &b< Prid si zahrat!" : ""));
			return;
		}

		String argument = args[0];
		String parameter = args.length >= 2 ? args[1] : "";
		String reason = "";

		if (parameter.startsWith("-") && !validParameters.contains(parameter)) {
			Common.tell(sender, Localization.WRONG_PARAMETERS.replace("%params", validParameters.toString().replace("[", "").replace("]", "")));
			return;
		}

		if (args.length > 1)
			for (int i = 1; i < args.length; i++)
				reason += " " + args[i];

		/**
		 * MUTE COMMAND
		 */
		if ("mute".equalsIgnoreCase(argument) || "m".equalsIgnoreCase(argument)) {

			checkPerm(sender, Permissions.Commands.mute);

			if (Common.hasPerm(sender, Permissions.Commands.muteSilent) && (parameter.equalsIgnoreCase("-silent") || parameter.equalsIgnoreCase("-s"))) {
				// do nothing
			} else if (Common.hasPerm(sender, Permissions.Commands.muteAnonymous) && (parameter.equalsIgnoreCase("-anonymous") || parameter.equalsIgnoreCase("-a")))
				Common.broadcastMsgIfEnabled(sender, "Mute.Broadcast", "Localization.Broadcast_Silent_" + (Variables.muted ? "Unmute" : "Mute"), "");
			else
				Common.broadcastMsgIfEnabled(sender, "Mute.Broadcast", "Localization.Broadcast_" + (Variables.muted ? "Unmute" : "Mute"), "");

			Common.tell(sender, Variables.muted ? Localization.MUTE_UNMUTE_SUCCESS: Localization.MUTE_SUCCESS);

			Variables.muted = !Variables.muted;
		}

		/**
		 * CLEAR COMMAND
		 */
		else if ("clear".equalsIgnoreCase(argument) || "c".equalsIgnoreCase(argument)) {

			checkPerm(sender, Permissions.Commands.clear);

			if (Common.hasPerm(sender, Permissions.Commands.clearConsole) && (parameter.equalsIgnoreCase("-console") || parameter.equalsIgnoreCase("-c") || parameter.equalsIgnoreCase("-konzola"))) {

				for (int i = 0; i < Settings.Chat.Clear.CONSOLE_LINES; i++)
					System.out.println("           ");

				if (sender instanceof Player)
					Common.Log("Console was cleared by " + sender.getName());

				Common.tell(sender, Localization.CHATCLEAR_CONSOLE);
				return;
			}

			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (Settings.Chat.Clear.IGNORE_STAFF && Common.hasPerm(pl, Permissions.Bypasses.chat_clear)) {
					Common.tell(pl, Localization.CHATCLEAR_STAFF, sender.getName());
					continue;
				}
				for (int i = 0; i < 120; i++)
					pl.sendMessage(ChatColor.RESET + "      ");
			}

			if (Common.hasPerm(sender, Permissions.Commands.clearSilent) && (parameter.equalsIgnoreCase("-silent") || parameter.equalsIgnoreCase("-s"))) {
				// do nothing
			} else if (Common.hasPerm(sender, Permissions.Commands.clearAnonymous) && (parameter.equalsIgnoreCase("-anonymous") || parameter.equalsIgnoreCase("-a"))) {
				Common.broadcastMsgIfEnabled(sender, "Clear.Broadcast", "Localization.Broadcast_Silent_Clear", "");
			} else {
				Common.broadcastMsgIfEnabled(sender, "Clear.Broadcast", "Localization.Broadcast_Clear", reason);
			}
		}

		/**
		 * FAKE COMMAND
		 */
		else if ("fake".equalsIgnoreCase(argument) || "f".equalsIgnoreCase(argument)) {

			checkPerm(sender, Permissions.Commands.fake);

			String fakePlayer = args.length != 3 ? sender.getName() : Common.colorize(args[2]);

			if (parameter.equalsIgnoreCase("join") || parameter.equalsIgnoreCase("j")) {
				if (Settings.Messages.JOIN == ChatMessageType.DEFAULT)
					Bukkit.broadcastMessage(ChatColor.YELLOW + fakePlayer + ChatColor.YELLOW + " joined the game.");

				else if (Settings.Messages.JOIN == ChatMessageType.HIDDEN)
					Common.tell(sender, Localization.CANNOT_BROADCAST_EMPTY_MESSAGE.replace("%event", Localization.Parts.JOIN));

				else
					Bukkit.broadcastMessage(Common.colorize(Settings.Messages.JOIN.getMsg().replace("%player", fakePlayer).replace("%prefix", Common.prefix())));

			} else if (parameter.equalsIgnoreCase("quit") || parameter.equalsIgnoreCase("q") || parameter.equalsIgnoreCase("leave") || parameter.equalsIgnoreCase("l")) {
				if (Settings.Messages.QUIT == ChatMessageType.DEFAULT)
					Bukkit.broadcastMessage(ChatColor.YELLOW + fakePlayer + ChatColor.YELLOW + " left the game.");

				else if (Settings.Messages.QUIT == ChatMessageType.HIDDEN)
					Common.tell(sender, Localization.CANNOT_BROADCAST_EMPTY_MESSAGE.replace("%event", Localization.Parts.QUIT));

				else
					Bukkit.broadcastMessage(Common.colorize(Settings.Messages.QUIT.getMsg().replace("%player", fakePlayer).replace("%prefix", Common.prefix())));

			} else {
				Common.tell(sender, Localization.USAGE_FAKE_CMD);
			}
		}

		/**
		 * RELOAD COMMAND
		 */
		else if ("reload".equalsIgnoreCase(argument) || "znovunacitat".equalsIgnoreCase(argument) || "r".equalsIgnoreCase(argument)) {
			checkPerm(sender, Permissions.Commands.reload);
			ConfHelper.loadAll();

			Common.tell(sender, Localization.RELOAD_COMPLETE);
		}

		/**
		 * LIST COMMAND
		 */

		else if ("commands".equalsIgnoreCase(argument) || "?".equals(argument) || "list".equalsIgnoreCase(argument)) {

			checkPerm(sender, Permissions.Commands.command_list);

			Common.tell(sender, 
					" ", 
					"&3  ChatControl &f(v" + ChatControl.instance().getDescription().getVersion() + ")", 
					"&2  [] &f= optional arguments (use only 1 at once)", "&6  <> &f= required arguments", 
					" ", 
					"  &f/chc mute &2[-silent] [-anonymous] [reason] &e- Chat (un)mute.", 
					"  &f/chc clear &2[-silent] [-anonymous] [reason] &e- Chat clearing.", 
					"  &f/chc fake &6<join/leave> &2[name] &e- Fake join/quit messages.", 
					"  &f/chc reload &e- Reload configuration.", "  &f/chc list &e- Command list.");
		} else
			Common.tell(sender, Localization.WRONG_ARGUMENTS);
	}

	private void checkPerm(CommandSender sender, String str) throws InsufficientPermissionException {
		if (!Common.hasPerm(sender, str))
			throw new InsufficientPermissionException();
	}
}
