package kangarko.chatcontrol.hooks;

import kangarko.chatcontrol.utils.Common;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class VaultHook {

	private Chat chat;
	private Economy economy;

	public VaultHook() {
		ServicesManager services = Bukkit.getServicesManager();

		RegisteredServiceProvider<Economy> economyProvider = services.getRegistration(net.milkbowl.vault.economy.Economy.class);		
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		else
			Common.Log("Economy plugin not found");

		RegisteredServiceProvider<Chat> chatProvider = services.getRegistration(net.milkbowl.vault.chat.Chat.class);		
		if (chatProvider != null)
			chat = chatProvider.getProvider();
		else
			Common.Log("Permissions/Chat plugin not found, prefix and suffix will not work");
	}

	public String getPlayerPrefix(Player pl) {
		if (chat == null)
			return "";

		return chat.getPlayerPrefix(pl);
	}

	public String getPlayerSuffix(Player pl) {
		if (chat == null)
			return "";

		return chat.getPlayerSuffix(pl);
	}

	@SuppressWarnings("deprecation")
	public void takeMoney(String player, double amount) {
		if (economy != null)
			economy.withdrawPlayer(player, amount);
	}
}
