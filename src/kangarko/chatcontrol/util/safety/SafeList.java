package kangarko.chatcontrol.util.safety;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SafeList<Hodnota> implements Iterable<Hodnota> {

	private List<Hodnota> list = new ArrayList<>();	
	
	public SafeList() {}
	
	public SafeList(List<Hodnota> zbierka) {
		list = zbierka;
	}
	
	public boolean add(Hodnota e) {
		return list.add(e);
	}

	/*public void add(int index, String element) {
		list.add(index, element);
	}

	public boolean addAll(Collection<? extends String> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends String> c) {
		return list.addAll(index, c);
	}*/

	public void clear() {
		list.clear();
	}

	// Object -> String
	public boolean contains(Hodnota o) {
		return list.contains(o);
	}

	public Hodnota get(int index) {
		return list.get(index);
	}

	/*public int indexOf(String o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}*/

	@Override
	public Iterator<Hodnota> iterator() {
		return list.iterator();
	}

	/*public int lastIndexOf(String o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<String> listIterator() {
		return list.listIterator();
	}

	public ListIterator<String> listIterator(int index) {
		return list.listIterator(index);
	}*/

	// Object -> String
	public boolean remove(Hodnota o) {
		return list.remove(o);
	}

	/*public String remove(int index) {
		return list.remove(index);
	}*/

	/*public boolean removeAll(Collection<String> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<String> c) {
		return list.retainAll(c);
	}*/

	public Hodnota set(int index, Hodnota element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	/*public List<String> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}*/
	
	public String listValues() {
		String array = "";
		
		for(int i = 0; i < list.size(); i++) {
			if (i == 0)
				array = String.valueOf(list.get(i));
			else
				array = array + ", " + list.get(i);
		}
			
		return array;
	}

	/*public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}*/
}
