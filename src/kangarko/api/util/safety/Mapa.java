package kangarko.api.util.safety;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;

public class Mapa<Kluc, Hodnota> extends BezpecnostneVarovania {

	private HashMap<Kluc, Hodnota> mapa = new HashMap<>();

	public Hodnota put(Kluc key, Hodnota value) {
		if (containsKey(key))
			varovat("Map already contains key: " + key + " \\w value: " + value);

		return mapa.put(key, value);
	}

	public void putIfAbsent(Kluc key, Hodnota value) {
		if (!containsKey(key))
			put(key, value);
	}

	public void clear() {
		mapa.clear();
	}

	// Object -> String
	public boolean containsKey(Kluc key) {
		return mapa.containsKey(key);
	}


	@SuppressWarnings("unchecked")
	/** Beware: Only use when storing String as a Key! */
	public boolean containsKey(Player pl) {
		return containsKey((Kluc) pl.getName());
	}

	// Object -> String
	public Hodnota remove(Kluc key) {
		if (!containsKey(key))
			varovat("Cannot remove key: " + key + " since it isnt in the map!");

		return mapa.remove(key);
	}

	public int size() {
		return mapa.size();
	}

	public Hodnota get(Kluc key) {
		if(mapa.get(key) == null)
			varovat("Trying to get key: " + key + " but map doesnt contains him!");

		return mapa.get(key);
	}

	@SuppressWarnings("unchecked")
	/** Beware: Only use when storing String as a Key! */
	public Hodnota get(Player pl) {
		return get((Kluc) pl.getName());
	}

	public boolean isEmpty() {
		return mapa.isEmpty();
	}

	public Collection<Hodnota> values() {
		return mapa.values();
	}

	public Set<Kluc> keySet() {
		return mapa.keySet();
	}

	public void zobrazovavatVarovania() {
		varovat = true;
	}
}
