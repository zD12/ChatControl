package kangarko.chatcontrol.util.safety;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class SafeMap<Kluc, Hodnota> {

	private HashMap<Kluc, Hodnota> mapa = new HashMap<>();

	public Hodnota put(Kluc key, Hodnota value) {		
		return mapa.put(key, value);
	}

	public void clear() {
		mapa.clear();
	}

	// Object -> String
	public boolean containsKey(Kluc key) {
		return mapa.containsKey(key);
	}

	// Object -> String
	public Hodnota remove(Kluc key) {		
		return mapa.remove(key);
	}
	
	public int size() {
		return mapa.size();
	}

	public Hodnota get(Kluc key) {
		return mapa.get(key);
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

	public String listValues() {
		String array = "";
		int i = 0;
		
		for(Kluc kluc : keySet()) {
			if (i == 0)
				array = kluc.toString();
			else
				array = array + ", " + kluc;
			i++;
		}
			
		return array;
	}
	
	public Set<java.util.Map.Entry<Kluc, Hodnota>> entrySet() {
		return mapa.entrySet();
	}
}
