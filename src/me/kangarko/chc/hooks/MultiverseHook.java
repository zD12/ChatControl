package me.kangarko.chc.hooks;

import me.kangarko.chc.utils.Common;

import org.bukkit.Bukkit;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MultiverseHook {

	private final MultiverseCore multiVerse;

	public MultiverseHook() {
		this.multiVerse = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");

		Common.Log("&2Hooked with Multiverse 2 (World Alias)!");
	}

	public String getColoredAlias(String world) {
		MultiverseWorld mvWorld = multiVerse.getMVWorldManager().getMVWorld(world);

		if (mvWorld != null)
			return mvWorld.getColoredWorldString();

		return world;
	}
}
