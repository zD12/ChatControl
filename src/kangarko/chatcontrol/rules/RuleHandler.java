package kangarko.chatcontrol.rules;

import java.util.Objects;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.model.Localization;
import kangarko.chatcontrol.model.Settings;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;
import kangarko.chatcontrol.utils.Writer;
import kangarko.chatcontrol.utils.Writer.FileType;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * 
 * @deprecated make custom handlers
 */
public enum RuleHandler {

	ADVERTISEMENT {
		@Override
		public <T extends Cancellable> String handle(T e, Player pl, String regex, String msg, boolean command, boolean sign) {
			if (Common.hasPerm(pl, Permissions.Bypasses.ADVERTISING))
				return msg;

			if (command && !Settings.Handlers.Ad.ENABLED_IN_COMMANDS)
				return msg;

			Common.customAction(pl, Settings.Handlers.Ad.DISPATCH_COMMAND, msg);

			if (command)
				msg = "&7[" + Localization.Parts.SIGN + "&7]&f " + msg;
			else if (sign)
				msg = "&7[" + Localization.Parts.COMMAND + "&7]&f " + msg;

			if (Settings.Handlers.Ad.WARN_PLAYER)
				Common.tell(pl, Localization.ANTIAD_PLAYER_WARN);

			if (Settings.Handlers.Ad.ALERT_STAFF)
				for (Player staff : ChatControl.getOnlinePlayers())
					if (Common.hasPerm(staff, Permissions.Notify.ADVERTISE))
						Common.tell(staff, Localization.ANTIAD_STAFF_ALERT.replace("%message", msg), pl.getName());

			if (Settings.Handlers.Ad.BROADCAST_BLOCK)
				for (Player online : ChatControl.getOnlinePlayers())
					if (!online.isOp() && !online.getName().equals(pl.getName()))
						Common.tell(online, Localization.ANTIAD_BROADCAST_ALERT.replace("%message", msg), pl.getName());

			if (Settings.Handlers.Ad.ALERT_CONSOLE)
				Common.Log(Localization.ANTIAD_CONSOLE_ALERT.replace("%player", pl.getName()).replace("%message", msg));

			if (Settings.Handlers.Ad.WRITE_TO_FILE)
				Writer.zapisatDo(FileType.ADVERTISEMENTS, pl.getName(), msg);

			e.setCancelled(true);
			return msg;
		}
	},

	SWEAR {
		@Override
		public <T extends Cancellable> String handle(T e, Player pl, String regex, String msg, boolean command, boolean sign) {
			if (Common.hasPerm(pl, Permissions.Bypasses.SWEARING))
				return msg;

			if (command && !Settings.Handlers.Swear.ENABLED_IN_COMMANDS)
				return msg;

			if (Settings.Handlers.Swear.ALERT_STAFF)
				for (Player online : ChatControl.getOnlinePlayers())
					if (Common.hasPerm(online, Permissions.Notify.SWEAR))
						Common.tell(online, Localization.ANTISWEAR_STAFF_ALERT.replace("%message", msg), pl.getName());

			if (Settings.Handlers.Swear.WARN_PLAYER)
				Common.tellLater(pl, 1, Localization.ANTISWEAR_PLAYER_WARN);

			Common.customAction(pl, Settings.Handlers.Swear.DISPATCH_COMMAND, msg);

			if (Settings.Handlers.Swear.BLOCK_MESSAGE)
				e.setCancelled(true);
			else if (Settings.Handlers.Swear.REPLACE_MESSAGE)
				msg = msg.replaceAll(regex, Settings.Handlers.Swear.REPLACEMENT);
			
			return msg;
		}
	};

	public abstract <T extends Cancellable> String handle(T e, Player pl, String regex, String msg, boolean command, boolean sign);

	public static RuleHandler fromName(String name) {
		RuleHandler handler = valueOf(name.toUpperCase());
		Objects.requireNonNull(handler, "Unknown handler: " + name);

		return handler;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
