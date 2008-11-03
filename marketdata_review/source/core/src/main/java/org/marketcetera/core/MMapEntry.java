package org.marketcetera.core;


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

    public boolean equals(Object obj)
    {
	if (!(obj instanceof Map.Entry)) {
	    return false;
	} else {
	    Map.Entry entry = (Map.Entry)obj;
	    return objEquals(this.getKey(), entry.getKey()) &&
		objEquals(this.getValue(), entry.getValue());
	}
    }

    private final boolean objEquals(Object obj1, Object obj2)
    {
	if (obj1 == null) return obj2==null;
	return obj1.equals(obj2);
    }
}
