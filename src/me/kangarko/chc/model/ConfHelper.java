package me.kangarko.chc.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import me.kangarko.chc.ChatControl;
import me.kangarko.chc.utils.Common;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfHelper {

	protected static YamlConfiguration cfg;
	protected static File file;;

	public static void loadAll() {
		// Order matters.
		Settings.load();
		SettingsConsole.load();
		SettingsChat.load();
		Localization.load();
	}

	protected static void loadValues(Class<?> clazz) {
		Objects.requireNonNull(cfg, "YamlConfiguration is null!");
		try {
			// Look in the class itself
			for (Method m : clazz.getDeclaredMethods()) {
				int modifier = m.getModifiers();

				if (Modifier.isPrivate(modifier) && Modifier.isFinal(modifier) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 0) {
					m.setAccessible(true);
					m.invoke(null);
				}
			}
			
			// Look in all sub-classes
			for (Class<?> subClazz : clazz.getDeclaredClasses())
				for (Method m : subClazz.getDeclaredMethods()) {
					int modifier = m.getModifiers();

					if (Modifier.isPrivate(modifier) && Modifier.isFinal(modifier) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 0) {
						m.setAccessible(true);
						m.invoke(null);
					}
				}
		} catch (ReflectiveOperationException ex) {
			ex.printStackTrace();
		}

		save();
	}

	protected static void createFileAndLoad() {
		File dataFolder = ChatControl.instance().getDataFolder();
		if (!dataFolder.exists())
			dataFolder.mkdirs();

		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		cfg = YamlConfiguration.loadConfiguration(file);
	}

	protected static void save() {
		if (file == null)
			return;

		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static boolean getBoolean(String path, boolean def) {
		if (!cfg.isSet(path))
			cfg.set(path, def);

		Validate.isTrue(cfg.isBoolean(path), "Malformed config value, expected boolean at: " + path);
		return cfg.getBoolean(path);
	}

	protected static String getString(String path, String def) {
		if (!cfg.isSet(path))
			cfg.set(path, def);

		Validate.isTrue(cfg.isString(path), "Malformed config value, expected string at: " + path);
		return cfg.getString(path);
	}

	protected static int getInteger(String path, int def) {
		if (!cfg.isSet(path))
			cfg.set(path, def);

		Validate.isTrue(cfg.isInt(path), "Malformed config value, expected integer at: " + path);
		return cfg.getInt(path);
	}

	protected static double getDouble(String path, double def) {
		if (!cfg.isSet(path))
			cfg.set(path, def);

		Validate.isTrue(cfg.isDouble(path), "Malformed config value, expected double at: " + path);
		return cfg.getDouble(path);
	}

	protected static HashMap<String, String> getValuesAndKeys(String path, HashMap<String, String> def, boolean deep) {
		if (!cfg.isSet(path)) {
			for (String str : def.keySet())
				cfg.set(path + "." + str, def.get(str));
		}

		Validate.isTrue(cfg.isConfigurationSection(path), "Malformed config value, expected configuration section at: " + path);
		HashMap<String, String> keys = new HashMap<>();

		for (String key : cfg.getConfigurationSection(path).getKeys(deep)) {
			if (keys.containsKey(key))
				Common.Warn("Duplicate key: " + key + " in " + path);
			keys.put(key, getString(path + "." + key, ""));
		}

		return keys;
	}

	protected static List<String> getStringList(String path, List<String> def) {
		if (!cfg.isSet(path))
			cfg.set(path, def);

		Validate.isTrue(cfg.isList(path), "Malformed config value, expected list at: " + path);
		return cfg.getStringList(path);
	}

	protected static ChatMessageType getMessage(String path, ChatMessageType def) {
		return ChatMessageType.fromValue(getString(path, def.toString().toLowerCase()));
	}
}
