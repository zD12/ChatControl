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
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;

// TODO Throw unknown values.
// TODO Config version.
// TODO Log newly added values.
public class ConfHelper {

	protected static YamlConfiguration cfg;
	protected static File file;;
	private static String pathPrefix = null;

	public static void loadAll() {
		// Order matters.
		Settings.load();
		SettingsConsole.load();
		SettingsChat.load();
		Localization.load();
	}

	protected static void loadValues(Class<?> clazz) {
		Objects.requireNonNull(cfg, "YamlConfiguration is null!");
		Common.Log("Setting up config: " + clazz.getSimpleName());

		try {
			// Look in the class itself
			for (Method m : clazz.getDeclaredMethods()) {
				int modifier = m.getModifiers();

				if (Modifier.isPrivate(modifier) && Modifier.isStatic(modifier) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 0) {
					Validate.isTrue(m.getName().equals("init"), "Unknown method named " + m.getName());

					m.setAccessible(true);
					m.invoke(null);
					pathPrefix(null);
				}
			}

			// Look in all sub-classes
			for (Class<?> subClazz : clazz.getDeclaredClasses()) {
				Common.Log("Setting up config section: " + subClazz.getSimpleName());

				for (Method m : subClazz.getDeclaredMethods()) {
					int modifier = m.getModifiers();

					if (Modifier.isPrivate(modifier) && Modifier.isStatic(modifier) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 0) {
						Validate.isTrue(m.getName().equals("init"), "Unknown method named " + m.getName());

						m.setAccessible(true);
						m.invoke(null);
						pathPrefix(null);
					}
				}

				for (Class<?> subSubClazz : subClazz.getDeclaredClasses()) {
					Common.Log("Setting up config section: " + subClazz.getSimpleName() + "." + subSubClazz.getSimpleName());
					for (Method m : subSubClazz.getDeclaredMethods()) {
						int modifier = m.getModifiers();

						if (Modifier.isPrivate(modifier) && Modifier.isStatic(modifier) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 0) {
							Validate.isTrue(m.getName().equals("init"), "Unknown method named " + m.getName());

							m.setAccessible(true);
							m.invoke(null);
							pathPrefix(null);
						}
					}
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
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);
			cfg.set(path, def);
		}

		Validate.isTrue(cfg.isBoolean(path), "Malformed config value, expected boolean at: " + path);
		return cfg.getBoolean(path);
	}

	protected static String getString(String path, String def) {
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);
			cfg.set(path, def);
		}

		Validate.isTrue(cfg.isString(path), "Malformed config value, expected string at: " + path);
		return cfg.getString(path);
	}

	protected static int getInteger(String path, int def) {
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);
			cfg.set(path, def);
		}

		Validate.isTrue(cfg.isInt(path), "Malformed config value, expected integer at: " + path);
		return cfg.getInt(path);
	}

	protected static double getDouble(String path, double def) {
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);
			cfg.set(path, def);
		}

		Validate.isTrue(cfg.isDouble(path), "Malformed config value, expected double at: " + path);
		return cfg.getDouble(path);
	}

	protected static HashMap<String, String> getValuesAndKeys(String path, HashMap<String, String> def, boolean deep) {
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);

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
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);
			cfg.set(path, def);
		}

		Validate.isTrue(cfg.isList(path), "Malformed config value, expected list at: " + path);
		return cfg.getStringList(path);
	}

	protected static ChatMessage getMessage(String path, ChatMessage def) {
		path = addPathPrefix(path);
		return new ChatMessage(getString(path, def.getMessage()));
	}

	private static <T> void validate(String path, T def) {
		if (file == null)
			throw new RuntimeException("Inbuilt localization doesn't contains " + def.getClass().getTypeName() + " at \"" + path + "\". Is it outdated? If you want to get rid of error, create file " 
					+ Settings.LOCALIZATION_SUFFIX + " in plugin's folder and it will be filled with default values.");
	}

	private static String addPathPrefix(String path) {
		return pathPrefix != null ? pathPrefix + "." + path : path;
	}

	protected static void pathPrefix(String pathPrefix) {
		ConfHelper.pathPrefix = pathPrefix;
	}

	public static class ChatMessage {
		private final Type type;
		private final String message;

		public ChatMessage(String message) {
			this.type = Type.fromValue(message);
			this.message = message;
		}

		public ChatMessage(Type type) {
			Validate.isTrue(type != Type.CUSTOM, "Type cannot be custom.");

			this.type = type;
			this.message = type == Type.DEFAULT ? "default" : type == Type.HIDDEN ? "hidden" : null;
		}

		public Type getType() {
			return type;
		}

		public String getMessage() {
			Objects.requireNonNull(message, "Message cannot be null!");		
			return message;
		}

		public enum Type {
			DEFAULT,
			HIDDEN,
			CUSTOM;

			public static Type fromValue(String raw) {
				switch (raw.toLowerCase()) {
					case "default":
					case "def":
					case "vanilla":
						return DEFAULT;
					case "none":
					case "hide":
					case "hidden":
						return HIDDEN;
					default:
						return CUSTOM;
				}
			}
		}
	}

	public static class CasusHelper {
		private final String nominativPl; // 2 to 4 seconds (slovak case - sekundy)
		private final String akuzativSg; // 1 second (slovak case - sekundu) - not in english (LOL noobs)
		private final String genitivePl; // 5 and more seconds (slovak case - sekund)

		protected CasusHelper(String raw) {
			String[] values = raw.split(", ");

			if (values.length == 2) {
				this.akuzativSg = values[0];
				this.nominativPl = values[1];
				this.genitivePl = nominativPl;
				return;
			}

			if (values.length != 3)
				throw new RuntimeException("Malformed type, use format: second, seconds, seconds (if your language has it)");

			this.akuzativSg = values[0];
			this.nominativPl = values[1];
			this.genitivePl = values[2];
		}

		public String formatNumbers(long count) {
			if(count == 1) 
				return akuzativSg;
			if(count > 1 && count < 5) 
				return nominativPl;

			return genitivePl;
		}
	}

	public static class SoundHelper {
		public final Sound sound;
		public final float volume, pitch;

		public SoundHelper(String raw) {			
			String[] values = raw.split(", ");

			if (values.length == 1) {
				this.sound = Sound.valueOf(values[0].toUpperCase());
				this.volume = 1F;
				this.pitch = 1.5F;
				return;
			}

			Validate.isTrue(values.length == 3, "Malformed sound type, use format: bukkit_sound_name, float_volume, float_pitch");
			this.sound = Sound.valueOf(values[0].toUpperCase());
			this.volume = Float.parseFloat(values[1]);
			this.pitch = Float.parseFloat(values[2]);
		}
	}
}
