package com.aptana.rdt.core.gems;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
// TODO Move this class out to RDT core or something. It's useful outside of gems!
public class Version implements Comparable<Version> {
	
	private String raw;
	
	private List<String> versions;

	public Version(String raw) {
		this.raw = raw;
		if (!correctFormat(raw)) {
			throw new IllegalArgumentException("version string must be of format #.#.#(.#)?, instead it was: " + raw);
		}
		parse(raw);
	}
	
	public static boolean correctFormat(String raw) {
		return raw.trim().matches("\\d+\\.\\d+\\.\\d+(\\.\\d+)?");
	}
	
	private void parse(String raw) {
		StringTokenizer tokenizer = new StringTokenizer(raw, ".");
		versions = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			versions.add(tokenizer.nextToken());
		}
	}
	
	public int compareTo(Version o) {
		int diff = getMajor() - o.getMajor();
		if (diff != 0) return diff;
		diff = getMinor() - o.getMinor();
		if (diff != 0) return diff;
		diff = getBugfix() - o.getBugfix();
		if (diff != 0) return diff;
		return getRevision() - o.getRevision();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Version) {
			return isEqualTo((Version) obj);
		}
		return false;
	}

	public boolean isGreaterThan(Version other) {
		// check major component
		if (getMajor() > other.getMajor()) return true;
		if (getMajor() < other.getMajor()) return false;
		
		// check minor component
		if (getMinor() > other.getMinor()) return true;
		if (getMinor() < other.getMinor()) return false;
		
		// check bugfix component
		if (getBugfix() > other.getBugfix()) return true;
		if (getBugfix() < other.getBugfix()) return false;
		
		// check revision component
		if (getRevision() > other.getRevision()) return true;
		if (getRevision() < other.getRevision()) return false;
		
		return false; // everything matches		
	}
	
	public boolean isEqualTo(Version other) {
		if (getMajor() != other.getMajor()) return false;
		if (getMinor() != other.getMinor()) return false;
		if (getBugfix() != other.getBugfix()) return false;
		if (getRevision() != other.getRevision()) return false;
		return true; // everything matches		
	}
	
	public boolean isGreaterThanOrEqualTo(Version other) {
		return isGreaterThan(other) || isEqualTo(other);	
	}
	
	public boolean isLessThan(Version other) {
		// check major component
		if (getMajor() > other.getMajor()) return false;
		if (getMajor() < other.getMajor()) return true;
		
		// check minor component
		if (getMinor() > other.getMinor()) return false;
		if (getMinor() < other.getMinor()) return true;
		
		// check bugfix component
		if (getBugfix() > other.getBugfix()) return false;
		if (getBugfix() < other.getBugfix()) return true;
		
		// check revision component
		if (getRevision() > other.getRevision()) return false;
		if (getRevision() < other.getRevision()) return true;
		
		return false; // everything matches		
	}
	
	public boolean isLessThanOrEqualTo(Version other) {
		return isLessThan(other) || isEqualTo(other);	
	}
	
	public int getMajor() {
		if (versions.isEmpty()) return 0;
		return Integer.parseInt(versions.get(0));
	}
	
	public int getMinor() {
		if (versions.size() < 2) return 0;
		return Integer.parseInt(versions.get(1));
	}
	
	public int getBugfix() {
		if (versions.size() < 3) return 0;
		return Integer.parseInt(versions.get(2));
	}
	
	public int getRevision() {
		if (versions.size() < 4) return 0;
		return Integer.parseInt(versions.get(3));
	}
	
	public String toString() {
		return raw;
	}

	public boolean isGreaterThanOrEqualTo(String string) {
		return isGreaterThanOrEqualTo(new Version(string));
	}

	public boolean isLessThanOrEqualTo(String string) {
		return isLessThanOrEqualTo(new Version(string));
	}

}
