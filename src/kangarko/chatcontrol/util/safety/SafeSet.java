package kangarko.chatcontrol.util.safety;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SafeSet<Hodnota> implements Iterable<Hodnota> {

	private Set<Hodnota> list = new HashSet<>();
	
	public SafeSet() {}
	
	public SafeSet(Set<Hodnota> zbierka) {
		list = zbierka;
	}
	
	public boolean add(Hodnota e) {
		return list.add(e);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Hodnota o) {
		return list.contains(o);
	}
	
	@Override
	public Iterator<Hodnota> iterator() {
		return list.iterator();
	}

	public boolean remove(Hodnota o) {
		return list.remove(o);
	}

	public int size() {
		return list.size();
	}

	public Object[] toArray() {
		return list.toArray();
	}
	
	public void fillWith(Collection<? extends Hodnota> values) {
		list.addAll(values);
	}
	
	public List<Hodnota> getSetAsArray() {
		return new ArrayList<>(list);
	}
}
