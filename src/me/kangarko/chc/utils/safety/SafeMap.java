package me.kangarko.chc.utils.safety;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;

public class SafeMap<Key, Value> extends SafetyWarnings {

	private HashMap<Key, Value> map = new HashMap<>();

	public Value put(Key key, Value value) {
		if (containsKey(key))
			warn("Map already contains key: " + key + " \\w value: " + value);

		return map.put(key, value);
	}

	public void putIfAbsent(Key key, Value value) {
		if (!containsKey(key))
			put(key, value);
	}

	public void clear() {
		map.clear();
	}

	// Object -> String
	public boolean containsKey(Key key) {
		return map.containsKey(key);
	}

	// Object -> String
	public Value remove(Key key) {
		if (!containsKey(key))
			warn("Cannot remove key: " + key + " since it isnt in the map!");

		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Value get(Key key) {
		if(map.get(key) == null)
			warn("Trying to get key: " + key + " but map doesnt contains him!");

		return map.get(key);
	}

	@SuppressWarnings("unchecked")
	/** Beware: Only use when storing String as a Key! */
	public Value get(Player pl) {
		return get((Key) pl.getName());
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Collection<Value> values() {
		return map.values();
	}

	public Set<Key> keySet() {
		return map.keySet();
	}

	public void displayWarnings() {
		warn = true;
	}
}
