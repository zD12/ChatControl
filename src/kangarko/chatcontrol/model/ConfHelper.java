package kangarko.chatcontrol.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.MissingResourceException;

import org.apache.commons.lang.Validate;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;

// TODO Throw unknown values.
// TODO Config version.
// TODO Log newly added values.
public class ConfHelper {

	protected static YamlConfiguration cfg;
	protected static String FILE_NAME = null;
	protected static String HEADER = null;
	protected static File file;;
	private static String pathPrefix = null;

	public static void loadAll() throws Exception {
		// Order matters.
		Settings.load();
		SettingsConsole.load();
		SettingsRemap.load();
		Localization.load();
	}

	protected static void loadValues(Class<?> clazz) {
		Objects.requireNonNull(cfg, "YamlConfiguration is null!");
		Common.Debug("Setting up config: " + clazz.getSimpleName());

		try {
			// The class itself.
			invokeMethods(clazz);

			// All sub-classes.
			for (Class<?> subClazz : clazz.getDeclaredClasses()) {
				Common.Debug("Setting up config section: " + subClazz.getSimpleName());
				invokeMethods(subClazz);

				// And classes in sub-classes.
				for (Class<?> subSubClazz : subClazz.getDeclaredClasses()) {
					Common.Debug("Setting up config section: " + subClazz.getSimpleName() + "." + subSubClazz.getSimpleName());
					invokeMethods(subSubClazz);
				}
			}

			save();

		} catch (IOException | ReflectiveOperationException ex) {
			ex.printStackTrace();
		}
	}

	private static void invokeMethods(Class<?> clazz) throws ReflectiveOperationException {
		for (Method m : clazz.getDeclaredMethods()) {
			int modifier = m.getModifiers();

			if (Modifier.isPrivate(modifier) && Modifier.isStatic(modifier) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 0) {
				Validate.isTrue(m.getName().equals("init"), "Method must be called init() not " + m.getName());

				m.setAccessible(true);
				m.invoke(null);
				pathPrefix(null);
			}
		}
	}

	protected static void createFileAndLoad() throws Exception {
		File dataFolder = ChatControl.instance().getDataFolder();
		if (!dataFolder.exists())
			dataFolder.mkdirs();

		Objects.requireNonNull(FILE_NAME, "File name cannot be null!");
		file = new File(ChatControl.instance().getDataFolder(), FILE_NAME);

		if (!file.exists()) {
			try {
				ChatControl.instance().saveResource(FILE_NAME, false);
			} catch (IllegalArgumentException ex) {
				throw new MissingResourceException("Inbuilt resource %file not found!", FILE_NAME);
			}
		}

		cfg = new YamlConfiguration();
		cfg.load(file);
	}

	protected static void save() throws IOException {
		if (file == null)
			return;

		if (HEADER != null)
			cfg.options().header(HEADER);

		cfg.options().copyDefaults(true);
		cfg.save(file);
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

	protected static HashMap<String, List<String>> getValuesAndList(String path, HashMap<String, List<String>> def) {
		path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);

			for (String str : def.keySet())
				cfg.set(path + "." + str, def.get(str));
		}

		Validate.isTrue(cfg.isConfigurationSection(path), "Malformed config value, expected configuration section at: " + path);
		HashMap<String, List<String>> keys = new HashMap<>();

		for (String key : cfg.getConfigurationSection(path).getKeys(true)) {
			if (keys.containsKey(key))
				Common.Warn("Duplicate key: " + key + " in " + path);

			keys.put(key, getStringList(path + "." + key, Arrays.asList(""), false));
		}

		return keys;
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

	protected static List<String> getStringList(String path, List<String> def, boolean addPathPrefix) {
		if (addPathPrefix)
			path = addPathPrefix(path);

		if (!cfg.isSet(path)) {
			validate(path, def);
			cfg.set(path, def);
		}

		Validate.isTrue(cfg.isList(path), "Malformed config value, expected list at: " + path);
		return cfg.getStringList(path);
	}

	protected static List<String> getStringList(String path, List<String> def) {
		return getStringList(path, def, true);
	}

	protected static ChatMessage getMessage(String path, ChatMessage def) {
		return new ChatMessage(getString(path, def.getMessage()));
	}

	private static <T> void validate(String path, T def) {
		Common.Log("&eWriting in path \"" + path + "\" value: \"" + def + "\"");

		if (file == null)
			throw new RuntimeException("Inbuilt config doesn't contains " + def.getClass().getTypeName() + " at \"" + path + "\". Is it outdated?");
	}

	// --------------- Lazy helpers ---------------

	private static String addPathPrefix(String path) {
		return pathPrefix != null ? pathPrefix + "." + path : path;
	}

	protected static void pathPrefix(String pathPrefix) {
		ConfHelper.pathPrefix = pathPrefix;
	}

	// --------------- Classes ---------------

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
				throw new RuntimeException("Malformed type, use format: 'second, seconds' OR 'second, seconds, seconds' (genitive plular) (if your language has it)");

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

			Validate.isTrue(values.length == 3, "Malformed sound type, use format: 'sound' OR 'sound, volume, pitch'");
			this.sound = Sound.valueOf(values[0].toUpperCase());
			this.volume = Float.parseFloat(values[1]);
			this.pitch = Float.parseFloat(values[2]);
		}
	}
}
