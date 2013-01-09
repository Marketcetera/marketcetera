package org.marketcetera.photon.internal.module.ui;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/* $License$ */

/**
 * Represents a tree of property keys and their corresponding values. The tree
 * can be navigated using {@link #getChildKeys(String)}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class PropertiesTree extends TreeMap<String, String> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Child keys are the keys that represent child nodes in the tree. A child
	 * key does not necessarily have to be in the map. For example, if a tree
	 * has keys "A.B" and "C", "A" and "C" are the child keys.
	 * 
	 * @return a sorted set of prefixes that represent child nodes of this tree,
	 *         may be empty
	 */
	public SortedSet<String> getChildKeys(String rootPrefix) {
		SortedMap<String, String> map = tailMap(rootPrefix);
		SortedSet<String> keys = new TreeSet<String>();
		for (String key : map.keySet()) {
			if (!key.startsWith(rootPrefix))
				break;
			int index = key.indexOf('.', rootPrefix.length() + 1);
			if (index != -1)
				keys.add(key.substring(0, index));
			else if (key.length() > rootPrefix.length())
				keys.add(key);
		}
		return keys;
	}
}