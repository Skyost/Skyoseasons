package fr.skyost.seasons.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MonthLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	
	public final V getByIndex(final int index) {
		try {
			return new ArrayList<V>(this.values()).get(index);
		}
		catch(final ArrayIndexOutOfBoundsException ex) {}
		return null;
	}

}
