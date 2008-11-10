package com.aptana.rdt.core.gems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;


public class LogicalGem extends Gem {
	
	private Collection<Gem> gems;

	private LogicalGem(Collection<Gem> gems, String name, String version, String description) {
		super(name, version, description);
		this.gems = gems;
	}
	
	public static LogicalGem create(Collection<Gem> gems) {
		if (gems == null || gems.isEmpty()) throw new IllegalArgumentException("Need a non-null, non-empty Collection of Gems");
		String name = null;
		String description = null;
		String version = "(";
		for (Gem gem : gems) {
			if (name == null) name = gem.getName();
			if (description == null) description = gem.getDescription();
			version += gem.getVersion() + ", ";
		}
		version = version.substring(0, version.length() - 2);
		version += ')';
		// XXX Need to take platform into account!!!!!!!!
		return new LogicalGem(gems, name, version, description);
	}

	public SortedSet<String> getVersions() {
		String raw = getVersion().substring(1, getVersion().length() - 1);
		SortedSet<String> version = new TreeSet<String>(new VersionComparator());
		StringTokenizer tokenizer = new StringTokenizer(raw, ",");
		while (tokenizer.hasMoreTokens()) {
			version.add(tokenizer.nextToken().trim());
		}
		return version;
	}
	
	private class VersionComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			String v1 = (String) o1;
			String v2 = (String) o2;
			
			List<Integer> v1Parts = getParts(v1);
			List<Integer> v2Parts = getParts(v2);
			int blah = Math.min(v1Parts.size(), v2Parts.size());
			for (int i = 0; i < blah; i++) {
				Integer one = v1Parts.get(i);
				Integer two = v2Parts.get(i);
				int result = one.compareTo(two);
				if (result != 0) return result;
			}
			// if parts sizes aren't equal, the one with more parts is newer.
			if (v1Parts.size() > v2Parts.size()) {
				return 1;
			} else if (v2Parts.size() > v1Parts.size()) {
				return -1;
			}
			return 0;
		}
		
		private List<Integer> getParts(String version) {
			StringTokenizer tokenizer = new StringTokenizer(version, ".");
			List<Integer> parts = new ArrayList<Integer>();
			while (tokenizer.hasMoreTokens()) {
				parts.add(Integer.parseInt(tokenizer.nextToken()));
			}
			return parts;
		}		
	}

	public Collection<Gem> getGems() {
		return gems;
	}
}
