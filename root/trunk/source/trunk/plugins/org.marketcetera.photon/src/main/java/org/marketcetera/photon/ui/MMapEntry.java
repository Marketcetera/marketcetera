package org.marketcetera.photon.ui;


import java.util.Map;

public class MMapEntry<K, V> implements Map.Entry<K, V> {
	K key;

	V value;

	public MMapEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public V setValue(V arg0) {
		V oldVal = value;
		value = arg0;
		return (oldVal);
	}
}