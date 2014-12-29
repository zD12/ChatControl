package kangarko.chatcontrol.hooks;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicesManager;

public class VaultHook {

	private final Chat chat;
	private final Economy economy;

	public VaultHook() {
		ServicesManager services = Bukkit.getServicesManager();

		chat = services.getRegistration(Chat.class).getProvider();
		economy = services.getRegistration(Economy.class).getProvider();
	}

	public String getPlayerPrefix(Player pl) {
		return chat.getPlayerPrefix(pl);
	}

	public String getPlayerSuffix(Player pl) {
		return chat.getPlayerSuffix(pl);
	}

	@SuppressWarnings("deprecation")
	public void takeMoney(String player, double amount) {
		economy.withdrawPlayer(player, amount);
	}
}
