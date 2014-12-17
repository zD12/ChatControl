package me.kangarko.chc.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Common;

import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("unused")
public class SettingsConsole extends ConfHelper {

	public static void load() {
		file = new File(ChatControl.instance().getDataFolder(), "Console.yml");

		File oldFile = new File(ChatControl.instance().getDataFolder(), "console.yml");
		cfg = YamlConfiguration.loadConfiguration(oldFile);

		if (cfg.isSet("Console")) {
			Common.Log("&fDetected old console.yml. This is &cnot &fcompatible with the new one, and file was moved to broken/console.yml");

			oldFile.renameTo(new File(ChatControl.instance().getDataFolder(), "/broken/console.yml"));
			oldFile.delete();
		}

		createFileAndLoad();
		loadValues(SettingsConsole.class);
	}

	
	public static boolean filterEnabled;
	public static boolean filterPluginMessages;
	public static boolean filterColorToAnsi;
	public static List<String> filterMessageList;
	
	private static final void filter() {
		filterEnabled = getBoolean("filter.enabled", true);
		filterPluginMessages = getBoolean("filter.filter-plugins", true);
		filterColorToAnsi = getBoolean("filter.colors-to-ansi", true);
		filterMessageList = getStringList("filter.filter-messages", Arrays.asList("Reached end of stream for", "Connection reset", "lost connection", ".*CONSOLE: (Save complete.|Forcing save..).*"));
		// TODO get replace back
	}
}
